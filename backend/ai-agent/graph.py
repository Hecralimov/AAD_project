from langgraph.graph import StateGraph, END
from typing import Literal
from state import AgentState
from agents import guardrails_agent, sql_agent, execute_sql, error_agent, analysis_agent, visualization_agent

def decide_graph_need(state: AgentState) -> Literal["visualization_agent", "__end__"]:
    print("--- decide_graph_need ---")
    question = state.get("question", "").lower()
    keywords = ["chart", "graph", "trend", "visualize"]
    if any(keyword in question for keyword in keywords):
        return "visualization_agent"
    return "__end__"

def should_execute_sql(state: AgentState) -> Literal["sql_agent", "__end__"]:
    if state.get("is_in_scope", True):
        return "sql_agent"
    return "__end__"

def route_after_sql_execution(state: AgentState) -> Literal["error_agent", "analysis_agent"]:
    error = state.get("error")
    iteration = state.get("iteration_count", 0)
    
    if error and iteration < 3:
        return "error_agent"
    return "analysis_agent"

# Build the graph
workflow = StateGraph(AgentState)

workflow.add_node("guardrails_agent", guardrails_agent)
workflow.add_node("sql_agent", sql_agent)
workflow.add_node("execute_sql", execute_sql)
workflow.add_node("error_agent", error_agent)
workflow.add_node("analysis_agent", analysis_agent)
workflow.add_node("visualization_agent", visualization_agent)

workflow.set_entry_point("guardrails_agent")

workflow.add_conditional_edges(
    "guardrails_agent",
    should_execute_sql,
    {
        "sql_agent": "sql_agent",
        "__end__": END
    }
)

workflow.add_edge("sql_agent", "execute_sql")

workflow.add_conditional_edges(
    "execute_sql",
    route_after_sql_execution,
    {
        "error_agent": "error_agent",
        "analysis_agent": "analysis_agent"
    }
)

workflow.add_edge("error_agent", "execute_sql")

workflow.add_conditional_edges(
    "analysis_agent",
    decide_graph_need,
    {
        "visualization_agent": "visualization_agent",
        "__end__": END
    }
)

workflow.add_edge("visualization_agent", END)

app_graph = workflow.compile()
