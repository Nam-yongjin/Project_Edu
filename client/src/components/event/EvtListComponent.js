import { useEffect, useState } from "react";
import { getList } from "../../api/eventApi";
import { useNavigate } from "react-router-dom";

const EventListComponent = () => {
  const [events, setEvents] = useState([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  const navigate = useNavigate();
  const host = "http://localhost:8090/view";

  // ✅ 행사 목록 조회
  useEffect(() => {
    getList(page).then((data) => {
      setEvents(data.content);       // 현재 페이지 데이터
      setTotalPages(data.totalPages); // 전체 페이지 수
    });
  }, [page]);

  // ✅ 날짜 형식 변환
  const formatDate = (dateStr) => {
    if (!dateStr) return "";
    const date = new Date(dateStr);
    return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, "0")}.${String(date.getDate()).padStart(2, "0")}`;
  };

  // ✅ 신청 상태 계산
  const getApplyStatus = (event) => {
    const now = new Date();
    const start = new Date(event.applyStartPeriod);
    const end = new Date(event.applyEndPeriod);

    if (now < start) return { text: "신청 시작 전", style: "bg-gray-400" };
    if (now <= end) return { text: "신청 가능", style: "bg-blue-600" };
    return { text: "신청 종료", style: "bg-gray-500" };
  };

  // ✅ 상세 페이지 이동
  const handleCardClick = (eventNum) => {
    navigate(`/event/detail/${eventNum}`);
  };

  // ✅ 페이지네이션 블록 계산
  const pageBlockSize = 10;
  const currentBlock = Math.floor((page - 1) / pageBlockSize);
  const blockStart = currentBlock * pageBlockSize + 1;
  const blockEnd = Math.min(blockStart + pageBlockSize - 1, totalPages);

  return (
    <div className="max-w-6xl mx-auto mt-10">
      {/* 카드 목록 */}
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-6">
        {events.map((event) => {
          const status = getApplyStatus(event);

          return (
            <div
              key={event.eventNum}
              className="border rounded-lg shadow p-4 hover:shadow-lg transition cursor-pointer"
              onClick={() => handleCardClick(event.eventNum)}
            >
              {/* 모집 대상 */}
              <div className="text-sm bg-blue-100 text-blue-700 inline-block px-2 py-1 rounded-full mb-2">
                {event.category === "TEACHER" ? "교사" : event.category === "STUDENT" ? "학생" : "일반인"}
              </div>

              {/* 대표 이미지 */}
              {event.mainImagePath && /\.(jpg|jpeg|png|gif)$/i.test(event.mainImagePath) ? (
                <img
                  src={`${host}/${event.mainImagePath}`}
                  alt="대표 이미지"
                  className="w-full h-40 object-cover rounded mb-2"
                />
              ) : (
                <div className="w-full h-40 bg-gray-200 flex items-center justify-center rounded mb-2 text-sm text-gray-500">
                  이미지 없음
                </div>
              )}

              {/* 제목 */}
              <h3 className="text-lg font-semibold mb-1 line-clamp-2">{event.eventName}</h3>

              {/* 신청 기간 */}
              <p className="text-sm text-gray-500 mb-1">
                신청기간: {formatDate(event.applyStartPeriod)} ~ {formatDate(event.applyEndPeriod)}
              </p>

              {/* 모집 인원 */}
              <p className="text-sm text-gray-500 mb-3">모집인원: {event.maxCapacity}명</p>

              {/* 신청 상태 */}
              <div className={`text-white px-3 py-1 rounded font-semibold text-sm text-center w-full ${status.style}`}>
                {status.text}
              </div>
            </div>
          );
        })}
      </div>

      {/* 페이지네이션 */}
      <div className="mt-6 flex justify-center gap-2 text-blue-600 font-semibold">
        {blockStart > 1 && (
          <button onClick={() => setPage(blockStart - 1)}>{"<"}</button>
        )}

        {Array.from({ length: blockEnd - blockStart + 1 }, (_, idx) => {
          const pageNum = blockStart + idx;
          return (
            <button
              key={pageNum}
              onClick={() => setPage(pageNum)}
              className={page === pageNum ? "underline text-blue-800" : "hover:text-blue-800"}
            >
              {pageNum}
            </button>
          );
        })}

        {blockEnd < totalPages && (
          <button onClick={() => setPage(blockEnd + 1)}>{">"}</button>
        )}
      </div>
    </div>
  );
};

export default EventListComponent;
