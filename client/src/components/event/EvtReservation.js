import { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { getReservationList, cancelReservation } from "../../api/eventApi";
import useLogin from "../../hooks/useLogin";

const ReservationListComponent = () => {
  const [reservations, setReservations] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const pageSize = 5;

  const navigate = useNavigate();
  const host = "http://localhost:8090/view";

  const { loginState } = useLogin();

  // 👉 예약 리스트 불러오기
  const fetchReservationList = useCallback(async () => {
    console.log("📡 fetchReservationList() 호출됨");

    try {
      const data = await getReservationList({ page, size: pageSize });
      console.log("✅ 예약 리스트 응답:", data);
      setReservations(data.content);
      setTotalPages(data.totalPages);
    } catch (error) {
      console.error("❌ 예약 이력 불러오기 실패", error);
    }
  }, [page]);

  // 👉 예약 취소
  const handleCancelReservation = async (evtRevNum) => {
    if (!window.confirm("예약을 취소하시겠습니까?")) return;

    try {
      console.log("⛔ 예약 취소 요청:", evtRevNum);
      await cancelReservation(evtRevNum);
      alert("예약이 취소되었습니다.");
      fetchReservationList(); // 새로고침
    } catch (error) {
      console.error("❌ 예약 취소 실패", error);
      alert("예약 취소 중 오류가 발생했습니다.");
    }
  };

  // 👉 날짜 포맷 함수
  const formatDate = (dateStr) => {
    const date = new Date(dateStr);
    return `${date.getFullYear()}.${date.getMonth() + 1}.${date.getDate()}`;
  };

  // 👉 useEffect: 페이지 바뀌거나 마운트 시
  useEffect(() => {
    console.log("🔍 useEffect 실행됨");
    console.log("✅ 로그인 상태:", loginState);
    fetchReservationList();
  }, [fetchReservationList, loginState]);

  return (
    <div className="max-w-4xl mx-auto mt-10 px-4">
      <h2 className="text-2xl font-bold mb-6 text-center">예약 이력 조회</h2>

      <div className="space-y-6">
        {reservations.length === 0 ? (
          <p className="text-center text-gray-500">예약 이력이 없습니다.</p>
        ) : (
          reservations.map((item) => (
            <div key={item.evtRevNum} className="flex items-start border-b pb-4 gap-4">
              <div className="w-24 h-24 bg-gray-200 flex items-center justify-center text-sm text-gray-500 flex-shrink-0">
                {item.mainImagePath && /\.(jpg|jpeg|png|gif)$/i.test(item.mainImagePath) ? (
                  <img
                    src={`${host}/${item.mainImagePath}`}
                    alt="행사 이미지"
                    className="w-full h-full object-cover"
                  />
                ) : (
                  "이미지 없음"
                )}
              </div>

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
                    {item.revState === "APPROVED"
                      ? "신청 완료"
                      : item.revState === "CANCEL"
                      ? "신청 취소"
                      : "대기 중"}
                  </span>
                </p>
              </div>

              <div className="flex flex-col gap-2">
                <button
                  onClick={() => handleCancelReservation(item.evtRevNum)}
                  className="bg-red-500 text-white px-3 py-1 rounded text-sm"
                >
                  취소하기
                </button>
                <button
                  onClick={() => navigate(`/event/detail/${item.eventNum}`)}
                  className="border border-gray-400 text-sm px-3 py-1 rounded"
                >
                  상세보기
                </button>
              </div>
            </div>
          ))
        )}
      </div>

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
            className={`${page === idx ? "underline text-blue-800" : "hover:text-blue-800"}`}
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