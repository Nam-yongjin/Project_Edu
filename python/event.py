import pymysql
import pandas as pd

def get_connection():
    return pymysql.connect(
        host="localhost",
        user="root",
        password="12345",
        database="edudb",
        charset="utf8mb4"
    )

def event_category_stats():
    conn = get_connection()
    df = pd.read_sql("""
        SELECT category, COUNT(*) AS count
        FROM event_info
        GROUP BY category
    """, conn)
    conn.close()

    total = df["count"].sum()
    df["ratio"] = round(df["count"] / total * 100, 2)
    return df.to_dict(orient="records")