import pandas as pd
from datetime import date
from database import get_connection

def event_category_stats():
    conn = get_connection()
    df = pd.read_sql("""
        SELECT category, SUM(curr_capacity) AS curr_capacity, COUNT(*) AS count
        FROM event_info
        GROUP BY category
    """, conn)
    conn.close()

    total = int(df["count"].sum())
    df["ratio"] = round(df["count"] / total * 100, 2)
    return df.to_dict(orient="records") # JSON 형태로 변환