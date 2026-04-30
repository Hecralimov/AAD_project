from fastapi import FastAPI
from pydantic import BaseModel
from typing import Literal
import uvicorn
from graph import app_graph
from state import AgentState

app = FastAPI(title="Text2SQL Agent API")

class ChatRequest(BaseModel):
    message: str
    role: Literal['INDIVIDUAL', 'CORPORATE', 'ADMIN']
    context_id: str

@app.post("/api/chat/ask")
async def chat_ask(request: ChatRequest):
    initial_state: AgentState = {
        "question": request.message,
        "sql_query": None,
        "query_result": None,
        "error": None,
        "final_answer": None,
        "visualization_code": None,
        "is_in_scope": True,
        "iteration_count": 0,
        "role": request.role,
        "context_id": request.context_id
    }
    
    final_state = app_graph.invoke(initial_state)
    
    return {
        "final_answer": final_state.get("final_answer"),
        "visualization_code": final_state.get("visualization_code")
    }

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
