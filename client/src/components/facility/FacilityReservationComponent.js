import { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { getMyReservations, cancelReservation } from "../../api/facilityApi";
import PageComponent from "../../components/common/PageComponent";

/* ==================== 이미지 유틸 ==================== */
const PLACEHOLDER = "/placeholder.svg";
const host = "http://localhost:8090/view";
const buildImageUrl = (p) => {
  const val = typeof p === "string" ? p : p?.imageUrl;
  if (!val) return PLACEHOLDER;
  if (/^https?:\/\//i.test(val)) return val;
  let path = String(val).trim();
  path = path.replace(/^https?:\/\/[^/]+/i, "");
  path = path.replace(/^\/?view\/?/, "/");
  if (!path.startsWith("/")) path = `/${path}`;
  return `${host}${path}`.replace(/([^:]\/)\/+/g, "$1");
};

/* ==================== 날짜/시간 유틸 ==================== */
const formatDateYmd = (v) => {
  if (!v) return "-";
  const d = new Date(v);
  if (Number.isNaN(d.getTime())) return v;
  const yyyy = d.getFullYear();
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  const dd = String(d.getDate()).padStart(2, "0");
  return `${yyyy}.${mm}.${dd}`;
};
const hhmm = (v) => {
  if (!v) return "-";
  const m = String(v).match(/(\d{1,2}):(\d{2})/);
  return m ? `${m[1].padStart(2, "0")}:${m[2]}` : v;
};
const getStatusChip = (state) => {
  switch (state) {
    case "APPROVED":   return { label: "승인 완료", cls: "bg-green-500" };
    case "CANCEL":
    case "CANCELLED":  return { label: "취소 완료", cls: "bg-red-500" };
    case "WAITING":
    default:           return { label: "승인 대기", cls: "bg-gray-500" };
  }
};

/* ==================== PageComponent 브리지 ==================== */
// PageComponent 구현의 prop 시그니처 차이를 흡수하기 위한 어댑터 
function PageBridge({ page, totalPages, blockSize = 10, onChange }) {
  const tp = Math.max(1, Number(totalPages) || 1);
  const current = Math.min(tp, Math.max(1, page + 1)); // 1-based
  const currentBlock = Math.floor((current - 1) / blockSize);
  const start = currentBlock * blockSize + 1;
  const end = Math.min(start + blockSize - 1, tp);
  const prev = start > 1;
  const next = end < tp;
  const pageNums = Array.from({ length: Math.max(0, end - start + 1) }, (_, i) => start + i);
  const movePage = (p1) => {
    const p = Math.min(tp, Math.max(1, Number(p1)));
    onChange(p); // 1-based 전달
  };
  return (
    <PageComponent
      // DTO 스타일
      pageResponse={{ start, end, prev, next, pageNums, current, totalPages: tp }}
      // 개별 키 스타일
      start={start} end={end} prev={prev} next={next}
      pageNums={pageNums} pageList={pageNums} current={current}
      // 단순 숫자형 스타일
      currentPage={current} totalPages={tp} blockSize={blockSize}
      // 콜백 호환
      movePage={movePage} onPageChange={movePage} onChange={movePage}
    />
  );
}

const FacilityReservationComponent = () => {
  const [reservations, setReservations] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 5;
  const navigate = useNavigate();

  // 데이터 조회
  const fetchReservations = useCallback(
    async (targetPage) => {
      try {
        const data = await getMyReservations({
          page: targetPage,
          size: pageSize,
          sort: "reserveAt,DESC",
        });
        setReservations(data?.content || []);
        setTotalPages(Math.max(1, data?.totalPages || 1));
        setTotalElements(Number.isFinite(data?.totalElements) ? data.totalElements : (data?.content?.length || 0));
        setPage(targetPage);
      } catch (error) {
        console.error("공간 예약 이력 불러오기 실패:", error);
        if (error.response?.status === 401) {
          alert("로그인이 필요합니다.");
          navigate("/login");
        } else {
          alert(error.response?.data?.message || "예약 정보를 불러오지 못했습니다.");
        }
      }
    },
    [pageSize, navigate]
  );

  useEffect(() => {
    fetchReservations(page);
  }, [page, fetchReservations]);

  // 예약 취소
  const handleCancelReservation = async (reserveId) => {
    if (!window.confirm("예약을 취소하시겠습니까?")) return;
    try {
      await cancelReservation(reserveId);
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

  // 버튼 활성화
  const isCancelable = (item) => {
    const now = new Date();
    const start = new Date(`${item.facDate}T${item.startTime}`);
    return item.state !== "CANCEL" && item.state !== "CANCELLED" && now < start;
  };

  // 버튼 라벨
  const getCancelButtonLabel = (item) => {
    const now = new Date();
    const start = new Date(`${item.facDate}T${item.startTime}`);
    if (item.state === "CANCEL" || item.state === "CANCELLED") return "취소 완료";
    if (now >= start) return "이용 중/종료";
    return "취소하기";
  };

  return (
    <div className="max-w-screen-xl mx-auto my-10">
      <div className="min-blank">
        {/* 타이틀 */}
        <h2 className="newText-3xl font-bold mb-6 text-center">공간 예약 내역</h2>

        {/* 목록 */}
        <div className="space-y-6">
          {reservations.length === 0 ? (
            <p className="newText-base text-center text-gray-500">예약 이력이 없습니다.</p>
          ) : (
            reservations.map((item) => {
              const chip = getStatusChip(item.state);
              const thumb = buildImageUrl(item.mainImageUrl);
              return (
                <div key={item.reserveId} className="page-shadow rounded-2xl border bg-white p-4">
                  <div className="flex items-start gap-4">
                    {/* 썸네일 */}
                    <div className="w-28 h-28 bg-gray-200 rounded overflow-hidden flex-shrink-0">
                      <img
                        src={thumb}
                        alt="공간 이미지"
                        className="w-full h-full object-cover"
                        onError={(e) => { e.currentTarget.src = PLACEHOLDER; }}
                      />
                    </div>

                    {/* 정보 */}
                    <div className="flex-1 newText-base">
                      <p className="mb-1"><span className="newText-sm text-gray-600">공간명</span> <b>{item.facName}</b></p>
                      <p className="mb-1"><span className="newText-sm text-gray-600">이용일</span> {formatDateYmd(item.facDate)}</p>
                      <p className="mb-1"><span className="newText-sm text-gray-600">이용 시간</span> {hhmm(item.startTime)} ~ {hhmm(item.endTime)}</p>
                      <p className="mb-1">
                        <span className="newText-sm text-gray-600">상태</span>{" "}
                        <span className={`inline-block px-2 py-0.5 rounded text-white newText-xs ${chip.cls}`}>
                          {chip.label}
                        </span>
                      </p>
                    </div>

                    {/* 버튼 영역 */}
                    <div className="flex flex-col gap-2">
                      <button
                        onClick={() => handleCancelReservation(item.reserveId)}
                        disabled={!isCancelable(item)}
                        className={`nagative-button newText-base px-3 py-1 rounded disabled:opacity-50`}
                      >
                        {getCancelButtonLabel(item)}
                      </button>

                      <button
                        onClick={() => navigate(`/facility/detail/${item.facRevNum}`)}
                        className="normal-button newText-base px-3 py-1 rounded"
                      >
                        상세보기
                      </button>
                    </div>
                  </div>
                </div>
              );
            })
          )}
        </div>

        {/* 요약 및 페이지네이션 */}
        <div className="mt-6 flex items-center justify-between">
          <div className="newText-sm text-gray-600">
            총 <b>{totalElements}</b>건
            <span className="ml-2 text-gray-400">({page + 1}/{Math.max(1, totalPages)})</span>
          </div>
        </div>

        <div className="mt-3 flex justify-center">
          <PageBridge
            page={page}
            totalPages={totalPages}
            blockSize={10}
            onChange={(next1Based) => {
              const nextZero = Math.max(0, Math.min(totalPages - 1, next1Based - 1));
              setPage(nextZero);
            }}
          />
        </div>
      </div>
    </div>
  );
};

export default FacilityReservationComponent;