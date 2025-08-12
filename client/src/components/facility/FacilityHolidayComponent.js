// FacilityCalendar.jsx
import React, { useEffect, useMemo, useState } from "react";
import {
  addDays, addMonths, subMonths, startOfMonth, endOfMonth,
  startOfWeek, endOfWeek, isSameMonth, format
} from "date-fns";
import ko from "date-fns/locale/ko";
import { getAllHolidays } from "../../api/facilityApi";

const toYmd = (d) => format(d, "yyyy-MM-dd");

export default function FacilityCalendar() {
  const [currentMonth, setCurrentMonth] = useState(new Date());
  // 날짜별 [{date, label, type}]를 모아두는 맵
  const [holidayMap, setHolidayMap] = useState(new Map());
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // 전체 시설 휴무일 불러오기
  useEffect(() => {
    let mounted = true;
    (async () => {
      setLoading(true);
      setError(null);
      try {
        // 예: [{ date:"2025-07-10", label:"설날", type:"PUBLIC" }, { date:"2025-07-10", label:"정기점검", type:"FACILITY" }]
        const list = await getAllHolidays();
        if (!mounted) return;

        const map = new Map();
        for (const it of list || []) {
          const key = String(it.date); // "yyyy-MM-dd"
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
            const items = getHolidayItems(d); // [{date,label,type}, ...]
            const isAnyHoliday = items.length > 0;

            return (
              <div
                key={`${wi}-${di}`}
                className={[
                  "bg-white min-h-[96px] p-2 text-sm relative",
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
              </div>
            );
          })
        )}
      </div>

      {loading && <div className="mt-3 text-sm text-gray-500">휴무일 불러오는 중…</div>}
      {error && <div className="mt-2 text-sm text-red-500">{error}</div>}
    </div>
  );
}