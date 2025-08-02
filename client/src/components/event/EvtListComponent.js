import { useEffect, useState } from "react";
import { getList } from "../../api/eventApi";
import { useNavigate } from "react-router-dom";

const EventListComponent = () => {
  const [events, setEvents] = useState([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const navigate = useNavigate();
  const host = "http://localhost:8090";

  useEffect(() => {
    getList(page).then((data) => {
      const list = Array.isArray(data.dtoList) ? data.dtoList : Object.values(data);
      setEvents(list);
      setTotalPages(data.totalPage || 1);
    });
  }, [page]);

  const formatDate = (dateStr) => {
    if (!dateStr) return "";
    const date = new Date(dateStr);
    return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, "0")}.${String(
      date.getDate()
    ).padStart(2, "0")}`;
  };

  const getApplyStatus = (event) => {
    const now = new Date();
    const start = new Date(event.applyStartPeriod);
    const end = new Date(event.applyEndPeriod);

    if (now < start) {
      return { text: "신청 시작 전", style: "bg-gray-400" };
    }
    if (now >= start && now <= end) {
      return { text: "신청 가능", style: "bg-blue-600" };
    }
    return { text: "신청 종료", style: "bg-gray-500" };
  };

  const handleCardClick = (eventNum) => {
    navigate(`/event/detail/${eventNum}`);
  };

  return (
    <div className="max-w-6xl mx-auto mt-10">
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
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
                {event.category === "TEACHER"
                  ? "교사"
                  : event.category === "STUDENT"
                  ? "학생"
                  : "일반인"}
              </div>

              {/* 대표 이미지 */}
              {event.filePath && event.filePath.match(/\.(jpg|jpeg|png|gif)$/i) ? (
                <img
                  src={`${host}/${event.filePath}`}
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
              <p className="text-sm text-gray-500 mb-3">
                모집인원: {event.maxCapacity}명
              </p>

              {/* 상태 표시 */}
              <div
                className={`text-white px-3 py-1 rounded font-semibold text-sm text-center w-full ${status.style}`}
              >
                {status.text}
              </div>
            </div>
          );
        })}
      </div>

      {/* 페이지네이션 */}
      <div className="mt-6 flex justify-center gap-2 text-blue-600 font-semibold">
        <button onClick={() => setPage((p) => Math.max(1, p - 1))}>{"<<"}</button>
        {[...Array(totalPages)].map((_, idx) => (
          <button
            key={idx + 1}
            onClick={() => setPage(idx + 1)}
            className={page === idx + 1 ? "underline text-blue-800" : "hover:text-blue-800"}
          >
            {idx + 1}
          </button>
        ))}
        <button onClick={() => setPage((p) => Math.min(totalPages, p + 1))}>{">>"}</button>
      </div>
    </div>
  );
};

export default EventListComponent;
