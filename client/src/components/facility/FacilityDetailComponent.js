// src/components/FacilityDetailContent.jsx
import React, { useEffect, useMemo, useRef, useState } from "react";
import {
  addDays, addMonths, startOfMonth, endOfMonth,
  startOfWeek, endOfWeek, isSameMonth, format,
  addMinutes, parse, differenceInMinutes
} from "date-fns";
import ko from "date-fns/locale/ko";
import { getFacilityDetail, getAllHolidays, createReservation } from "../../api/facilityApi";

// -------------------- 이미지 URL 유틸 --------------------
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

// -------------------- 시간/날짜 유틸 --------------------
const toYmd = (d) => format(d, "yyyy-MM-dd");
const todayYmd = () => format(new Date(), "yyyy-MM-dd");
const nowHHmm = () => format(new Date(), "HH:mm");

/** "HH:mm[:ss[.SSS]]" 등 → "HH:mm" */
function normalizeHHmm(v) {
  if (v == null) return null;
  if (typeof v === "string") {
    const m = v.match(/(\d{1,2}):(\d{2})/);
    if (m) return `${m[1].padStart(2, "0")}:${m[2].padStart(2, "0")}`;
  }
  try {
    const d = new Date(v);
    if (!Number.isNaN(d.getTime())) return format(d, "HH:mm");
  } catch (_) {}
  return null;
}

/** 시작시간 라디오 목록(1시간 간격) */
function genStartSlots(openHHmm, closeHHmm) {
  const base = new Date();
  let s = parse(openHHmm, "HH:mm", base);
  const end = parse(closeHHmm, "HH:mm", base);
  const out = [];
  while (addMinutes(s, 60) <= end) {
    out.push({ key: format(s, "HH:mm"), label: format(s, "HH:mm") });
    s = addMinutes(s, 60);
  }
  return out;
}

function maxDurationHours(startHHmm, closeHHmm, capHours = 4) {
  const base = new Date();
  const s = parse(startHHmm, "HH:mm", base);
  const close = parse(closeHHmm, "HH:mm", base);
  const diffMin = Math.max(0, differenceInMinutes(close, s));
  return Math.min(capHours, Math.floor(diffMin / 60));
}

function calcEndHHmm(startHHmm, hours) {
  const base = new Date();
  const s = parse(startHHmm, "HH:mm", base);
  return format(addMinutes(s, hours * 60), "HH:mm");
}

// ======================================================

export default function FacilityDetailContent({ facRevNum }) {
  // 상단 상세
  const [data, setData] = useState(null);
  const [idx, setIdx] = useState(0);
  const [detailLoading, setDetailLoading] = useState(true);
  const [err, setErr] = useState("");
  const startX = useRef(null);

  // 달력/예약
  const [currentMonth, setCurrentMonth] = useState(new Date());
  const [holidayMap, setHolidayMap] = useState(new Map());
  const [holLoading, setHolLoading] = useState(false);
  const [holError, setHolError] = useState("");

  const [selectedDate, setSelectedDate] = useState(null);
  const [startKey, setStartKey] = useState(null);
  const [durationHrs, setDurationHrs] = useState(null);

  // 예약 전송 상태/메시지
  const [submitting, setSubmitting] = useState(false);
  const [notice, setNotice] = useState("");

  // -------------------- 상세 불러오기 --------------------
  useEffect(() => {
    let mounted = true;
    setDetailLoading(true);
    setErr("");
    getFacilityDetail(facRevNum)
      .then((d) => { if (mounted) { setData(d); setIdx(0); }})
      .catch((e) => { if (mounted) setErr(e?.response?.data?.message || e.message || "시설 정보를 불러오지 못했습니다."); })
      .finally(() => mounted && setDetailLoading(false));
    return () => { mounted = false; };
  }, [facRevNum]);

  // -------------------- 공휴일/휴무 불러오기 --------------------
  useEffect(() => {
    let mounted = true;
    (async () => {
      setHolLoading(true);
      setHolError("");
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
        setHolError("휴무일을 불러오지 못했어요.");
        setHolidayMap(new Map());
        console.error(e);
      } finally {
        if (mounted) setHolLoading(false);
      }
    })();
    return () => { mounted = false; };
  }, []);

  // -------------------- 이미지 캐러셀 --------------------
  const srcs = useMemo(() => {
    const raw = data?.images ?? [];
    const arr = (Array.isArray(raw) && raw.length ? raw : [null]).map(buildImageUrl);
    const cleaned = arr.filter(Boolean);
    return cleaned.length ? cleaned : [PLACEHOLDER];
  }, [data]);
  const n = srcs.length;
  const prevImg = () => setIdx((i) => (i - 1 + n) % n);
  const nextImg = () => setIdx((i) => (i + 1) % n);
  const onTouchStart = (e) => (startX.current = e.touches[0].clientX);
  const onTouchEnd = (e) => {
    if (startX.current == null) return;
    const dx = e.changedTouches[0].clientX - startX.current;
    if (Math.abs(dx) > 40) (dx > 0 ? prevImg() : nextImg());
    startX.current = null;
  };
  useEffect(() => {
    const onKey = (e) => {
      if (e.key === "ArrowLeft") prevImg();
      if (e.key === "ArrowRight") nextImg();
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [n]);

  // -------------------- 달력 --------------------
  const monthLabel = format(currentMonth, "yyyy. MM", { locale: ko });

  // "이전 달 금지" 제어
  const minMonthStart = startOfMonth(new Date());
  const isPrevDisabled = startOfMonth(currentMonth) <= minMonthStart;

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
  const selectedDateHasAnyHoliday =
    selectedDate ? ((holidayMap.get(selectedDate)?.length || 0) > 0) : false;

  const handleDayClick = (d) => {
    const ymd = toYmd(d);
    if (!isSameMonth(d, currentMonth)) return;
    if (ymd < todayYmd()) return; // 과거 날짜 비활성화
    setSelectedDate(ymd);
    setStartKey(null);
    setDurationHrs(null);
    setNotice("");
  };

  // -------------------- 예약가능시간 (정시) --------------------
  const open  = normalizeHHmm(data?.reserveStart) ?? "09:00";
  const close = normalizeHHmm(data?.reserveEnd)   ?? "18:00";

  // 오늘이면 "최소 1시간 사용 시 종료가 현재 이후"인 시작 슬롯만 표시
  const startSlots = useMemo(() => {
    const base = genStartSlots(open, close);
    if (selectedDate === todayYmd()) {
      return base.filter(s => calcEndHHmm(s.key, 1) > nowHHmm());
    }
    return base;
  }, [open, close, selectedDate]);

  const maxHrsFromStart = startKey ? maxDurationHours(startKey, close, 4) : 0;
  const endTimeText = (startKey && durationHrs)
    ? `${startKey} ~ ${calcEndHHmm(startKey, durationHrs)}`
    : "-";

  // 오늘이면 종료시각이 현재 초과일 때만 예약 허용
  const endIfChosen = (startKey && durationHrs) ? calcEndHHmm(startKey, durationHrs) : null;
  const notPastTimeOK = !startKey || !durationHrs || (selectedDate !== todayYmd() ? true : endIfChosen > nowHHmm());
  const canReserve = !!selectedDate && !!startKey && !!durationHrs && !selectedDateHasAnyHoliday && notPastTimeOK;

  // -------------------- 예약 API 호출 --------------------
  const applyReserve = async () => {
    if (!canReserve || submitting) return;
    try {
      setSubmitting(true);
      setNotice("");
      await createReservation({
        facRevNum,
        facDate: selectedDate,                 // "YYYY-MM-DD"
        startTime: startKey,                   // "HH:mm"
        endTime: calcEndHHmm(startKey, durationHrs), // "HH:mm"
      });
      setNotice("예약 신청이 완료되었습니다. (승인 대기)");
    } catch (e) {
      const msg = e?.response?.data?.message || e?.message || "예약 신청 중 오류가 발생했습니다.";
      setNotice(msg);
    } finally {
      setSubmitting(false);
    }
  };

  // -------------------- 로딩/에러 --------------------
  if (detailLoading) {
    return (
      <div className="mt-6 grid grid-cols-1 gap-8">
        <div className="h-[420px] rounded border border-gray-300 bg-gray-100 animate-pulse" />
        <div className="space-y-4">
          <div className="h-6 w-3/4 rounded bg-gray-100 animate-pulse" />
          <div className="h-4 w-full rounded bg-gray-100 animate-pulse" />
          <div className="h-4 w-5/6 rounded bg-gray-100 animate-pulse" />
          <div className="h-24 w-full rounded bg-gray-100 animate-pulse" />
        </div>
      </div>
    );
  }
  if (err) {
    return <div className="mt-6 rounded-xl border border-red-200 bg-red-50 p-6 text-red-800">{err}</div>;
  }

  // -------------------- 렌더 --------------------
  return (
    <div className="mt-6 space-y-8">
      {/* 상단: 이미지 + 상세 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* 이미지 캐러셀 */}
        <div
          className="relative border border-gray-300 bg-white overflow-hidden select-none rounded-xl"
          onTouchStart={onTouchStart}
          onTouchEnd={onTouchEnd}
        >
          <div className="w-full flex items-center justify-center">
            <img
              key={srcs[idx]}
              src={srcs[idx]}
              alt={`facility-${idx + 1}`}
              className="w-full h-auto max-h-[420px] object-contain"
              onError={(e) => (e.currentTarget.src = PLACEHOLDER)}
              draggable={false}
            />
          </div>
          {srcs.length > 1 && (
            <>
              <button
                aria-label="이전 이미지"
                onClick={prevImg}
                className="absolute left-2 top-1/2 -translate-y-1/2 bg-white/80 hover:bg-white rounded-full w-9 h-9 flex items-center justify-center shadow"
              >
                ‹
              </button>
              <button
                aria-label="다음 이미지"
                onClick={nextImg}
                className="absolute right-2 top-1/2 -translate-y-1/2 bg-white/80 hover:bg-white rounded-full w-9 h-9 flex items-center justify-center shadow"
              >
                ›
              </button>
              <div className="absolute bottom-2 left-1/2 -translate-x-1/2 flex gap-1">
                {srcs.map((_, i) => (
                  <button
                    key={i}
                    type="button"
                    onClick={() => setIdx(i)}
                    className={`w-2.5 h-2.5 rounded-full ${i === idx ? "bg-black/70" : "bg-black/30"}`}
                    aria-label={`이미지 ${i + 1}`}
                  />
                ))}
              </div>
            </>
          )}
        </div>

        {/* 상세 정보 */}
        <div className="p-2">
          <h2 className="text-lg font-semibold">{data?.facName ?? "시설명"}</h2>
          <p className="mt-1 text-gray-500">{data?.facInfo ?? "-"}</p>
          <dl className="mt-6 space-y-3">
            <Row label="예약가능시간" value={`${open} ~ ${close}`} />
            <Row label="수용인원" value={data?.capacity != null ? `${data.capacity}명` : "-"} />
            <Row label="구비품목" value={data?.facItem ?? "-"} />
            <Row label="유의사항" value={data?.etc ?? "-"} />
          </dl>
        </div>
      </div>

      <div className="border-t border-gray-200" />

      {/* 하단: 달력 + 예약 패널 */}
      <CalendarAndReserve
        currentMonth={currentMonth}
        setCurrentMonth={setCurrentMonth}
        isPrevDisabled={isPrevDisabled}
        monthLabel={monthLabel}
        weeks={weeks}
        getHolidayItems={getHolidayItems}
        selectedDate={selectedDate}
        setSelectedDate={handleDayClick}
        open={open}
        close={close}
        selectedDateHasAnyHoliday={selectedDateHasAnyHoliday}
        startSlots={startSlots}
        startKey={startKey}
        setStartKey={setStartKey}
        durationHrs={durationHrs}
        setDurationHrs={setDurationHrs}
        maxHrsFromStart={maxHrsFromStart}
        endTimeText={endTimeText}
        canReserve={canReserve}
        applyReserve={applyReserve}
        holLoading={holLoading}
        holError={holError}
        submitting={submitting}
        notice={notice}
      />
    </div>
  );
}

function CalendarAndReserve(props) {
  const {
    currentMonth, setCurrentMonth, isPrevDisabled, monthLabel, weeks, getHolidayItems,
    selectedDate, setSelectedDate, open, close,
    selectedDateHasAnyHoliday, startSlots, startKey, setStartKey,
    durationHrs, setDurationHrs, maxHrsFromStart, endTimeText,
    canReserve, applyReserve, holLoading, holError, submitting, notice
  } = props;

  return (
    <div className="flex items-start justify-center gap-6">
      {/* 달력 */}
      <div className="flex-1 max-w-[680px]">
        {/* 헤더: 이전달 이동 비활성화 */}
        <div className="flex items-center justify-center gap-6 mb-3">
          <button
            onClick={() => { if (!isPrevDisabled) setCurrentMonth(addMonths(currentMonth, -1)); }}
            className={`px-3 py-1 rounded ${isPrevDisabled ? "opacity-40 cursor-not-allowed" : "hover:bg-gray-100"}`}
            aria-label="이전 달"
            disabled={isPrevDisabled}
          >
            &lt;
          </button>
          <div className="text-3xl font-extrabold tracking-wide">{monthLabel}</div>
          <button
            onClick={() => setCurrentMonth(addMonths(currentMonth, 1))}
            className="px-3 py-1 rounded hover:bg-gray-100"
            aria-label="다음 달"
          >
            &gt;
          </button>
        </div>

        {/* 요일 헤더 */}
        <div className="grid grid-cols-7 text-center text-sm font-semibold text-gray-500 mb-1">
          {["일","월","화","수","목","금","토"].map((d, i) => (
            <div key={i} className={i===0 ? "text-red-500" : i===6 ? "text-blue-500" : ""}>{d}요일</div>
          ))}
        </div>

        {/* 날짜 그리드 (오늘 이전 날짜 비활성화) */}
        <div className="grid grid-cols-7 gap-[1px] bg-gray-200 rounded">
          {weeks.flatMap((week, wi) =>
            week.map((d, di) => {
              const ymd = toYmd(d);
              const inMonth = isSameMonth(d, currentMonth);
              const items = getHolidayItems(d);
              const isAnyHoliday = items.length > 0;
              const isSelected = selectedDate && selectedDate === ymd;
              const isPastDay = ymd < todayYmd();

              return (
                <button
                  type="button"
                  onClick={() => setSelectedDate(d)}
                  key={`${wi}-${di}`}
                  className={[
                    "text-left bg-white h-[96px] p-2 text-sm relative focus:outline-none",
                    isPastDay ? "opacity-40 cursor-not-allowed" : "hover:bg-gray-50 cursor-pointer",
                    !inMonth ? "text-gray-300" : "",
                    isAnyHoliday ? "ring-2 ring-red-300" : "",
                    isSelected ? "outline outline-2 outline-blue-400" : "",
                  ].join(" ")}
                  disabled={isPastDay}
                >
                  <div className={[
                    "absolute top-2 right-2 text-xs",
                    di===0 ? "text-red-500" : di===6 ? "text-blue-500" : "text-gray-600"
                  ].join(" ")}>
                    {format(d, "d")}
                  </div>
                  <div className="mt-6 flex flex-col gap-1">
                    {items.map((it, idx) => (
                      <span
                        key={idx}
                        className={[
                          "inline-block w-fit text-[11px] px-2 py-0.5 rounded-full",
                          it.type === "PUBLIC" ? "bg-red-100 text-red-700" : "bg-amber-100 text-amber-700"
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

        {holLoading && <div className="mt-3 text-sm text-gray-500">휴무일 불러오는 중…</div>}
        {holError && <div className="mt-2 text-sm text-red-500">{holError}</div>}
      </div>

      {/* 예약 패널 */}
      <aside className="w-full max-w-[360px]">
        <div className="rounded-xl border border-gray-200 bg-white p-5">
          <div className="text-blue-500 font-bold">예약 정보</div>

          <dl className="mt-4 space-y-3 text-sm">
            <div className="grid grid-cols-3">
              <dt className="text-gray-500">예약일</dt>
              <dd className="col-span-2 font-medium text-gray-900">{selectedDate ?? "-"}</dd>
            </div>
            <div className="grid grid-cols-3">
              <dt className="text-gray-500">예약가능</dt>
              <dd className="col-span-2 font-medium text-gray-900">{open} ~ {close}</dd>
            </div>
            <div className="grid grid-cols-3">
              <dt className="text-gray-500">규칙</dt>
              <dd className="col-span-2 text-gray-500">하루 1회, 최대 4시간 연속 사용</dd>
            </div>
          </dl>

          {/* 시작시간 */}
          <div className="mt-3">
            <div className="text-sm font-medium text-gray-700 mb-2">시작시간</div>
            <div className="grid grid-cols-2 gap-2">
              {startSlots.map((s) => (
                <label key={s.key}
                  className="flex items-center gap-2 rounded px-2 py-1 hover:bg-gray-50 cursor-pointer text-sm border">
                  <input
                    type="radio"
                    name="start"
                    value={s.key}
                    disabled={!selectedDate || selectedDateHasAnyHoliday}
                    checked={startKey === s.key}
                    onChange={() => { setStartKey(s.key); setDurationHrs(null); }}
                  />
                  <span className="text-gray-800">{s.label}</span>
                </label>
              ))}
            </div>
          </div>

          {/* 이용시간 (오늘은 종료가 현재 이후인 경우만 활성) */}
          <div className="mt-4">
            <div className="text-sm font-medium text-gray-700 mb-2">이용시간</div>
            <div className="flex flex-wrap gap-2">
              {[1,2,3,4].map((h) => {
                const allowedByClose = startKey ? h <= maxHrsFromStart : false;
                const allowedByNow =
                  !startKey ? false :
                  (selectedDate !== todayYmd() ? true : (calcEndHHmm(startKey, h) > nowHHmm()));
                const allowed = allowedByClose && allowedByNow;

                return (
                  <button
                    key={h}
                    type="button"
                    className={[
                      "px-3 py-1 rounded border text-sm",
                      durationHrs === h ? "bg-blue-600 text-white border-blue-600" : "hover:bg-gray-50",
                      !allowed || !selectedDate || selectedDateHasAnyHoliday ? "opacity-50 cursor-not-allowed" : ""
                    ].join(" ")}
                    disabled={!allowed || !selectedDate || selectedDateHasAnyHoliday}
                    onClick={() => setDurationHrs(h)}
                  >
                    {h}시간
                  </button>
                );
              })}
            </div>
            <div className="mt-2 text-sm text-gray-600">
              선택한 시간: <span className="font-medium text-gray-900">
                {startKey && durationHrs ? endTimeText : "-"}
              </span>
            </div>
          </div>

          <button
            className="mt-5 w-full h-12 rounded-lg bg-blue-500 text-white font-semibold hover:bg-blue-600 disabled:opacity-50"
            onClick={applyReserve}
            disabled={!canReserve || submitting}
          >
            {submitting ? "전송 중..." : "예약 신청하기"}
          </button>

          {notice && <div className="mt-3 text-sm text-gray-700">{notice}</div>}
        </div>
      </aside>
    </div>
  );
}

function Row({ label, value }) {
  return (
    <div className="grid grid-cols-3">
      <dt className="col-span-1 text-gray-400">{label}</dt>
      <dd className="col-span-2 text-gray-800 whitespace-pre-line">{value}</dd>
    </div>
  );
}