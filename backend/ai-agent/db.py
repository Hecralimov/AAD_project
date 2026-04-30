import os
from sqlalchemy import create_engine, inspect, text
from dotenv import load_dotenv

load_dotenv()

DATABASE_URL = os.getenv("DATABASE_URL")
print(f"DEBUG: DB URL: {DATABASE_URL}")

if not DATABASE_URL:
    raise ValueError("DATABASE_URL not found! Check your .env file placement.")

engine = create_engine(DATABASE_URL)

def get_schema() -> str:
    inspector = inspect(engine)
    schema_lines = []
    
    for table_name in inspector.get_table_names():
        columns = inspector.get_columns(table_name)
        col_strs = [f"{col['name']} {col['type']}" for col in columns]
        schema_lines.append(f"TABLE {table_name} ({', '.join(col_strs)});")
        
    return "\n".join(schema_lines)

def run_query(query: str) -> list[dict]:
    with engine.connect() as conn:
        result = conn.execute(text(query))
        return [dict(row) for row in result.mappings().all()]
