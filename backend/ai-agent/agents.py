from langchain_ollama import ChatOllama
from langchain_core.prompts import ChatPromptTemplate
from state import AgentState
from models import GuardrailsOutput, SQLOutput, AnalysisOutput, VizOutput
llm = ChatOllama(model="llama3", temperature=0)

def guardrails_agent(state: AgentState) -> dict:
    print("--- guardrails_agent ---")
    prompt = ChatPromptTemplate.from_messages([
        ("system", """You are a security guard for an AI system.
Your job is to intercept requests that violate authority rules.

AUTHORITY RULES:
1. User asks for "SQL Code": You must refuse. Answer: "I'm sorry, providing raw SQL code is out of my authority for security reasons."
2. User asks to see other users' private data: Refuse.
3. User asks about system internals: Refuse.

If the request is safe and relates to business analytics, pass it to the 'sql_agent'.
"""),
        ("user", "{question}")
    ])
    chain = prompt | llm.with_structured_output(GuardrailsOutput)
    response = chain.invoke({"question": state["question"]})
    
    return {
        "is_in_scope": response.is_in_scope,
        "final_answer": response.final_answer
    }

from db import get_schema, run_query

def sql_agent(state: AgentState) -> dict:
    print("--- sql_agent ---")
    schema_str = get_schema()
    system_prompt = """You are a MySQL expert for an E-commerce Analytics platform. 
Generate valid MySQL based on the user's role and specific business logic.

USER CONTEXT:
- Role: {role}
- Context ID: {context_id} (This is store_id for Corporate/STORE, and user_id for Individual/CUSTOMER)

BUSINESS LOGIC RULES:
1. RIVALS (Corporate): To find rivals, identify the store's top 5 sold products by volume. Then, find other stores selling those same products. Filter by stores with ratings >= 4.0.
2. REVENUE/PROFIT: Profit is calculated as SUM(quantity * unit_price).
3. LAST PURCHASE %: Find the revenue of the most recent order. Divide it by the SUM(revenue) of the last N orders (default N=10).
4. CATEGORY EXPENSES (Individual): Group orders by category and SUM the total price for the current month.
5. BEST SELLER (Individual): For a given category, find the store with the highest SUM(quantity) and highest average rating.

SECURITY:
- If Role is STORE, always filter by store_id = {context_id}.
- If Role is CUSTOMER, always filter by user_id = {context_id}.
- If the user asks for SQL code, DO NOT generate it in the query field. Use the Guardrail to block it.

Schema: {schema}
"""

    prompt = ChatPromptTemplate.from_messages([
        ("system", system_prompt),
        ("user", "{question}")
    ])
    chain = prompt | llm.with_structured_output(SQLOutput)
    response = chain.invoke({
        "schema": schema_str, 
        "role": state["role"],
        "context_id": state["context_id"],
        "question": state["question"]
    })
    
    return {"sql_query": response.sql_query}

def execute_sql(state: AgentState) -> dict:
    print("--- execute_sql ---")
    iteration = state.get("iteration_count", 0) + 1
    sql_query = state.get("sql_query")
    print(f"DEBUG: Executing SQL: {sql_query}")
    
    if not sql_query:
        return {
            "iteration_count": iteration,
            "query_result": None,
            "error": "No SQL query provided."
        }
        
    try:
        results = run_query(sql_query)
        print(f"DEBUG: Found {len(results)} rows.")
        return {
            "iteration_count": iteration,
            "query_result": results,
            "error": None
        }
    except Exception as e:
        print(f"DEBUG: SQL ERROR: {str(e)}")
        return {
            "iteration_count": iteration,
            "query_result": None,
            "error": str(e)
        }

def error_agent(state: AgentState) -> dict:
    print("--- error_agent ---")
    schema_str = get_schema()
    prompt = ChatPromptTemplate.from_messages([
        ("system", "You diagnose and fix SQL errors with expert knowledge of database schemas and query optimization.\nSchema:\n{schema}"),
        ("user", "Previous query: {sql_query}\nError: {error}\nQuestion: {question}\nFix the query.")
    ])
    chain = prompt | llm.with_structured_output(SQLOutput)
    response = chain.invoke({
        "schema": schema_str, 
        "sql_query": state.get("sql_query", ""), 
        "error": state.get("error", ""),
        "question": state["question"]
    })
    
    return {"sql_query": response.sql_query, "error": None}

def analysis_agent(state: AgentState) -> dict:
    print("--- analysis_agent ---")
    prompt = ChatPromptTemplate.from_messages([
        ("system", """You are an Expert Business Analyst. 
Based on the provided Query Results, answer the user's question and explain the calculation clearly.

SCENARIO GUIDELINES:
- Rivals: If other stores appear in the data, list them as rivals. If the list is empty, say "You don't have any rivals."
- Calculations: When asked "How did you calculate this?", look at the data. 
    * If it's revenue, say: "I summed the total price (quantity x unit price) for your items."
    * If it's rivals, say: "I looked up stores selling your top items and compared their ratings."
    * If it's percentages, say: "I divided the value of your last purchase by the total value of the period."
- Totals: For "Last purchase content", list the item names and the total grand_total.

Always be concise. If no data is found, politely state that no records match the criteria.
"""),
        ("user", "Question: {question}\nQuery Result: {query_result}")
    ])
    chain = prompt | llm.with_structured_output(AnalysisOutput)
    response = chain.invoke({
        "question": state["question"],
        "query_result": str(state.get("query_result", []))
    })
    
    return {"final_answer": response.final_answer}

def visualization_agent(state: AgentState) -> dict:
    print("--- visualization_agent ---")
    prompt = ChatPromptTemplate.from_messages([
        ("system", "You are a data visualization expert. Generate clean, executable Plotly code without markdown formatting."),
        ("user", "Question: {question}\nQuery Result: {query_result}")
    ])
    chain = prompt | llm.with_structured_output(VizOutput)
    response = chain.invoke({
        "question": state["question"],
        "query_result": str(state.get("query_result", []))
    })
    
    return {"visualization_code": response.visualization_code}
