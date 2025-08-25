// 변경 사항 요약
// 1) 이미지 영역을 Swiper 슬라이더로 교체(자동재생/페이지네이션/네비게이션 적용)
// 2) "목록으로" 버튼은 항상 "/facility/list"로 이동하도록 유지
// 3) 최상단 div의 클래스(max-w-screen-xl mx-auto my-10)와 그 바로 아래 div의 클래스(min-blank)는 고정

import React, { useEffect, useMemo, useState, useCallback } from "react";
import {
  addDays, addMonths, startOfMonth, endOfMonth,
  startOfWeek, endOfWeek, isSameMonth, format,
  addMinutes, parse, differenceInMinutes, isBefore,
  isEqual,
} from "date-fns";
import ko from "date-fns/locale/ko";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import {
  getFacilityDetail,
  getAllHolidays,
  createReservation,
  getReservedBlocks,
} from "../../api/facilityApi";

// Swiper 슬라이더 관련 import
import { Swiper, SwiperSlide } from "swiper/react";
import "swiper/css";
import "swiper/css/pagination";
import "swiper/css/navigation";
import { Autoplay, Pagination, Navigation } from "swiper/modules";

/* ---------- 이미지 URL 유틸 ---------- */
const PLACEHOLDER = "/placeholder.svg";
const host = "http://localhost:8090/view";
function buildImageUrl(p) {
  const val = typeof p === "string" ? p : p?.imageUrl;
  if (!val) return PLACEHOLDER;
  if (/^https?:\/\//i.test(val)) return val;
  let path = String(val).trim();
  path = path.replace(/^https?:\/\/[^/]+/i, "");
  path = path.replace(/^\/?view\/?/, "/");
  if (!path.startsWith("/")) path = "/" + path;
  return (host + path).replace(/([^:]\/)\/+/g, "$1");
}

/* ---------- 시간/날짜 유틸 ---------- */
const toYmd = (d) => format(d, "yyyy-MM-dd");
const todayYmd = () => format(new Date(), "yyyy-MM-dd");
const nowHHmm = () => format(new Date(), "HH:mm");

function normalizeHHmm(v) {
  if (v == null) return null;
  if (typeof v === "string") {
    const m = v.match(/(\d{1,2}):(\d{2})/);
    if (m) return `${m[1].padStart(2, "0")}:${m[2].padStart(2, "0")}`;
  }
  try {
    const d = new Date(v);
    if (!Number.isNaN(d.getTime())) return format(d, "HH:mm");
  } catch {}
  return null;
}
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
const overlaps = (s1, e1, s2, e2) => s1 < e2 && s2 < e1;

/* ====================================================== */

export default function FacilityDetailContent({ facRevNum }) {
  /* 상세 */
  const [data, setData] = useState(null);
  const [detailLoading, setDetailLoading] = useState(true);
  const [err, setErr] = useState("");

  /* 캘린더 & 예약 */
  const [currentMonth, setCurrentMonth] = useState(new Date());
  const [holidayMap, setHolidayMap] = useState(new Map());
  const [holLoading, setHolLoading] = useState(false);
  const [holError, setHolError] = useState("");

  const [selectedDate, setSelectedDate] = useState(null);
  const [startKey, setStartKey] = useState(null);
  const [durationHrs, setDurationHrs] = useState(null);
  const [reservedBlocks, setReservedBlocks] = useState([]);

  const [submitting, setSubmitting] = useState(false);
  const [notice, setNotice] = useState("");

  const isAdmin = useSelector((s) => s.loginState?.role === "ADMIN");
  const navigate = useNavigate();

  /* 상세 불러오기 */
  useEffect(() => {
    let mounted = true;
    setDetailLoading(true);
    setErr("");
    getFacilityDetail(facRevNum)
      .then((d) => {
        if (!mounted) return;
        setData(d);
      })
      .catch((e) => mounted && setErr(e?.response?.data?.message || e.message || "공간 정보를 불러오지 못했습니다."))
      .finally(() => mounted && setDetailLoading(false));
    return () => { mounted = false; };
  }, [facRevNum]);

  /* 휴무일 */
  useEffect(() => {
    let mounted = true;
    setHolLoading(true);
    setHolError("");
    getAllHolidays()
      .then((list) => {
        if (!mounted) return;
        const map = new Map();
        (Array.isArray(list) ? list : []).forEach((it) => {
          const key = String(it.date);
          if (!map.has(key)) map.set(key, []);
          map.get(key).push(it);
        });
        setHolidayMap(map);
      })
      .catch(() => {
        if (!mounted) return;
        setHolError("휴무일을 불러오지 못했어요.");
        setHolidayMap(new Map());
      })
      .finally(() => mounted && setHolLoading(false));
    return () => { mounted = false; };
  }, []);

  /* 선택일 예약 블록 */
  useEffect(() => {
    let mounted = true;
    if (!selectedDate) { setReservedBlocks([]); return; }
    getReservedBlocks(facRevNum, selectedDate)
      .then((blocks) => {
        if (!mounted) return;
        const list = (Array.isArray(blocks) ? blocks : [])
          .map((b) => ({
            start: typeof b.start === "string" ? b.start.slice(0, 5) : b.start,
            end: typeof b.end === "string" ? b.end.slice(0, 5) : b.end,
          }))
          .sort((a, b) => (a.start < b.start ? -1 : 1));
        setReservedBlocks(list);
      })
      .catch(() => setReservedBlocks([]));
    return () => { mounted = false; };
  }, [facRevNum, selectedDate]);

  /* ===== 이미지: 대표 먼저 오도록 정렬 ===== */
  const srcs = useMemo(() => {
    const raw = Array.isArray(data?.images) ? data.images : [];
    if (!raw.length) return [PLACEHOLDER];
    const sorted = [...raw].sort((a, b) => {
      const aMain = !!(typeof a === "object" && a?.mainImage);
      const bMain = !!(typeof b === "object" && b?.mainImage);
      return (bMain ? 1 : 0) - (aMain ? 1 : 0);
    });
    const urls = sorted.map(buildImageUrl).filter(Boolean);
    return urls.length ? urls : [PLACEHOLDER];
  }, [data]);

  /* 달력 경계(현재월 포함 + 3개월) */
  const monthStart = useMemo(() => startOfMonth(currentMonth), [currentMonth]);
  const minMonthStart = useMemo(() => startOfMonth(new Date()), []);
  const maxMonthStart = useMemo(() => startOfMonth(addMonths(new Date(), 3)), []);

  const isPrevDisabled = useMemo(
    () => isEqual(monthStart, minMonthStart) || isBefore(monthStart, minMonthStart),
    [monthStart, minMonthStart]
  );
  const isNextDisabled = useMemo(
    () => isEqual(monthStart, maxMonthStart) || isBefore(maxMonthStart, monthStart),
    [monthStart, maxMonthStart]
  );

  const monthLabel = useMemo(
    () => format(currentMonth, "yyyy. MM", { locale: ko }),
    [currentMonth]
  );

  const weeks = useMemo(() => {
    const startMonth_ = startOfMonth(currentMonth);
    const endMonthDate = endOfMonth(currentMonth);
    const gridStart = startOfWeek(startMonth_, { weekStartsOn: 0 });
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
  const selectedDateHasHoliday = selectedDate ? (holidayMap.get(selectedDate)?.length > 0) : false;

  function handleDayClick(d) {
    const ymd = toYmd(d);
    if (!isSameMonth(d, currentMonth)) return;
    if (ymd < todayYmd()) return;
    setSelectedDate(ymd);
    setStartKey(null);
    setDurationHrs(null);
    setNotice("");
  }

  /* 슬롯/버튼 데이터 */
  const open = normalizeHHmm(data?.reserveStart) ?? "09:00";
  const close = normalizeHHmm(data?.reserveEnd) ?? "18:00";

  const baseStartSlots = useMemo(() => {
    const base = genStartSlots(open, close);
    if (selectedDate === todayYmd()) {
      const now = nowHHmm();
      return base.filter((s) => s.key > now);
    }
    return base;
  }, [open, close, selectedDate]);

  const startSlots = useMemo(() => {
    return baseStartSlots.map((s) => {
      const tmpEnd = calcEndHHmm(s.key, 1);
      const blocked = reservedBlocks.some((b) => overlaps(s.key, tmpEnd, b.start, b.end));
      return { ...s, blocked };
    });
  }, [baseStartSlots, reservedBlocks]);

  const maxHrs = startKey ? maxDurationHours(startKey, close, 4) : 0;
  const endTimeText = (startKey && durationHrs) ? `${startKey} ~ ${calcEndHHmm(startKey, durationHrs)}` : "-";
  const notPastTimeOK = !startKey || !durationHrs || (selectedDate !== todayYmd() ? true : (startKey > nowHHmm()));
  const canReserve = !!selectedDate && !!startKey && !!durationHrs && !selectedDateHasHoliday && notPastTimeOK;

  /* 액션 */
  function applyReserve() {
    if (!canReserve || submitting) return;
    setSubmitting(true);

    createReservation({
      facRevNum,
      facDate: selectedDate,
      startTime: startKey,
      endTime: calcEndHHmm(startKey, durationHrs),
    })
      .then(() => {
        alert("예약 신청이 완료되었습니다.");
        navigate("/facility/list");
      })
      .catch((e) => {
        alert(e?.response?.data?.message || e?.message || "예약 신청 중 오류가 발생했습니다.");
      })
      .finally(() => setSubmitting(false));
  }

  const handleDelete = useCallback(async () => {
    if (!window.confirm("정말 이 시설을 삭제하시겠습니까?")) return;
    try {
      await axios.delete("http://localhost:8090/api/facility/delete", { params: { facRevNum } });
      alert("시설이 삭제되었습니다.");
      navigate("/facility/list");
    } catch (err) {
      alert(
        err?.response?.data ||
        err?.response?.data?.message ||
        err?.message ||
        "삭제 중 오류가 발생했습니다."
      );
    }
  }, [facRevNum, navigate]);

  const isErrorNotice =
    notice &&
    (/(오류|불가|이미|중복|로그인)/.test(notice));

  /* 로딩/에러 카드 */
  if (detailLoading || err) {
    return (
      <div className="max-w-screen-xl mx-auto my-10">
        <div className="min-blank page-shadow bg-white rounded-xl p-10 text-center">
          {err ? <p className="newText-base text-red-600">{err}</p> : <p className="newText-base">불러오는 중...</p>}
        </div>
      </div>
    );
  }

  /* 렌더 */
  return (
    // 최상단 div: max-w-screen-xl mx-auto my-10 고정
    <div className="max-w-screen-xl mx-auto my-10">
      {/* 바로 아래 div: min-blank 고정 */}
      <div className="min-blank">
        {/* ====== 상단 배너 ====== */}
        <header className="page-shadow rounded-xl overflow-hidden mb-6">
          <div className="bg-gradient-to-r from-blue-50 via-sky-50 to-white border border-blue-100 px-6 py-5 flex items-center justify-between">
            <div>
              <h1 className="newText-3xl font-extrabold text-gray-800">공간 상세정보</h1>
              <p className="newText-sm text-gray-500 mt-1">예약 가능 시간과 이용 안내를 확인하고 바로 신청하세요.</p>
            </div>
            {/* 목록으로: 리스트 페이지로 이동 */}
            <button
              className="normal-button newText-sm"
              onClick={() => navigate("/facility/list")}
            >
              목록으로
            </button>
          </div>
        </header>

        {/* 상단: 이미지 / 정보 */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* 이미지 카드 - Swiper 슬라이더 적용 */}
          <div className="page-shadow bg-white rounded-xl overflow-hidden select-none">
            <div className="relative aspect-[4/3] w-full">
              <Swiper
                spaceBetween={16}
                centeredSlides={true}
                autoplay={{ delay: 3500, disableOnInteraction: false }}
                pagination={{ clickable: true }}
                navigation={true}
                modules={[Autoplay, Pagination, Navigation]}
                style={{ width: "100%", height: "100%" }}
                className="w-full h-full"
              >
                {srcs.map((src, i) => (
                  <SwiperSlide key={`${src}-${i}`} className="w-full h-full">
                    <img
                      src={src}
                      alt={`facility-${i + 1}`}
                      className="w-full h-full object-cover"
                      onError={(e) => { e.currentTarget.src = PLACEHOLDER; }}
                      draggable={false}
                    />
                  </SwiperSlide>
                ))}
              </Swiper>
            </div>
          </div>

          {/* 정보 카드 */}
          <div className="page-shadow bg-white rounded-xl p-6">
            <h2 className="newText-2xl font-bold">{data?.facName ?? "공간명"}</h2>
            <p className="newText-base text-gray-600 mt-2">{data?.facInfo ?? "-"}</p>

            <dl className="mt-6 space-y-3">
              <InfoRow label="예약가능시간" value={`${normalizeHHmm(data?.reserveStart) ?? "09:00"} ~ ${normalizeHHmm(data?.reserveEnd) ?? "18:00"}`} />
              <InfoRow label="수용인원" value={data?.capacity != null ? `${data.capacity}명` : "-"} />
              <InfoRow label="구비품목" value={data?.facItem ?? "-"} />
              <InfoRow label="유의사항" value={data?.etc ?? "-"} />
            </dl>

            {isAdmin && (
              <div className="mt-6 grid grid-cols-2 gap-3">
                <button
                  type="button"
                  className="normal-button newText-base"
                  onClick={() => navigate(`/facility/update/${facRevNum}`)}
                >
                  수정
                </button>
                <button
                  type="button"
                  className="negative-button newText-base"
                  onClick={handleDelete}
                >
                  삭제
                </button>
              </div>
            )}

            {notice && (
              <div className={`mt-4 newText-sm ${isErrorNotice ? "text-red-600" : "text-green-700"}`}>
                {notice}
              </div>
            )}
          </div>
        </div>

        {/* 하단: 캘린더 / 예약 패널 */}
        <DetailCalendarSection
          currentMonth={currentMonth}
          setCurrentMonth={setCurrentMonth}
          monthLabel={monthLabel}
          isPrevDisabled={isPrevDisabled}
          isNextDisabled={isNextDisabled}
          weeks={weeks}
          getHolidayItems={getHolidayItems}
          selectedDate={selectedDate}
          handleDayClick={handleDayClick}
          open={open}
          close={close}
          selectedDateHasHoliday={selectedDateHasHoliday}
          startSlots={startSlots}
          startKey={startKey}
          setStartKey={setStartKey}
          durationHrs={durationHrs}
          setDurationHrs={setDurationHrs}
          maxHrs={maxHrs}
          endTimeText={endTimeText}
          canReserve={canReserve}
          applyReserve={applyReserve}
          holLoading={holLoading}
          holError={holError}
          reservedBlocks={reservedBlocks}
          submitting={submitting}
          notice={notice}
          isErrorNotice={isErrorNotice}
        />
      </div>
    </div>
  );
}

/* ---------- 하단 캘린더/예약 컴포넌트 ---------- */
function DetailCalendarSection(props) {
  const {
    currentMonth, setCurrentMonth, monthLabel, isPrevDisabled, isNextDisabled, weeks, getHolidayItems,
    selectedDate, handleDayClick, open, close, selectedDateHasHoliday,
    startSlots, startKey, setStartKey, durationHrs, setDurationHrs, maxHrs,
    endTimeText, canReserve, applyReserve, holLoading, holError, submitting,
    notice, isErrorNotice,
    reservedBlocks,
  } = props;

  const handlePrevClick = useCallback(() => {
    if (!isPrevDisabled) setCurrentMonth(addMonths(currentMonth, -1));
  }, [currentMonth, isPrevDisabled, setCurrentMonth]);

  const handleNextClick = useCallback(() => {
    if (!isNextDisabled) setCurrentMonth(addMonths(currentMonth, 1));
  }, [currentMonth, isNextDisabled, setCurrentMonth]);

  const isToday = (d) => toYmd(d) === todayYmd();
  const isPast = (d) => toYmd(d) < todayYmd();

  const handleDurationClick = useCallback((h) => {
    if (h <= maxHrs) {
      const selectedStartSlot = startSlots.find(s => s.key === startKey);
      if (selectedStartSlot && selectedStartSlot.blocked) return;

      const endHHmm = calcEndHHmm(startKey, h);
      const isOverlapping = reservedBlocks.some(b => overlaps(startKey, endHHmm, b.start, b.end));

      if (isOverlapping) {
        alert("선택한 시간대에 이미 예약이 존재합니다. 다른 시간대를 선택해주세요.");
        return;
      }
      setDurationHrs(h);
    }
  }, [startKey, maxHrs, reservedBlocks, startSlots, setDurationHrs]);

  return (
    <div className="mt-8 flex items-start gap-6">
      {/* 캘린더 카드 */}
      <div className="flex-1 page-shadow bg-white rounded-xl p-5">
        <div className="flex items-center justify-center gap-4 mb-4">
          <button
            onClick={handlePrevClick}
            className={`normal-button newText-base ${isPrevDisabled ? "opacity-40 cursor-not-allowed" : ""}`}
            disabled={isPrevDisabled}
          >
            &lt;
          </button>
          <div className="newText-3xl font-bold tracking-wide">{monthLabel}</div>
          <button
            onClick={handleNextClick}
            className={`normal-button newText-base ${isNextDisabled ? "opacity-40 cursor-not-allowed" : ""}`}
            disabled={isNextDisabled}
          >
            &gt;
          </button>
        </div>

        <div className="grid grid-cols-7 text-center newText-sm font-semibold text-gray-500 mb-2">
          {["일", "월", "화", "수", "목", "금", "토"].map((d, i) => (
            <div key={i} className={i === 0 ? "text-red-500" : i === 6 ? "text-blue-500" : ""}>{d}요일</div>
          ))}
        </div>

        <div className="grid grid-cols-7 gap-[1px] bg-gray-200 rounded-lg overflow-hidden">
          {weeks.flatMap((week, wi) =>
            week.map((d, di) => {
              const ymd = toYmd(d);
              const inMonth = isSameMonth(d, currentMonth);
              const items = getHolidayItems(d);
              const isHoliday = items.length > 0;
              const isSelected = selectedDate === ymd;
              const isDisabled = !inMonth || isPast(d);

              return (
                <button
                  key={`${wi}-${di}`}
                  type="button"
                  onClick={() => handleDayClick(d)}
                  className={[
                    "h-[96px] bg-white text-left p-2 relative focus:outline-none",
                    "transition",
                    isDisabled ? "opacity-40 cursor-not-allowed" : "hover:bg-gray-50 cursor-pointer",
                    isHoliday && "ring-2 ring-red-300",
                    isSelected && "outline outline-2 outline-blue-400",
                  ].filter(Boolean).join(" ")}
                  disabled={isDisabled}
                >
                  <div className={`absolute top-2 right-2 newText-sm ${di === 0 ? "text-red-500" : di === 6 ? "text-blue-500" : "text-gray-600"}`}>
                    {format(d, "d")}
                  </div>
                  {isToday(d) && <span className="absolute left-2 top-2 newText-xs text-blue-600">오늘</span>}
                  <div className="mt-6 flex flex-col gap-1">
                    {items.map((it, i2) => (
                      <span
                        key={i2}
                        className={`inline-block w-fit newText-xs px-2 py-0.5 rounded-full ${it.type === "PUBLIC" ? "bg-red-100 text-red-700" : "bg-amber-100 text-amber-700"}`}
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

        {holLoading && <div className="mt-3 newText-sm text-gray-500">휴무일 불러오는 중...</div>}
        {holError && <div className="mt-2 newText-sm text-red-500">{holError}</div>}
      </div>

      {/* 예약 패널 */}
      <aside className="w-full max-w-[360px] sticky top-6 page-shadow bg-white rounded-xl p-5">
        <div className="newText-base text-blue-600 font-bold">예약 정보</div>

        <dl className="mt-3 space-y-2 newText-sm">
          <Row label="예약일" value={selectedDate ?? "-"} />
          <Row label="예약가능" value={`${open} ~ ${close}`} />
          <Row label="규칙" value="하루 1회, 최대 4시간 연속 사용" />
        </dl>

        <div className="mt-4">
          <div className="newText-sm font-medium text-gray-700 mb-2">시작시간</div>
          <div className="grid grid-cols-2 gap-2">
            {startSlots.map((s) => {
              const disabled = !selectedDate || selectedDateHasHoliday || s.blocked;
              const selected = startKey === s.key;
              return (
                <label
                  key={s.key}
                  className={[
                    "rounded-md border text-center py-2 transition newText-sm",
                    selected ? "positive-button" : "normal-button",
                    disabled ? "opacity-50 cursor-not-allowed" : "",
                  ].join(" ")}
                  title={s.blocked ? "이미 예약된 시간입니다" : undefined}
                >
                  <input
                    type="radio"
                    name="start"
                    value={s.key}
                    checked={selected}
                    onChange={() => setStartKey(s.key)}
                    disabled={disabled}
                    className="sr-only"
                  />
                  {s.label}{s.blocked && " · 예약됨"}
                </label>
              );
            })}
          </div>
        </div>

        <div className="mt-4">
          <div className="newText-sm font-medium text-gray-700 mb-2">이용시간</div>
          <div className="grid grid-cols-4 gap-2">
            {[1, 2, 3, 4].map((h) => {
              const allowed = startKey && (h <= maxHrs);
              const selected = durationHrs === h;
              return (
                <button
                  key={h}
                  type="button"
                  aria-pressed={selected}
                  className={[
                    "rounded-md text-center py-2 transition newText-sm",
                    selected ? "positive-button" : "normal-button",
                    !allowed ? "opacity-50 cursor-not-allowed" : "",
                  ].join(" ")}
                  onClick={() => allowed && handleDurationClick(h)}
                  disabled={!allowed}
                >
                  {h}시간
                </button>
              );
            })}
          </div>

          <div className="mt-2 newText-sm text-gray-600">
            선택한 시간: <span className="font-medium text-gray-900">{startKey && durationHrs ? endTimeText : "-"}</span>
          </div>
        </div>

        <button
          className={`mt-5 w-full newText-base ${canReserve ? "positive-button" : "normal-button opacity-50 cursor-not-allowed"}`}
          onClick={applyReserve}
          disabled={!canReserve || submitting}
        >
          {submitting ? "전송 중..." : "예약 신청하기"}
        </button>

        {notice && (
          <div className={`mt-3 newText-sm ${isErrorNotice ? "text-red-600" : "text-green-700"}`}>
            {notice}
          </div>
        )}
      </aside>
    </div>
  );
}

/* ---------- 소형 컴포넌트 ---------- */
function InfoRow({ label, value }) {
  return (
    <div className="grid grid-cols-[110px_1fr] items-start gap-x-3">
      <dt className="newText-sm text-gray-500">{label}</dt>
      <dd className="newText-base text-gray-800 whitespace-pre-line">{value}</dd>
    </div>
  );
}
function Row({ label, value }) {
  return (
    <div className="grid grid-cols-3">
      <dt className="col-span-1 newText-sm text-gray-500">{label}</dt>
      <dd className="col-span-2 newText-sm text-gray-900">{value}</dd>
    </div>
  );
}
