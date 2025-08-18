import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getAdminReservations, updateReservationState } from "../../api/facilityApi";

// ==================== 날짜/시간 유틸 ====================
const pad2 = (n) => String(n).padStart(2, "0");
const toYmd = (d) => `${d.getFullYear()}-${pad2(d.getMonth() + 1)}-${pad2(d.getDate())}`;
const formatYmdDots = (v) => {
  if (!v) return "-";
  const m = String(v).match(/^(\d{4})-(\d{2})-(\d{2})/);
  if (m) return `${m[1]}.${m[2]}.${m[3]}`;
  const d = new Date(String(v).replace(" ", "T"));
  if (Number.isNaN(d.getTime())) return String(v);
  return `${d.getFullYear()}.${pad2(d.getMonth() + 1)}.${pad2(d.getDate())}`;
};
const formatDateTime = (v) => {
  if (!v) return "-";
  const d = new Date(String(v).replace(" ", "T"));
  if (Number.isNaN(d.getTime())) return String(v);
  return `${d.getFullYear()}.${pad2(d.getMonth() + 1)}.${pad2(d.getDate())} ${pad2(d.getHours())}:${pad2(d.getMinutes())}`;
};
const hhmm = (v) => {
  if (!v) return "-";
  const m = String(v).match(/(\d{1,2}):(\d{2})/);
  return m ? `${pad2(m[1])}:${m[2]}` : String(v);
};
const chipOf = (state) => {
  switch (state) {
    case "APPROVED":
      return { label: "승인 완료", cls: "bg-green-500" };
    case "CANCEL":
    case "CANCELLED":
      return { label: "취소", cls: "bg-red-500" };
    case "WAITING":
    default:
      return { label: "승인 대기", cls: "bg-gray-500" };
  }
};

// ==================== 기간 빠른 선택 ====================
const todayRange = () => {
  const t = new Date();
  const ymd = toYmd(t);
  return { from: ymd, to: ymd };
};
const thisWeekRange = () => {
  const t = new Date();
  const day = t.getDay(); // 0(일)~6(토)
  const diffToMon = (day + 6) % 7; // 월=1 -> 0
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

// ==================== 메인 컴포넌트 ====================
export default function AdminFacilityReservations() {
  const navigate = useNavigate();

  // 필터 상태
  const [state, setState] = useState(""); // "", "WAITING", "APPROVED", "CANCEL"
  const [from, setFrom] = useState(thisMonthRange().from);
  const [to, setTo] = useState(thisMonthRange().to);

  // 데이터 상태
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // 액션 로딩(중복 방지)
  const [savingId, setSavingId] = useState(null); // reserveId 또는 null

  // 클라이언트 페이징/정렬
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [sortKey, setSortKey] = useState("reserveAt");
  const [sortDir, setSortDir] = useState("DESC");

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

  const paged = useMemo(() => {
    const start = page * pageSize;
    return sorted.slice(start, start + pageSize);
  }, [sorted, page, pageSize]);

  const totalPages = Math.max(1, Math.ceil(sorted.length / pageSize));

  const fetchData = async () => {
    if (from && to && new Date(`${from}T00:00:00`) > new Date(`${to}T00:00:00`)) {
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
      setPage(0);
    } catch (e) {
      console.error(e);
      const msg = e?.response?.data?.message || e?.message || "목록을 불러오지 못했습니다.";
      setError(msg);
      if (e?.response?.status === 401) {
        alert("관리자 로그인이 필요합니다.");
        navigate("/login");
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const quick = {
    today: () => { const r = todayRange(); setFrom(r.from); setTo(r.to); fetchData(); },
    week: () => { const r = thisWeekRange(); setFrom(r.from); setTo(r.to); fetchData(); },
    month: () => { const r = thisMonthRange(); setFrom(r.from); setTo(r.to); fetchData(); },
    all: () => { setFrom(""); setTo(""); fetchData(); },
  };

  const onChangeSort = (k) => {
    if (sortKey === k) setSortDir((d) => (d === "ASC" ? "DESC" : "ASC"));
    else { setSortKey(k); setSortDir("DESC"); }
  };

  // ====== 액션: 승인 / 거절 ======
  const doUpdate = async ({ reserveId, nextState }) => {
    try {
      setSavingId(reserveId);
      await updateReservationState({ reserveId, state: nextState });
      await fetchData();
    } catch (e) {
      console.error(e);
      alert(e?.response?.data?.message || e?.message || "상태 변경에 실패했습니다.");
    } finally {
      setSavingId(null);
    }
  };

  const handleApprove = (r) => {
    if (!r?.reserveId) return;
    if (!window.confirm(`예약 ${r.reserveId}을(를) 승인하시겠습니까?`)) return;
    doUpdate({ reserveId: r.reserveId, nextState: "APPROVED" });
  };

  const handleReject = (r) => {
    if (!r?.reserveId) return;
    if (!window.confirm(`예약 ${r.reserveId}을(를) 거절(취소)하시겠습니까?`)) return;
    doUpdate({ reserveId: r.reserveId, nextState: "CANCEL" });
  };

  const renderActions = (r) => {
    const waiting = r.state === "WAITING";
    if (!waiting) return <span className="text-gray-400">-</span>;
    const disabled = savingId === r.reserveId;
    return (
      <div className="flex gap-2">
        <button
          disabled={disabled}
          onClick={() => handleApprove(r)}
          className={`px-3 py-1 rounded text-white ${disabled ? "bg-green-300 cursor-not-allowed" : "bg-green-600 hover:bg-green-700"}`}
        >승인</button>
        <button
          disabled={disabled}
          onClick={() => handleReject(r)}
          className={`px-3 py-1 rounded text-white ${disabled ? "bg-red-300 cursor-not-allowed" : "bg-red-600 hover:bg-red-700"}`}
        >거절</button>
      </div>
    );
  };

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <h2 className="text-2xl font-bold mb-6 text-center">공간 예약 현황 (관리자)</h2>

      {/* 필터 박스 */}
      <div className="rounded-2xl border p-4 mb-4 bg-white shadow-sm">
        <div className="grid grid-cols-1 md:grid-cols-5 gap-3 items-end">
          <div className="flex flex-col">
            <label className="text-xs text-gray-600 mb-1">상태</label>
            <select
              value={state}
              onChange={(e) => setState(e.target.value)}
              className="border rounded px-3 py-2"
            >
              <option value="">전체</option>
              <option value="WAITING">승인 대기</option>
              <option value="APPROVED">승인 완료</option>
              <option value="CANCEL">취소</option>
            </select>
          </div>
          <div className="flex flex-col">
            <label className="text-xs text-gray-600 mb-1">시작일</label>
            <input
              type="date"
              value={from}
              onChange={(e) => setFrom(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && fetchData()}
              className="border rounded px-3 py-2"
            />
          </div>
          <div className="flex flex-col">
            <label className="text-xs text-gray-600 mb-1">종료일</label>
            <input
              type="date"
              value={to}
              onChange={(e) => setTo(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && fetchData()}
              className="border rounded px-3 py-2"
            />
          </div>
          <div className="flex gap-2 md:col-span-2">
            <button onClick={fetchData} className="flex-1 bg-blue-600 text-white rounded px-3 py-2 hover:bg-blue-700">조회</button>
            <button onClick={quick.today} className="px-3 py-2 border rounded hover:bg-gray-50">오늘</button>
            <button onClick={quick.week} className="px-3 py-2 border rounded hover:bg-gray-50">이번주</button>
            <button onClick={quick.month} className="px-3 py-2 border rounded hover:bg-gray-50">이번달</button>
            <button onClick={quick.all} className="px-3 py-2 border rounded hover:bg-gray-50">전체</button>
          </div>
        </div>
      </div>

      {/* 옵션 바 */}
      <div className="flex flex-wrap items-center justify-between gap-3 mb-3">
        <div className="text-sm text-gray-600">총 <b>{rows.length}</b>건</div>
        <div className="flex items-center gap-2">
          <label className="text-sm text-gray-600">정렬</label>
          <select value={`${sortKey}:${sortDir}`} onChange={(e) => {
            const [k, d] = e.target.value.split(":");
            setSortKey(k); setSortDir(d);
          }} className="border rounded px-2 py-1 text-sm">
            <option value="reserveAt:DESC">신청일 ↓</option>
            <option value="reserveAt:ASC">신청일 ↑</option>
            <option value="facDate:DESC">이용일 ↓</option>
            <option value="facDate:ASC">이용일 ↑</option>
            <option value="facName:ASC">공간명 A→Z</option>
            <option value="facName:DESC">공간명 Z→A</option>
          </select>
          <label className="text-sm text-gray-600 ml-3">페이지 크기</label>
          <select value={pageSize} onChange={(e) => { setPageSize(Number(e.target.value)); setPage(0); }} className="border rounded px-2 py-1 text-sm">
            <option value={10}>10</option>
            <option value={20}>20</option>
            <option value={50}>50</option>
          </select>
        </div>
      </div>

      {/* 데이터 표 */}
      <div className="overflow-x-auto rounded-2xl border bg-white shadow-sm">
        <table className="min-w-full text-sm">
          <thead>
            <tr className="bg-gray-50 text-left">
              <th className="px-4 py-3 cursor-pointer" onClick={() => onChangeSort("reserveId")}>예약ID</th>
              <th className="px-4 py-3 cursor-pointer" onClick={() => onChangeSort("facName")}>공간명</th>
              <th className="px-2 py-3">공간번호</th>
              <th className="px-4 py-3 cursor-pointer" onClick={() => onChangeSort("facDate")}>이용일</th>
              <th className="px-4 py-3">이용 시간</th>
              <th className="px-4 py-3">신청자</th>
              <th className="px-4 py-3">상태</th>
              <th className="px-4 py-3 cursor-pointer" onClick={() => onChangeSort("reserveAt")}>신청일</th>
              <th className="px-4 py-3">액션</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={9} className="px-4 py-8 text-center text-gray-500">불러오는 중…</td></tr>
            ) : error ? (
              <tr><td colSpan={9} className="px-4 py-8 text-center text-red-600">{error}</td></tr>
            ) : paged.length === 0 ? (
              <tr><td colSpan={9} className="px-4 py-8 text-center text-gray-500">데이터가 없습니다.</td></tr>
            ) : (
              paged.map((r) => {
                const chip = chipOf(r.state);
                const mem = r.memId || "-";
                return (
                  <tr key={r.reserveId} className="border-t">
                    <td className="px-4 py-3 whitespace-nowrap">{r.reserveId}</td>
                    <td className="px-4 py-3 whitespace-nowrap">{r.facName}</td>
                    <td className="px-2 py-3 whitespace-nowrap text-gray-600">{r.facRevNum}</td>
                    <td className="px-4 py-3 whitespace-nowrap">{formatYmdDots(r.facDate)}</td>
                    <td className="px-4 py-3 whitespace-nowrap">{hhmm(r.startTime)} ~ {hhmm(r.endTime)}</td>
                    <td className="px-4 py-3 whitespace-nowrap">{mem}</td>
                    <td className="px-4 py-3 whitespace-nowrap">
                      <span className={`text-white text-xs px-2 py-0.5 rounded ${chip.cls}`}>{chip.label}</span>
                    </td>
                    <td className="px-4 py-3 whitespace-nowrap text-gray-600">{formatDateTime(r.reserveAt)}</td>
                    <td className="px-4 py-3 whitespace-nowrap">
                      <div className="flex items-center gap-2">
                        {renderActions(r)}
                        <button
                          onClick={() => navigate(`/facility/detail/${r.facRevNum}`)}
                          className="border px-3 py-1 rounded hover:bg-gray-50"
                        >상세보기</button>
                      </div>
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>

      {/* 페이지네이션 */}
      <div className="mt-4 flex justify-center items-center gap-2 text-blue-600 font-semibold">
        <button
          className="px-2 py-1 disabled:text-gray-400"
          disabled={page === 0}
          onClick={() => setPage((p) => Math.max(0, p - 1))}
        >{"<"}</button>
        {Array.from({ length: totalPages }, (_, i) => (
          <button
            key={i}
            className={`px-2 py-1 ${page === i ? "underline text-blue-800" : "hover:text-blue-800"}`}
            onClick={() => setPage(i)}
          >{i + 1}</button>
        ))}
        <button
          className="px-2 py-1 disabled:text-gray-400"
          disabled={page >= totalPages - 1}
          onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
        >{">"}</button>
      </div>
    </div>
  );
}
