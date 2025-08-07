import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getReservationList, cancelReservation } from "../../api/eventApi";

const ReservationListComponent = () => {
  const [reservations, setReservations] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const pageSize = 5;

  const navigate = useNavigate();
  const host = "http://localhost:8090/view";

  // 📡 예약 리스트 조회 함수
  const fetchReservations = async (targetPage = page) => {
    console.log("📡 예약 리스트 조회 요청");
    try {
      const data = await getReservationList({ page: targetPage, size: pageSize });
      console.log("✅ 예약 리스트 응답:", data);
      setReservations(data.content);
      setTotalPages(data.totalPages);
      setPage(targetPage);
    } catch (error) {
      console.error("❌ 예약 이력 불러오기 실패:", error);
      if (error.response?.status === 401) {
        alert("로그인이 필요합니다.");
        navigate("/login");
      } else {
        alert("예약 정보를 불러오지 못했습니다.");
      }
    }
  };

  // 컴포넌트 mount 및 page 변경 시 호출
  useEffect(() => {
    fetchReservations();
  }, [page]);

  // ❌ 예약 취소 처리
  const handleCancelReservation = async (evtRevNum) => {
    if (!window.confirm("예약을 취소하시겠습니까?")) return;

    try {
      console.log("⛔ 예약 취소 요청:", evtRevNum);
      await cancelReservation(evtRevNum);
      alert("예약이 취소되었습니다.");
      fetchReservations(0); // 첫 페이지부터 다시 조회
    } catch (error) {
      console.error("❌ 예약 취소 실패:", error);
      alert("예약 취소 중 오류가 발생했습니다.");
    }
  };

  const formatDate = (dateStr) => {
  if (!dateStr) return "없음";
  const date = new Date(dateStr);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  return `${year}.${month}.${day} ${hours}:${minutes}`;
};

  // 🏷 상태 텍스트 반환 함수
  const getStatusLabel = (state) => {
    switch (state) {
      case "APPROVED":
        return "신청 완료";
      case "CANCEL":
        return "취소 완료";
      default:
        return "대기 중";
    }
  };

  return (
    <div className="max-w-4xl mx-auto mt-10 px-4">
      <h2 className="text-2xl font-bold mb-6 text-center">예약 이력 조회</h2>

      <div className="space-y-6">
        {reservations.length === 0 ? (
          <p className="text-center text-gray-500">예약 이력이 없습니다.</p>
        ) : (
          reservations.map((item) => (
            <div
              key={item.evtRevNum}
              className="flex items-start border-b pb-4 gap-4"
            >
              {/* 행사 이미지 */}
              <div className="w-24 h-24 bg-gray-200 flex items-center justify-center text-sm text-gray-500 flex-shrink-0">
                {item.mainImagePath &&
                /\.(jpg|jpeg|png|gif)$/i.test(item.mainImagePath) ? (
                  <img
                    src={`${host}/${item.mainImagePath}`}
                    alt="행사 이미지"
                    className="w-full h-full object-cover"
                  />
                ) : (
                  "이미지 없음"
                )}
              </div>

              {/* 행사 정보 */}
              <div className="flex-1 text-sm">
                <p className="mb-1">
                  <strong>행사이름:</strong> {item.eventName}
                </p>
                <p className="mb-1">
                  <strong>행사 일정:</strong> {formatDate(item.eventStartPeriod)} ~{" "}
                  {formatDate(item.eventEndPeriod)}
                </p>
                <p className="mb-1">
                  <strong>예약 일시:</strong> {formatDate(item.applyAt)}
                </p>
                <p className="mb-1">
                  <strong>상태:</strong>{" "}
                  <span
                    className={`inline-block px-2 py-0.5 rounded text-white text-xs ${
                      item.revState === "APPROVED"
                        ? "bg-green-500"
                        : item.revState === "CANCEL"
                        ? "bg-red-500"
                        : "bg-gray-400"
                    }`}
                  >
                    {getStatusLabel(item.revState)}
                  </span>
                </p>
              </div>

              {/* 버튼 영역 */}
              <div className="flex flex-col gap-2">
                <button
                  onClick={() => handleCancelReservation(item.evtRevNum)}
                  disabled={item.revState === "CANCEL"}
                  className={`px-3 py-1 rounded text-sm
                    ${
                      item.revState === "CANCEL"
                        ? "bg-gray-300 text-gray-600 cursor-not-allowed"
                        : "bg-red-500 text-white hover:bg-red-600"
                    }`}
                >
                  {item.revState === "CANCEL" ? "취소완료" : "취소하기"}
                </button>

                <button
                  onClick={() => navigate(`/event/detail/${item.eventNum}`)}
                  className="border border-gray-400 text-sm px-3 py-1 rounded hover:bg-gray-100"
                >
                  상세보기
                </button>
              </div>
            </div>
          ))
        )}
      </div>

      {/* 페이지네이션 */}
      <div className="mt-6 flex justify-center gap-2 text-blue-600 font-semibold">
        {page > 0 && (
          <button onClick={() => setPage(page - 1)} className="hover:text-blue-800">
            {"<"}
          </button>
        )}
        {Array.from({ length: totalPages }, (_, idx) => (
          <button
            key={idx}
            onClick={() => setPage(idx)}
            className={`${
              page === idx ? "underline text-blue-800" : "hover:text-blue-800"
            }`}
          >
            {idx + 1}
          </button>
        ))}
        {page < totalPages - 1 && (
          <button onClick={() => setPage(page + 1)} className="hover:text-blue-800">
            {">"}
          </button>
        )}
      </div>
    </div>
  );
};

export default ReservationListComponent;