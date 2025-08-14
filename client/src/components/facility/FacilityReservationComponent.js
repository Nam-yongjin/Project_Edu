// src/components/facility/FacilityReservationListPaged.jsx
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getMyReservations } from "../../api/facilityApi";

/* ==================== 이미지 유틸 ==================== */
const PLACEHOLDER = "/placeholder.svg";
const host = "http://localhost:8090/view";

const buildImageUrl = (p) => {
  const val = typeof p === "string" ? p : p?.imageUrl;
  if (!val) return PLACEHOLDER;
  if (/^https?:\/\//i.test(val)) return val;

  let path = String(val).trim();
  path = path.replace(/^https?:\/\/[^/]+/i, ""); // 혹시 포함된 도메인 제거
  path = path.replace(/^\/?view\/?/, "/");       // 중복 view 제거
  if (!path.startsWith("/")) path = `/${path}`;  // 선행 슬래시 보장
  return `${host}${path}`.replace(/([^:]\/)\/+/g, "$1"); // 이중 슬래시 정리
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
  // 서버 enum에 CANCEL 또는 CANCELLED 둘 다 대비
  switch (state) {
    case "APPROVED":   return { label: "승인 완료", cls: "bg-green-500" };
    case "CANCEL":
    case "CANCELLED":  return { label: "취소 완료", cls: "bg-red-500" };
    case "WAITING":
    default:           return { label: "승인 대기", cls: "bg-gray-500" };
  }
};

const FacilityReservationComponent = () => {
  const [reservations, setReservations] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const pageSize = 5;

  const navigate = useNavigate();

  const fetchReservations = async (targetPage = page) => {
    try {
      const data = await getMyReservations({
        page: targetPage,
        size: pageSize,
        sort: "reserveAt,DESC", // 가장 최근 예약이 위
      });
      setReservations(data?.content || []);
      setTotalPages(data?.totalPages || 1);
      setPage(targetPage);
    } catch (error) {
      console.error("시설 예약 이력 불러오기 실패:", error);
      if (error.response?.status === 401) {
        alert("로그인이 필요합니다.");
        navigate("/login");
      } else {
        alert(error.response?.data?.message || "예약 정보를 불러오지 못했습니다.");
      }
    }
  };

  useEffect(() => {
    fetchReservations();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  return (
    <div className="max-w-4xl mx-auto mt-10 px-4">
      <h2 className="text-2xl font-bold mb-6 text-center">시설 예약 이력</h2>

      <div className="space-y-6">
        {reservations.length === 0 ? (
          <p className="text-center text-gray-500">예약 이력이 없습니다.</p>
        ) : (
          reservations.map((item) => {
            const chip = getStatusChip(item.state);
            const thumb = buildImageUrl(item.mainImageUrl); // ✅ 백엔드에서 내려준 mainImageUrl 사용
            return (
              <div key={item.reserveId} className="flex items-start border-b pb-4 gap-4">
                {/* 썸네일 */}
                <div className="w-24 h-24 bg-gray-200 rounded overflow-hidden flex-shrink-0">
                  <img
                    src={thumb}
                    alt="시설 이미지"
                    className="w-full h-full object-cover"
                    onError={(e) => { e.currentTarget.src = PLACEHOLDER; }}
                  />
                </div>

                {/* 정보 */}
                <div className="flex-1 text-sm">
                  <p className="mb-1">
                    <strong>시설명:</strong> {item.facName}
                  </p>
                  <p className="mb-1">
                    <strong>이용일:</strong> {formatDateYmd(item.facDate)}
                  </p>
                  <p className="mb-1">
                    <strong>이용 시간:</strong> {hhmm(item.startTime)} ~ {hhmm(item.endTime)}
                  </p>
                  <p className="mb-1">
                    <strong>상태:</strong>{" "}
                    <span className={`inline-block px-2 py-0.5 rounded text-white text-xs ${chip.cls}`}>
                      {chip.label}
                    </span>
                  </p>
                </div>

                {/* 버튼 */}
                <div className="flex flex-col gap-2">
                  <button
                    onClick={() => navigate(`/facility/detail/${item.facRevNum}`)}
                    className="border border-gray-400 text-sm px-3 py-1 rounded hover:bg-gray-100"
                  >
                    상세보기
                  </button>
                </div>
              </div>
            );
          })
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

export default FacilityReservationComponent;