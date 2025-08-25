import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import {
  getAdminReservations,
  updateReservationState,
  adminCancelReservation,
} from "../../api/facilityApi";
import PageComponent from "../../components/common/PageComponent";

/* ==================== 유틸 ==================== */
// 2자리 패딩
const pad2 = (n) => String(n).padStart(2, "0");

// Date -> 'YYYY-MM-DD'
const toYmd = (d) =>
  `${d.getFullYear()}-${pad2(d.getMonth() + 1)}-${pad2(d.getDate())}`;

// 'YYYY-MM-DD' or Date/DateTime string -> 'YYYY.MM.DD'
const formatYmdDots = (v) => {
  if (!v) return "-";
  const m = String(v).match(/^(\d{4})-(\d{2})-(\d{2})/);
  if (m) return `${m[1]}.${m[2]}.${m[3]}`;
  const d = new Date(String(v).replace(" ", "T"));
  if (Number.isNaN(d.getTime())) return String(v);
  return `${d.getFullYear()}.${pad2(d.getMonth() + 1)}.${pad2(d.getDate())}`;
};

// Date/DateTime string -> 'YYYY.MM.DD HH:mm'
const formatDateTime = (v) => {
  if (!v) return "-";
  const d = new Date(String(v).replace(" ", "T"));
  if (Number.isNaN(d.getTime())) return String(v);
  return `${d.getFullYear()}.${pad2(d.getMonth() + 1)}.${pad2(d.getDate())} ${pad2(d.getHours())}:${pad2(d.getMinutes())}`;
};

// 'H:mm' or 'HH:mm' -> 'HH:mm'
const hhmm = (v) => {
  if (!v) return "-";
  const m = String(v).match(/(\d{1,2}):(\d{2})/);
  return m ? `${pad2(m[1])}:${m[2]}` : String(v);
};

// 이용일이 오늘 이전인가
const isPastUseDate = (ymd) => {
  if (!ymd) return false;
  const today = toYmd(new Date());
  return String(ymd) < today;
};

// 상태 칩 계산
// - APPROVED 이면서 이용일이 지났으면 "완료" 라벨
const chipOf = (state, facDate) => {
  if (state === "APPROVED" && isPastUseDate(facDate)) {
    return { label: "완료", cls: "bg-slate-500" };
  }
  switch (state) {
    case "APPROVED":
      return { label: "승인 완료", cls: "bg-green-500" };
    case "REJECTED":
      return { label: "거절", cls: "bg-red-500" };
    case "CANCELLED":
      return { label: "취소", cls: "bg-red-500" };
    case "WAITING":
    default:
      return { label: "승인 대기", cls: "bg-gray-500" };
  }
};

/* ==================== 기간 빠른 선택 ==================== */
const todayRange = () => {
  const t = new Date();
  const ymd = toYmd(t);
  return { from: ymd, to: ymd };
};
const thisWeekRange = () => {
  const t = new Date();
  const day = t.getDay(); // 0(일)~6(토)
  const diffToMon = (day + 6) % 7; // 월요일까지 뒤로 이동
  const mon = new Date(t);
  mon.setDate(t.getDate() - diffToMon);
  const sun = new Date(mon);
  sun.setDate(mon.getDate() + 6);
  return { from: toYmd(mon), to: toYmd(sun) };
};
const thisMonthRange = () => {
  const t = new Date();
  const first = new Date(t.getFullYear(), t.getMonth(), 1);
  const last = new Date(t.getFullYear(), t.getMonth() + 1, 0);
  return { from: toYmd(first), to: toYmd(last) };
};

/* ==================== PageComponent 브리지 ==================== */
// PageComponent는 (totalPages, current[0-based], setCurrent[0-based])만 기대함.
// 상위(AdminFacilityReservations)는 onChange에서 1-based를 기대하므로 여기서 변환 처리.
function PageBridge({ page, pageSize, totalCount, onChange }) {
  const totalPages = Math.max(1, Math.ceil(totalCount / pageSize));
  const safeCurrent = Math.max(0, Math.min(totalPages - 1, Number(page) || 0));

  const setCurrent = (nextZero) => {
    const nz = Math.max(0, Math.min(totalPages - 1, Number(nextZero)));
    onChange(nz + 1); // 상위 onChange는 1-based를 기대
  };

  return (
    <PageComponent
      totalPages={totalPages}
      current={safeCurrent}   // 0-based
      setCurrent={setCurrent} // 0-based 인덱스로 호출
    />
  );
}

/* ==================== 메인 컴포넌트 ==================== */
export default function AdminFacilityReservations() {
  const navigate = useNavigate();

  // ✅ 권한
  const isAdmin = useSelector((s) => s?.loginState?.role === "ADMIN");

  // 필터 상태
  const [state, setState] = useState(""); // "", "WAITING", "APPROVED", "REJECTED"
  const [from, setFrom] = useState(thisMonthRange().from);
  const [to, setTo] = useState(thisMonthRange().to);

  // 데이터 상태
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // 액션 로딩(중복 방지)
  const [savingId, setSavingId] = useState(null); // reserveId 또는 null

  // 클라이언트 페이징/정렬
  const [page, setPage] = useState(0); // 0-based
  const [pageSize, setPageSize] = useState(10);
  const [sortKey, setSortKey] = useState("reserveAt");
  const [sortDir, setSortDir] = useState("DESC");

  // ===== 권한 체크: ADMIN이 아니면 접근 불가 =====
  useEffect(() => {
    if (isAdmin === false) {
      alert("권한이 없습니다.");
      navigate("/facility/list");
    }
  }, [isAdmin, navigate]);

  // 데이터 로드
  const fetchData = async () => {
    if (
      from &&
      to &&
      new Date(`${from}T00:00:00`) > new Date(`${to}T00:00:00`)
    ) {
      setError("시작일이 종료일보다 클 수 없습니다.");
      return;
    }
    setLoading(true);
    setError("");
    try {
      const data = await getAdminReservations({
        state: state || undefined,
        from: from || undefined,
        to: to || undefined,
      });
      setRows(Array.isArray(data) ? data : []);
      setPage(0); // 조회 시 첫 페이지로 이동
    } catch (e) {
      const msg =
        e?.response?.data?.message || e?.message || "목록을 불러오지 못했습니다.";
      setError(msg);
      if (e?.response?.status === 401) {
        alert("관리자 로그인이 필요합니다.");
        navigate("/login");
      }
    } finally {
      setLoading(false);
    }
  };

  // 초기 로드: 관리자일 때만 실행
  useEffect(() => {
    if (isAdmin) fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAdmin]);

  // 정렬
  const sorted = useMemo(() => {
    const arr = [...rows];
    const key = sortKey;
    arr.sort((a, b) => {
      let va = a?.[key];
      let vb = b?.[key];
      if (key === "reserveAt") {
        const da = va ? new Date(String(va).replace(" ", "T")).getTime() : 0;
        const db = vb ? new Date(String(vb).replace(" ", "T")).getTime() : 0;
        return da - db;
      }
      if (key === "facDate") {
        const da = va ? new Date(`${va}T00:00:00`).getTime() : 0;
        const db = vb ? new Date(`${vb}T00:00:00`).getTime() : 0;
        return da - db;
      }
      if (typeof va === "number" && typeof vb === "number") return va - vb;
      return String(va ?? "").localeCompare(String(vb ?? ""));
    });
    return sortDir === "DESC" ? arr.reverse() : arr;
  }, [rows, sortKey, sortDir]);

  // 페이징 적용된 데이터
  const paged = useMemo(() => {
    const start = page * pageSize;
    return sorted.slice(start, start + pageSize);
  }, [sorted, page, pageSize]);

  const totalCount = sorted.length;
  const totalPages = Math.max(1, Math.ceil(totalCount / pageSize));

  // 빠른 범위 선택
  const quick = {
    today: () => {
      const r = todayRange();
      setFrom(r.from);
      setTo(r.to);
      fetchData();
    },
    week: () => {
      const r = thisWeekRange();
      setFrom(r.from);
      setTo(r.to);
      fetchData();
    },
    month: () => {
      const r = thisMonthRange();
      setFrom(r.from);
      setTo(r.to);
      fetchData();
    },
    all: () => {
      setFrom("");
      setTo("");
      fetchData();
    },
  };

  // 정렬 변경
  const onChangeSort = (k) => {
    if (sortKey === k) setSortDir((d) => (d === "ASC" ? "DESC" : "ASC"));
    else {
      setSortKey(k);
      setSortDir("DESC");
    }
  };

  // ====== 액션 공통: 서버 상태 변경 후 재조회 ======
  const doUpdate = async ({ reserveId, nextState }) => {
    try {
      setSavingId(reserveId);
      await updateReservationState({ reserveId, state: nextState });
      await fetchData();
    } catch (e) {
      alert(
        e?.response?.data?.message || e?.message || "상태 변경에 실패했습니다."
      );
    } finally {
      setSavingId(null);
    }
  };

  // 승인
  const handleApprove = (r) => {
    if (!r?.reserveId) return;
    if (!window.confirm(`예약 ${r.reserveId}을(를) 승인하시겠습니까?`)) return;
    doUpdate({ reserveId: r.reserveId, nextState: "APPROVED" });
  };

  // 거절
  const handleReject = (r) => {
    if (!r?.reserveId) return;
    if (!window.confirm(`예약 ${r.reserveId}을(를) 거절하시겠습니까?`)) return;
    doUpdate({ reserveId: r.reserveId, nextState: "REJECTED" });
  };

  // 관리자 강제취소
  const handleAdminCancel = (r) => {
    if (!r?.reserveId) return;
    if (!window.confirm(`예약 ${r.reserveId}을(를) 강제 취소하시겠습니까?`))
      return;
    setSavingId(r.reserveId);
    adminCancelReservation({ reserveId: r.reserveId, requesterId: r.memId })
      .then(() => fetchData())
      .catch((e) => {
        alert(e?.response?.data?.message || e?.message || "취소에 실패했습니다.");
      })
      .finally(() => setSavingId(null));
  };

  // 액션 렌더링
  const renderActions = (r) => {
    const disabled = savingId === r.reserveId;

    if (r.state === "WAITING") {
      return (
        <div className="flex items-center justify-center gap-2">
          <button
            disabled={disabled}
            onClick={() => handleApprove(r)}
            className={`green-button newText-base px-3 py-1 rounded ${disabled ? "opacity-60 cursor-not-allowed" : ""}`}
          >
            승인
          </button>
          <button
            disabled={disabled}
            onClick={() => handleReject(r)}
            className={`nagative-button newText-base px-3 py-1 rounded ${disabled ? "opacity-60 cursor-not-allowed" : ""}`}
          >
            거절
          </button>
        </div>
      );
    }

    if (r.state === "APPROVED") {
      if (isPastUseDate(r.facDate)) {
        return <span className="newText-sm text-gray-400">-</span>;
      }
      return (
        <div className="flex items-center justify-center gap-2">
          <button
            disabled={disabled}
            onClick={() => handleAdminCancel(r)}
            className={`nagative-button newText-base px-3 py-1 rounded ${disabled ? "opacity-60 cursor-not-allowed" : ""}`}
          >
            강제취소
          </button>
        </div>
      );
    }

    return <span className="newText-sm text-gray-400">-</span>;
  };

  // 비관리자라면(리다이렉트 직전) 화면 렌더를 막아 깜빡임 방지
  if (isAdmin === false) return null;

  /* ==================== 렌더 ==================== */
  return (
    // 최상단 레이아웃: 고정 클래스 사용
    <div className="max-w-screen-xl mx-auto my-10">
      {/* 좌우 여백: 고정 클래스 사용 */}
      <div className="min-blank">
        <h2 className="newText-3xl font-bold mb-6 text-center">
          공간 예약 현황 (관리자)
        </h2>

        {/* 필터 박스 */}
        <div className="page-shadow rounded-2xl border p-4 mb-4 bg-white">
          <div className="grid grid-cols-1 md:grid-cols-5 gap-3 items-end">
            <div className="flex flex-col">
              <label className="newText-xs text-gray-600 mb-1">상태</label>
              <select
                value={state}
                onChange={(e) => setState(e.target.value)}
                className="input-focus newText-base rounded px-3 py-2"
              >
                <option value="">전체</option>
                <option value="WAITING">승인 대기</option>
                <option value="APPROVED">승인 완료</option>
                <option value="REJECTED">거절</option>
                {/* 필요 시 <option value="CANCELLED">취소</option> 추가 */}
              </select>
            </div>
            <div className="flex flex-col">
              <label className="newText-xs text-gray-600 mb-1">시작일</label>
              <input
                type="date"
                value={from}
                onChange={(e) => setFrom(e.target.value)}
                onKeyDown={(e) => e.key === "Enter" && fetchData()}
                className="input-focus newText-base rounded px-3 py-2"
              />
            </div>
            <div className="flex flex-col">
              <label className="newText-xs text-gray-600 mb-1">종료일</label>
              <input
                type="date"
                value={to}
                onChange={(e) => setTo(e.target.value)}
                onKeyDown={(e) => e.key === "Enter" && fetchData()}
                className="input-focus newText-base rounded px-3 py-2"
              />
            </div>
            <div className="flex gap-2 md:col-span-2">
              <button
                onClick={fetchData}
                className="positive-button newText-base flex-1 rounded px-3 py-2"
              >
                조회
              </button>
              <button
                onClick={quick.today}
                className="normal-button newText-base px-3 py-2"
              >
                오늘
              </button>
              <button
                onClick={quick.week}
                className="normal-button newText-base px-3 py-2"
              >
                이번주
              </button>
              <button
                onClick={quick.month}
                className="normal-button newText-base px-3 py-2"
              >
                이번달
              </button>
              <button
                onClick={quick.all}
                className="normal-button newText-base px-3 py-2"
              >
                전체
              </button>
            </div>
          </div>
        </div>

        {/* 옵션 바 */}
        <div className="flex flex-wrap items-center justify-between gap-3 mb-3">
          <div className="newText-sm text-gray-600">
            총 <b>{totalCount}</b>건
            <span className="ml-2 text-gray-400">
              ({page + 1}/{totalPages})
            </span>
          </div>
          <div className="flex items-center gap-2">
            <label className="newText-sm text-gray-600">정렬</label>
            <select
              value={`${sortKey}:${sortDir}`}
              onChange={(e) => {
                const [k, d] = e.target.value.split(":");
                setSortKey(k);
                setSortDir(d);
              }}
              className="input-focus newText-base rounded px-2 py-1"
            >
              <option value="reserveAt:DESC">신청일 ↓</option>
              <option value="reserveAt:ASC">신청일 ↑</option>
              <option value="facDate:DESC">이용일 ↓</option>
              <option value="facDate:ASC">이용일 ↑</option>
              <option value="facName:ASC">공간명 A→Z</option>
              <option value="facName:DESC">공간명 Z→A</option>
            </select>
            <label className="newText-sm text-gray-600 ml-3">페이지 크기</label>
            <select
              value={pageSize}
              onChange={(e) => {
                setPageSize(Number(e.target.value));
                setPage(0);
              }}
              className="input-focus newText-base rounded px-2 py-1"
            >
              <option value={10}>10</option>
              <option value={20}>20</option>
              <option value={50}>50</option>
            </select>
          </div>
        </div>

        {/* 데이터 표 - 전체 가운데 정렬 */}
        <div className="page-shadow overflow-x-auto rounded-2xl border bg-white">
          <table className="min-w-full newText-sm">
            <thead>
              <tr className="bg-gray-50 text-center">
                <th
                  className="px-4 py-3 text-center cursor-pointer"
                  onClick={() => onChangeSort("reserveId")}
                >
                  예약ID
                </th>
                <th
                  className="px-4 py-3 text-center cursor-pointer"
                  onClick={() => onChangeSort("facName")}
                >
                  공간명
                </th>
                <th className="px-2 py-3 text-center">공간번호</th>
                <th
                  className="px-4 py-3 text-center cursor-pointer"
                  onClick={() => onChangeSort("facDate")}
                >
                  이용일
                </th>
                <th className="px-4 py-3 text-center">이용 시간</th>
                <th className="px-4 py-3 text-center">신청자</th>
                <th className="px-4 py-3 text-center">상태</th>
                <th
                  className="px-4 py-3 text-center cursor-pointer"
                  onClick={() => onChangeSort("reserveAt")}
                >
                  신청일
                </th>
                <th className="px-4 py-3 text-center">액션</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td
                    colSpan={9}
                    className="px-4 py-8 text-center newText-base text-gray-500"
                  >
                    불러오는 중...
                  </td>
                </tr>
              ) : error ? (
                <tr>
                  <td
                    colSpan={9}
                    className="px-4 py-8 text-center newText-base text-red-600"
                  >
                    {error}
                  </td>
                </tr>
              ) : paged.length === 0 ? (
                <tr>
                  <td
                    colSpan={9}
                    className="px-4 py-8 text-center newText-base text-gray-500"
                  >
                    데이터가 없습니다.
                  </td>
                </tr>
              ) : (
                paged.map((r) => {
                  const chip = chipOf(r.state, r.facDate); // 이용일 반영
                  const mem = r.memId || "-";
                  return (
                    <tr key={r.reserveId} className="border-t text-center">
                      <td className="px-4 py-3 whitespace-nowrap text-center">
                        {r.reserveId}
                      </td>
                      <td className="px-4 py-3 whitespace-nowrap text-center">
                        {r.facName}
                      </td>
                      <td className="px-2 py-3 whitespace-nowrap text-gray-600 text-center">
                        {r.facRevNum}
                      </td>
                      <td className="px-4 py-3 whitespace-nowrap text-center">
                        {formatYmdDots(r.facDate)}
                      </td>
                      <td className="px-4 py-3 whitespace-nowrap text-center">
                        {hhmm(r.startTime)} ~ {hhmm(r.endTime)}
                      </td>
                      <td className="px-4 py-3 whitespace-nowrap text-center">
                        {mem}
                      </td>
                      <td className="px-4 py-3 whitespace-nowrap text-center">
                        <span
                          className={`text-white newText-xs px-2 py-0.5 rounded ${chip.cls}`}
                        >
                          {chip.label}
                        </span>
                      </td>
                      <td className="px-4 py-3 whitespace-nowrap text-gray-600 text-center">
                        {formatDateTime(r.reserveAt)}
                      </td>
                      <td className="px-4 py-3 whitespace-nowrap text-center">
                        <div className="flex items-center justify-center gap-2">
                          {renderActions(r)}
                          <button
                            onClick={() =>
                              navigate(`/facility/detail/${r.facRevNum}`)
                            }
                            className="normal-button newText-base px-3 py-1 rounded"
                          >
                            상세보기
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>

        {/* 페이지네이션 - PageComponent 브리지 사용 */}
        <div className="mt-4 flex justify-center">
          <PageBridge
            page={page} // 0-based
            pageSize={pageSize}
            totalCount={totalCount}
            onChange={(next1Based) => {
              // next1Based는 1-based, 내부 상태는 0-based로 변환
              const nextZero = Math.max(0, Math.min(totalPages - 1, next1Based - 1));
              setPage(nextZero);
            }}
          />
        </div>
      </div>
    </div>
  );
}
