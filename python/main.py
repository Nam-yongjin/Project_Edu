from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from datetime import datetime
from database import get_connection
from visit import visitors
from member import member_role_stats
from event import event_category_stats
from facility import popular_facility_times
from demonstration import demonstration_registration_stats, demonstration_reserve_stats

app = FastAPI()


app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 방문자 수 카운트
@app.get("/api/recordVisit")
async def record_visit(request: Request):
    conn = None

    # DB에 기록
    try:
        ip = request.client.host

        conn = get_connection()
        cursor = conn.cursor()
        cursor.execute("""
            INSERT INTO visit (ip_address, visited_at)
            VALUES (%s, %s)
        """, (ip, datetime.now()))
        conn.commit()
        return
    except Exception as e:
        print("방문자 기록 실패:", e)
        return
    finally:
        if conn:
            conn.close()

# 일일 방문자, 총합 방문자
@app.get("/api/admin/visitors")
async def visitor_stats():
    return visitors()

# 회원 유형별 가입자 수, 비율
@app.get("/api/admin/memberRole")
async def memberRole_stats():
    return member_role_stats()

# 프로그램 카테고리별 등록 수, 비율
@app.get("/api/admin/eventCategory")
async def eventCategory_stats():
    return event_category_stats()

# 공간 대여 인기 시간대
@app.get("/api/admin/popular_facTimes")
async def popular_facTimes():
    return popular_facility_times()

# 실증 카테고리별 등록 수, 비율
@app.get("/api/admin/demRegCategory")
async def demonstrationRegistration_stats():
    return demonstration_registration_stats()

# 실증 카테고리별 대여 수, 비율
@app.get("/api/admin/demRevCategory")
async def demonstrationReserve_stats():
    return demonstration_reserve_stats()