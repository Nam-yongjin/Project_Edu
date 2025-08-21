import { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { getReservationList, cancelReservation } from "../../api/eventApi";
import PageComponent from "../common/PageComponent";

const EvtReservation = () => {
  const [reservations, setReservations] = useState([]);
  const [page, setPage] = useState(0);            // 0-based (PageComponent와 호환)
  const [totalPages, setTotalPages] = useState(1);
  const pageSize = 5;

  const navigate = useNavigate();
  const host = "http://localhost:8090/view";

  // 예약 리스트 조회
  const fetchReservations = useCallback(
    async (targetPage = page) => {
      try {
        const data = await getReservationList({
          page: targetPage,
          size: pageSize,
          sort: "applyAt,DESC",
        });
        setReservations(data.content || []);
        setTotalPages(data.totalPages || 1);
        setPage(targetPage);
      } catch (error) {
        console.error("예약 이력 불러오기 실패:", error);
        if (error.response?.status === 401) {
          alert("로그인이 필요합니다.");
          navigate("/login");
        } else {
          alert("예약 정보를 불러오지 못했습니다.");
        }
      }
    },
    [page, pageSize, navigate]
  );

  useEffect(() => {
    fetchReservations(page);
  }, [page, fetchReservations]);

  // 예약 취소
  const handleCancelReservation = async (evtRevNum) => {
    if (!window.confirm("예약을 취소하시겠습니까?")) return;
    try {
      await cancelReservation(evtRevNum);
      alert("예약이 취소되었습니다.");
      fetchReservations(0);
    } catch (error) {
      console.error("예약 취소 실패:", error);
      if (error.response?.data) {
        alert(error.response.data);
      } else {
        alert("예약 취소 중 오류가 발생했습니다.");
      }
    }
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return "없음";
    const date = new Date(dateStr);
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, "0");
    const d = String(date.getDate()).padStart(2, "0");
    const hh = String(date.getHours()).padStart(2, "0");
    const mm = String(date.getMinutes()).padStart(2, "0");
    return `${y}.${m}.${d} ${hh}:${mm}`;
  };

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

  const getCancelButtonLabel = (item) => {
    const now = new Date();
    const start = new Date(item.eventStartPeriod);
    const end = new Date(item.eventEndPeriod);

    if (item.revState === "CANCEL") return "취소완료";
    if (now >= start && now <= end) return "프로그램 진행 중";
    if (now > end) return "프로그램 완료";
    return "취소하기";
  };

  const isCancelable = (item) => {
    const now = new Date();
    const start = new Date(item.eventStartPeriod);
    return item.revState !== "CANCEL" && now < start; // 시작 전만 취소 가능
  };

  return (
    <div className="max-w-screen-xl mx-auto my-10">
      <div className="min-blank page-shadow bg-white rounded-lg p-8">
        {/* 헤더 */}
        <h2 className="newText-3xl font-bold text-center mb-8">프로그램 신청 내역</h2>

        {/* 리스트 */}
        <div className="space-y-5">
          {reservations.length === 0 ? (
            <p className="newText-base text-center text-gray-500 py-10">
              예약 이력이 없습니다.
            </p>
          ) : (
            reservations.map((item) => {
              const cancelable = isCancelable(item);
              return (
                <div
                  key={item.evtRevNum}
                  className="page-border rounded-md border border-gray-100 p-4 flex items-start gap-4"
                >
                  {/* 썸네일 */}
                  <div className="w-24 h-24 rounded-md overflow-hidden bg-gray-100 flex-shrink-0">
                    {item.mainImagePath && /\.(jpg|jpeg|png|gif|webp)$/i.test(item.mainImagePath) ? (
                      <img
                        src={`${host}/${item.mainImagePath}`}
                        alt="프로그램 이미지"
                        className="w-full h-full object-cover"
                      />
                    ) : (
                      <div className="w-full h-full flex items-center justify-center newText-sm text-gray-500">
                        이미지 없음
                      </div>
                    )}
                  </div>

                  {/* 내용 */}
                  <div className="flex-1">
                    <div className="flex items-start justify-between">
                      <div className="newText-base">
                        <p className="newText-lg font-semibold mb-1">
                          {item.eventName}
                        </p>
                        <p className="text-gray-600 mb-0.5">
                          <span className="font-semibold">프로그램 일정:</span>{" "}
                          {formatDate(item.eventStartPeriod)} ~ {formatDate(item.eventEndPeriod)}
                        </p>
                        <p className="text-gray-600 mb-0.5">
                          <span className="font-semibold">예약 일시:</span>{" "}
                          {formatDate(item.applyAt)}
                        </p>
                        <p className="text-gray-600">
                          <span className="font-semibold">상태:</span>{" "}
                          <span
                            className={`inline-block px-2 py-0.5 rounded newText-sm text-white ${
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

                      {/* 우측 버튼 */}
                      <div className="flex flex-col gap-2 items-end">
                        <button
                          onClick={() => handleCancelReservation(item.evtRevNum)}
                          disabled={!cancelable}
                          className={`newText-sm rounded ${
                            cancelable
                              ? "nagative-button"
                              : "normal-button cursor-not-allowed !bg-gray-300 !text-gray-700 !border !border-gray-400"
                          }`}
                          title={getCancelButtonLabel(item)}
                        >
                          {getCancelButtonLabel(item)}
                        </button>

                        <button
                          onClick={() => navigate(`/event/detail/${item.eventNum}`)}
                          className="normal-button newText-sm"
                        >
                          상세보기
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              );
            })
          )}
        </div>

        {/* 페이지네이션 (공용 컴포넌트 / 0-based 인덱스) */}
        <div className="mt-8 flex justify-center">
          <PageComponent
            totalPages={totalPages}
            current={page}
            setCurrent={setPage}
          />
        </div>
      </div>
    </div>
  );
};

export default EvtReservation;