import React, { useEffect, useMemo, useState } from "react";
import {
  addDays, addMonths, subMonths, startOfMonth, endOfMonth,
  startOfWeek, endOfWeek, isSameMonth, format
} from "date-fns";
import ko from "date-fns/locale/ko";
import {getAllHolidays, addPublicHoliday, deleteHoliday } from "../../api/facilityApi";

const toYmd = (d) => format(d, "yyyy-MM-dd");

// 백엔드 HolidayReason enum과 동일한 옵션 (라벨은 등록 시 label로 사용)
const HOLIDAY_REASON_OPTIONS = [
  { code: "MAINTENANCE", label: "정기점검" },
  { code: "INTERNAL",    label: "기관사정" },
  { code: "OTHER",       label: "기타" },
];

export default function FacilityDetailComponent() {
  const [currentMonth, setCurrentMonth] = useState(new Date());
  const [holidayMap, setHolidayMap] = useState(new Map()); // key: yyyy-MM-dd, val: [{date,label,type}]
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [info, setInfo] = useState("");

  // 모달 상태
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedDate, setSelectedDate] = useState(null); // "yyyy-MM-dd"
  const [selectedReason, setSelectedReason] = useState(HOLIDAY_REASON_OPTIONS[0].code);

  // 전체(공통) 공휴일 + 시설휴무(있다면) 로드
  useEffect(() => {
    let mounted = true;
    (async () => {
      setLoading(true);
      setError(null);
      try {
        const list = await getAllHolidays(); // [{ date, label, type }]
        if (!mounted) return;
        const map = new Map();
        for (const it of list || []) {
          const key = String(it.date);
          if (!map.has(key)) map.set(key, []);
          map.get(key).push(it);
        }
        setHolidayMap(map);
      } catch (e) {
        if (!mounted) return;
        setError("휴무일을 불러오지 못했어요.");
        setHolidayMap(new Map());
        console.error(e);
      } finally {
        if (mounted) setLoading(false);
      }
    })();
    return () => { mounted = false; };
  }, []);

  const monthLabel = format(currentMonth, "yyyy. MM", { locale: ko });

  const weeks = useMemo(() => {
    const startMonth = startOfMonth(currentMonth);
    const endMonthDate = endOfMonth(currentMonth);
    const gridStart = startOfWeek(startMonth, { weekStartsOn: 0 });
    const gridEnd = endOfWeek(endMonthDate, { weekStartsOn: 0 });

    const rows = [];
    let day = gridStart;
    while (day <= gridEnd) {
      const w = [];
      for (let i = 0; i < 7; i++) { w.push(day); day = addDays(day, 1); }
      rows.push(w);
    }
    return rows;
  }, [currentMonth]);

  const getHolidayItems = (d) => holidayMap.get(toYmd(d)) ?? [];

  // 날짜 클릭 → (이미 PUBLIC이면) 삭제 확인→삭제 / (없으면) 모달 오픈
  const handleDayClick = async (d) => {
    if (!isSameMonth(d, currentMonth) || saving) return;

    const dateStr = toYmd(d);
    const existing = getHolidayItems(d);
    const hasPublic = existing.some((it) => it.type === "PUBLIC");

    setError(null);
    setInfo("");

    if (hasPublic) {
      // ✅ 삭제 플로우
      const ok = window.confirm(`${dateStr} 공휴일을 삭제할까요?`);
      if (!ok) return;
      try {
        setSaving(true);
        await deleteHoliday(dateStr);

        // 상태 반영: PUBLIC 라벨만 제거 (같은 날짜의 FACILITY는 유지)
        const newMap = new Map(holidayMap);
        const oldArr = newMap.get(dateStr) ?? [];
        const nextArr = oldArr.filter((it) => it.type !== "PUBLIC");
        if (nextArr.length) newMap.set(dateStr, nextArr);
        else newMap.delete(dateStr);
        setHolidayMap(newMap);

        setInfo("공휴일을 삭제했습니다.");
      } catch (e) {
        console.error(e);
        const status = e?.response?.status;
        const msg =
          status === 404
            ? "해당 날짜의 공휴일이 없습니다."
            : e?.response?.data?.message || e.message || "공휴일 삭제에 실패했습니다.";
        setError(msg);
      } finally {
        setSaving(false);
      }
      return;
    }

    // ✅ 등록 플로우
    setSelectedDate(dateStr);
    setSelectedReason(HOLIDAY_REASON_OPTIONS[0].code);
    setModalOpen(true);
  };

  // 저장 → PublicHoliday 등록
  const submitHoliday = async () => {
    if (!selectedDate) return;
    const reasonObj = HOLIDAY_REASON_OPTIONS.find((o) => o.code === selectedReason);
    const label = reasonObj?.label ?? "휴무일";

    try {
      setSaving(true);
      await addPublicHoliday({ date: selectedDate, label }); // POST /addholiday

      // 낙관적 갱신
      const newMap = new Map(holidayMap);
      const arr = newMap.get(selectedDate) ?? [];
      arr.push({ date: selectedDate, label, type: "PUBLIC" });
      newMap.set(selectedDate, arr);
      setHolidayMap(newMap);

      setInfo("공휴일이 등록되었습니다.");
      setModalOpen(false);
    } catch (e) {
      console.error(e);
      const status = e?.response?.status;
      const msg =
        status === 409
          ? "이미 등록된 휴무일입니다."
          : e?.response?.data?.message || e.message || "공휴일 등록에 실패했습니다.";
      setError(msg);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="w-full max-w-3xl mx-auto">
      {/* 헤더 */}
      <div className="flex items-center justify-between mb-4">
        <button
          onClick={() => setCurrentMonth(subMonths(currentMonth, 1))}
          className="px-2 py-1 rounded hover:bg-gray-100"
          aria-label="이전 달"
        >
          &lt;
        </button>
        <div className="text-2xl font-semibold">{monthLabel}</div>
        <button
          onClick={() => setCurrentMonth(addMonths(currentMonth, 1))}
          className="px-2 py-1 rounded hover:bg-gray-100"
          aria-label="다음 달"
        >
          &gt;
        </button>
      </div>

      {/* 요일 헤더 */}
      <div className="grid grid-cols-7 text-center text-sm font-medium mb-2">
        {["일","월","화","수","목","금","토"].map((d, i) => (
          <div key={i} className={i===0 ? "text-red-500" : i===6 ? "text-blue-500" : ""}>
            {d}요일
          </div>
        ))}
      </div>

      {/* 날짜 그리드 */}
      <div className="grid grid-cols-7 gap-[1px] bg-gray-200 rounded">
        {weeks.flatMap((week, wi) =>
          week.map((d, di) => {
            const inMonth = isSameMonth(d, currentMonth);
            const items = getHolidayItems(d);
            const isAnyHoliday = items.length > 0;

            return (
              <button
                type="button"
                onClick={() => handleDayClick(d)}
                key={`${wi}-${di}`}
                className={[
                  "text-left bg-white min-h-[96px] p-2 text-sm relative focus:outline-none",
                  "hover:bg-gray-50 cursor-pointer",
                  !inMonth ? "text-gray-300" : "",
                  isAnyHoliday ? "ring-2 ring-red-300" : "",
                ].join(" ")}
              >
                <div
                  className={[
                    "absolute top-2 right-2 text-xs",
                    di===0 ? "text-red-500" : di===6 ? "text-blue-500" : "text-gray-600"
                  ].join(" ")}
                >
                  {format(d, "d")}
                </div>

                {/* 라벨 목록 */}
                <div className="mt-6 flex flex-col gap-1">
                  {items.map((it, idx) => (
                    <span
                      key={idx}
                      className={[
                        "inline-block w-fit text-[11px] px-2 py-0.5 rounded-full",
                        it.type === "PUBLIC"
                          ? "bg-red-100 text-red-700"      // 공휴일
                          : "bg-amber-100 text-amber-700"  // 시설 휴무
                      ].join(" ")}
                      title={it.type === "PUBLIC" ? "공휴일" : "시설 휴무"}
                    >
                      {it.label}
                    </span>
                  ))}
                </div>
              </button>
            );
          })
        )}
      </div>

      {loading && <div className="mt-3 text-sm text-gray-500">휴무일 불러오는 중…</div>}
      {saving && <div className="mt-2 text-sm text-gray-500">처리 중…</div>}
      {info && <div className="mt-2 text-sm text-green-600">{info}</div>}
      {error && <div className="mt-2 text-sm text-red-500">{error}</div>}

      {/* 공휴일 등록 모달 */}
      {modalOpen && (
        <div className="fixed inset-0 bg-black/30 flex items-center justify-center z-50" role="dialog" aria-modal="true">
          <div className="w-full max-w-md rounded-xl bg-white p-5 shadow">
            <div className="flex items-center justify-between">
              <h3 className="text-lg font-semibold">공휴일 등록</h3>
              <button className="p-1 rounded hover:bg-gray-100" onClick={() => setModalOpen(false)} aria-label="닫기">
                ✕
              </button>
            </div>

            <div className="mt-3 text-sm text-gray-600">
              날짜: <span className="font-medium text-gray-800">{selectedDate}</span>
            </div>

            <div className="mt-4">
              <label className="block text-sm font-medium mb-1">사유 선택</label>
              <div className="grid grid-cols-1 gap-2">
                {HOLIDAY_REASON_OPTIONS.map((opt) => (
                  <label key={opt.code} className="flex items-center gap-2 p-2 rounded border hover:bg-gray-50 cursor-pointer">
                    <input
                      type="radio"
                      name="holidayReason"
                      value={opt.code}
                      checked={selectedReason === opt.code}
                      onChange={(e) => setSelectedReason(e.target.value)}
                    />
                    <span>{opt.label}</span>
                  </label>
                ))}
              </div>
            </div>

            <div className="mt-5 flex justify-end gap-2">
              <button
                className="px-4 py-2 rounded border hover:bg-gray-50"
                onClick={() => setModalOpen(false)}
                disabled={saving}
              >
                취소
              </button>
              <button
                className="px-4 py-2 rounded bg-blue-600 text-white hover:bg-blue-700 disabled:opacity-50"
                onClick={submitHoliday}
                disabled={saving}
              >
                저장
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}