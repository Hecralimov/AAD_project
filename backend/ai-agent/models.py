from pydantic import BaseModel, Field
from typing import Optional

class GuardrailsOutput(BaseModel):
    is_in_scope: bool = Field(description="Whether the question is relevant to e-commerce data analysis.")
    final_answer: Optional[str] = Field(default=None, description="A friendly greeting or rejection message if the question is out of scope or just a greeting.")

class SQLOutput(BaseModel):
    sql_query: str = Field(description="The valid SQL query generated.")

class AnalysisOutput(BaseModel):
    final_answer: str = Field(description="The natural language explanation of the query results.")

class VizOutput(BaseModel):
    visualization_code: str = Field(description="The clean, executable Plotly code without markdown formatting.")
