import pandas as pd
from database import get_connection


def popular_facility_times():
    conn = get_connection()
    df = pd.read_sql("""
        SELECT 
            start_time, 
            end_time
        FROM facility_reserve
    """, conn)
    conn.close()

    # 시간대별 카운트 초기화 (0~23)
    hours_count = {h: 0 for h in range(0, 24)}

    for _, row in df.iterrows():
        start_hour = int(row["start_time"].total_seconds() // 3600)
        end_hour = int(row["end_time"].total_seconds() // 3600)

        # 예약 시간대는 "start ~ end" → [start, end) (end 제외)
        for h in range(start_hour, end_hour):
            if 0 <= h < 24:
                hours_count[h] += 1

    # ✅ 겹치는 부분만 남기려면: count > 1인 시간대만 유지
    result_list = []
    for h in range(0, 24):
        count = hours_count[h]
        label = f"{h:02d}:00 ~ {h+1:02d}:00"
        result_list.append({"hour": h, "count": count, "label": label})

    return result_list
