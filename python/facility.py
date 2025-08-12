from fastapi import FastAPI
import pymysql
import pandas as pd

app = FastAPI()

def get_connection():
    return pymysql.connect(
        host="localhost",
        user="root", 
        password="12345",
        database="mydb",
        charset="utf8mb4"
    )

@app.get("/api/admin/facility/popular-times")
def popular_facility_times():
    conn = get_connection()
    
    # 시간대별 예약 건수 집계
    df = pd.read_sql("""
        SELECT 
            start_time, 
            end_time, 
            COUNT(*) AS count
        FROM facility_reserve
        GROUP BY start_time, end_time
        ORDER BY count DESC
    """, conn)
    conn.close()

    return df.to_dict(orient="records") # JSON 형태로 변환
