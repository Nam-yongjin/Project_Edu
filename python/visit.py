from database import get_connection
import pandas as pd
from datetime import date


def visitors():
    conn = get_connection()
    df = pd.read_sql("""
        SELECT DATE(visited_at) AS date, COUNT(*) AS count
        FROM visit
        GROUP BY DATE(visited_at)
        ORDER BY date ASC
    """, conn)
    conn.close()

    total_visitors = int(df["count"].sum())

    today_str = date.today().strftime("%Y-%m-%d")
    today_row = df[df["date"].astype(str) == today_str]
    daily_visitors = int(today_row["count"].values[0]) if not today_row.empty else 0
    
    return {
        "total_visitors": total_visitors,
        "daily_visitors": daily_visitors,
        "trend": df.to_dict(orient="records") # JSON 형태로 변환
    }