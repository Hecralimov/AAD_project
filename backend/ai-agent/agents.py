from langchain_ollama import ChatOllama
from langchain_core.prompts import ChatPromptTemplate
from state import AgentState
from models import GuardrailsOutput, SQLOutput, AnalysisOutput, VizOutput
llm = ChatOllama(model="llama3", temperature=0)

def guardrails_agent(state: AgentState) -> dict:
    print("--- guardrails_agent ---")
    prompt = ChatPromptTemplate.from_messages([
        ("system", "You are a strict guardrails system that filters questions to ensure they are relevant to e-commerce data analysis.\nIf the user sends a greeting, set is_in_scope=False and provide a friendly welcome in final_answer. If out-of-scope, set is_in_scope=False and provide a rejection message. Otherwise, is_in_scope=True."),
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
    system_prompt = """You are a MySQL expert. 
Generate a valid query based ONLY on the tables provided in the schema.
If the user is an ADMIN, ignore all WHERE clauses related to user_id or store_id.

Schema: {schema}
User Role: {role}
Context ID: {context_id}

CRITICAL: Double-check the table names in the schema before generating.
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
        ("system", "You are a helpful data analyst that explains database query results in natural language with clear insights."),
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
