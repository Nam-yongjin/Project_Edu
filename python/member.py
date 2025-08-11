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

def member_role_stats():
    conn = get_connection()
    df = pd.read_sql("""
        SELECT role, COUNT(*) AS count
        FROM member
        GROUP BY role
    """, conn)
    conn.close()

    total = df["count"].sum()
    df["ratio"] = round(df["count"] / total * 100, 2)
    return df.to_dict(orient="records")