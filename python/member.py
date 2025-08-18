from database import get_connection
import pandas as pd

def member_role_stats():
    conn = get_connection()
    df = pd.read_sql("""
        SELECT role, COUNT(*) AS count
        FROM member
        GROUP BY role
    """, conn)
    conn.close()

    total = int(df["count"].sum())
    df["ratio"] = round(df["count"] / total * 100, 2)
    return {
        "total_member": total,
        "trend": df.to_dict(orient="records") # JSON 형태로 변환
    }