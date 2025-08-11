// src/components/FacilityDetailContent.jsx
import React, { useEffect, useMemo, useRef, useState } from "react";
import { getFacilityDetail } from "../../api/facilityApi";

// 리스트와 동일 규칙
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

export default function FacilityDetailContent({ facRevNum }) {
  const [data, setData] = useState(null);
  const [idx, setIdx] = useState(0);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");
  const startX = useRef(null);

  const srcs = useMemo(() => {
    const raw = data?.images ?? [];
    const arr = (Array.isArray(raw) && raw.length ? raw : [null]).map(buildImageUrl);
    const cleaned = arr.filter(Boolean);
    return cleaned.length ? cleaned : [PLACEHOLDER];
  }, [data]);

  const n = srcs.length;

  useEffect(() => {
    let mounted = true;
    setLoading(true);
    setErr("");
    getFacilityDetail(facRevNum)
      .then((d) => {
        if (!mounted) return;
        setData(d);
        setIdx(0);
      })
      .catch((e) => {
        if (!mounted) return;
        setErr(e?.response?.data?.message || e.message || "시설 정보를 불러오지 못했습니다.");
      })
      .finally(() => mounted && setLoading(false));
    return () => { mounted = false; };
  }, [facRevNum]);

  const prev = () => setIdx((i) => (i - 1 + n) % n);
  const next = () => setIdx((i) => (i + 1) % n);

  const onTouchStart = (e) => (startX.current = e.touches[0].clientX);
  const onTouchEnd = (e) => {
    if (startX.current == null) return;
    const dx = e.changedTouches[0].clientX - startX.current;
    if (Math.abs(dx) > 40) (dx > 0 ? prev() : next());
    startX.current = null;
  };

  useEffect(() => {
    const onKey = (e) => {
      if (e.key === "ArrowLeft") prev();
      if (e.key === "ArrowRight") next();
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [n]);

  if (loading) {
    return (
      <div className="mt-6 grid grid-cols-1 lg:grid-cols-2 gap-8">
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
    return (
      <div className="mt-6 rounded-xl border border-red-200 bg-red-50 p-6 text-red-800">
        {err}
      </div>
    );
  }

  return (
    <div className="mt-6 grid grid-cols-1 lg:grid-cols-2 gap-8">
      {/* 좌측: 이미지 캐러셀 (자연 비율 유지) */}
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
            className="w-full h-auto max-h-[520px] object-contain" // 🔧 핵심: 비율 유지 + 최대 높이 제한
            onError={(e) => (e.currentTarget.src = PLACEHOLDER)}
            draggable={false}
          />
        </div>

        {/* 컨트롤 */}
        {srcs.length > 1 && (
          <>
            <button
              aria-label="이전 이미지"
              onClick={prev}
              className="absolute left-2 top-1/2 -translate-y-1/2 bg-white/80 hover:bg-white rounded-full w-9 h-9 flex items-center justify-center shadow"
            >
              ‹
            </button>
            <button
              aria-label="다음 이미지"
              onClick={next}
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

      {/* 우측: 상세 정보 */}
      <div className="p-2">
        <h2 className="text-lg font-semibold">{data?.facName ?? "시설명"}</h2>
        <p className="mt-1 text-gray-500">{data?.facInfo ?? "-"}</p>

        <dl className="mt-6 space-y-3">
          <Row label="예약가능시간" value={data?.availableTime ?? "-"} />
          <Row label="수용인원" value={data?.capacity != null ? `${data.capacity}명` : "-"} />
          <Row label="구비품목" value={data?.facItem ?? "-"} />
          <Row label="유의사항" value={data?.etc ?? "-"} />
        </dl>
      </div>
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
