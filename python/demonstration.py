import pandas as pd
from datetime import date
from database import get_connection

def demonstration_registration_stats():
    conn = get_connection()
    df = pd.read_sql("""
        SELECT d.category, COUNT(dr.dem_reg_num) AS count
        FROM demonstration_registration dr
        JOIN demonstration d ON dr.dem_num = d.dem_num
        WHERE dr.state LIKE 'ACCEPT'
        GROUP BY d.category
    """, conn)
    conn.close()

    total = int(df["count"].sum())
    df["ratio"] = round(df["count"] / total * 100, 2)
    
    return {
        "total_demReg": total,
        "trend": df.to_dict(orient="records") # JSON 형태로 변환
    }
    
def demonstration_reserve_stats():
    conn = get_connection()
    df = pd.read_sql("""
        SELECT d.category, COUNT(dr.dem_rev_num) AS count
        FROM demonstration_reserve dr
        JOIN demonstration d ON dr.dem_num = d.dem_num
        WHERE dr.state LIKE 'ACCEPT'
        GROUP BY d.category
    """, conn)
    conn.close()

    total = int(df["count"].sum())
    df["ratio"] = round(df["count"] / total * 100, 2)
    
    return {
        "total_demRev": total,
        "trend": df.to_dict(orient="records") # JSON 형태로 변환
    }