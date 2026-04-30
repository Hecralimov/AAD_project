from typing import TypedDict, Optional, List

class AgentState(TypedDict):
    question: str
    sql_query: Optional[str]
    query_result: Optional[List[dict]]
    error: Optional[str]
    final_answer: Optional[str]
    visualization_code: Optional[str]
    is_in_scope: bool
    iteration_count: int
    role: str
    context_id: str
