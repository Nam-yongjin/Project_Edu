// src/pages/facility/FacilityListComponent.jsx
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { FacilityList } from "../../api/facilityApi";

const PLACEHOLDER = "/placeholder.svg";
const host = "http://localhost:8090/view";

// ✅ 이미지 경로 안전 조립 헬퍼 (문자열/객체 모두 지원)
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

const FacilityListComponent = () => {
  const navigate = useNavigate();

  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const size = 12;

  const [list, setList] = useState([]);
  const [keyword, setKeyword] = useState("");
  const [searchTrigger, setSearchTrigger] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchList = async () => {
      setLoading(true);
      setError("");
      try {
        const res = await FacilityList({
          page: Math.max(page - 1, 0),
          size,
          keyword: keyword.trim(),
        });
        const content = Array.isArray(res) ? res : res?.content ?? [];
        setList(content);
        setTotalPages(res?.totalPages ?? 1);
      } catch (e) {
        setError(e?.response?.data?.message || e.message || "목록 조회 실패");
        console.error(e);
      } finally {
        setLoading(false);
      }
    };
    fetchList();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, searchTrigger]);

  const handleSearch = () => {
    setPage(1);
    setSearchTrigger((v) => !v);
  };

  const pageBlockSize = 10;
  const currentBlock = Math.floor((page - 1) / pageBlockSize);
  const blockStart = currentBlock * pageBlockSize + 1;
  const blockEnd = Math.min(blockStart + pageBlockSize - 1, totalPages);

  return (
    <div className="max-w-7xl mx-auto px-4 py-10">
      <h1 className="text-3xl font-bold text-center mb-10">공간/체험 신청</h1>

      {/* 검색 */}
      <div className="flex flex-wrap gap-2 mb-6 items-center justify-center">
        <input
          type="text"
          placeholder="시설명/소개로 검색"
          className="border p-2 rounded w-60"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && handleSearch()}
        />
        <button onClick={handleSearch} className="bg-blue-500 text-white px-4 py-2 rounded">
          검색
        </button>
      </div>

      {error && <div className="text-center text-red-600 mb-4">{error}</div>}

      {/* 카드 목록 */}
      {loading ? (
        <div className="text-center py-16">불러오는 중…</div>
      ) : list.length === 0 ? (
        <div className="text-center py-16 text-gray-500">등록된 시설이 없습니다.</div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {list.map((item) => (
            <FacilityCard
              key={item.facRevNum}
              item={item}
              onCardClick={() => navigate(`/facility/detail/${item.facRevNum}`)}
              onApplyClick={() => navigate(`/facility/detail/${item.facRevNum}`)}
            />
          ))}
        </div>
      )}

      {/* 페이지네이션 */}
      {totalPages > 1 && (
        <div className="mt-6 flex justify-center gap-2 text-blue-600 font-semibold">
          {blockStart > 1 && <button onClick={() => setPage(blockStart - 1)}>{"<"}</button>}
          {Array.from({ length: blockEnd - blockStart + 1 }, (_, i) => {
            const p = blockStart + i;
            return (
              <button
                key={p}
                onClick={() => setPage(p)}
                className={page === p ? "underline text-blue-800" : "hover:text-blue-800"}
              >
                {p}
              </button>
            );
          })}
          {blockEnd < totalPages && <button onClick={() => setPage(blockEnd + 1)}>{">"}</button>}
        </div>
      )}
    </div>
  );
};

const FacilityCard = ({ item, onCardClick, onApplyClick }) => {
  const { facName, facInfo, capacity } = item;
  const images = Array.isArray(item.images) ? item.images.filter(Boolean) : [];
  const srcs = (images.length ? images : [null]).map(buildImageUrl);

  return (
    <div
      className="border rounded-lg shadow hover:shadow-lg transition bg-white overflow-hidden cursor-pointer"
      onClick={onCardClick}
    >
      <ImageSlider images={srcs} alt={facName} />
      <div className="p-4 flex flex-col gap-2">
        <h3 className="text-lg font-semibold leading-snug">{facName}</h3>
        <p className="text-sm text-gray-600 min-h-[42px]">
          {facInfo?.length > 90 ? `${facInfo.slice(0, 90)}…` : facInfo || ""}
        </p>
        <p className="text-sm text-gray-700">(수용인원 : {Number(capacity) || 0}명)</p>
        <button
          type="button"
          onClick={(e) => {
            e.stopPropagation();
            onApplyClick();
          }}
          className="mt-2 w-full bg-indigo-600 text-white py-2 rounded hover:bg-indigo-700"
        >
          신청하기
        </button>
      </div>
    </div>
  );
};

/**
 * 🔧 리스트 썸네일 슬라이더
 * - 컨테이너: 4:3 고정 비율 → pb-[75%]
 * - 내부 트랙: absolute + translateX
 * - 각 슬라이드: absolute fill + object-cover (비율 유지하며 꽉 채움)
 */
const ImageSlider = ({ images = [], alt = "facility" }) => {
  const [idx, setIdx] = useState(0);
  const [startX, setStartX] = useState(null);
  const [dragging, setDragging] = useState(false);

  const prev = () => setIdx((i) => (i === 0 ? images.length - 1 : i - 1));
  const next = () => setIdx((i) => (i === images.length - 1 ? 0 : i + 1));

  const onTouchStart = (e) => { setStartX(e.touches[0].clientX); setDragging(true); };
  const onTouchEnd = (e) => {
    if (!dragging || startX == null) return;
    const d = e.changedTouches[0].clientX - startX;
    if (d < -40) next();
    if (d > 40) prev();
    setDragging(false); setStartX(null);
  };
  const onMouseDown = (e) => { setStartX(e.clientX); setDragging(true); };
  const onMouseUp = (e) => {
    if (!dragging || startX == null) return;
    const d = e.clientX - startX;
    if (d < -40) next();
    if (d > 40) prev();
    setDragging(false); setStartX(null);
  };

  // 비율 컨테이너 (4:3)
  return (
    <div
      className="relative w-full pb-[75%] bg-gray-100 select-none overflow-hidden"
      onTouchStart={onTouchStart}
      onTouchEnd={onTouchEnd}
      onMouseDown={onMouseDown}
      onMouseUp={onMouseUp}
    >
      {/* 트랙 */}
      <div
        className="absolute inset-0 h-full flex transition-transform duration-500"
        style={{ transform: `translateX(-${idx * 100}%)`, width: `${images.length * 100}%` }}
      >
        {images.map((src, i) => (
          <div key={`${src}-${i}`} className="relative w-full h-full shrink-0">
            {/* 슬라이드 영역 채우기 */}
            <img
              src={src}
              alt={`${alt}-${i + 1}`}
              className="absolute inset-0 w-full h-full object-cover block"
              loading="lazy"
              onError={(e) => { e.currentTarget.src = PLACEHOLDER; }}
            />
          </div>
        ))}
      </div>

      {images.length > 1 && (
        <>
          <button
            type="button"
            onClick={(e) => { e.stopPropagation(); prev(); }}
            className="absolute left-2 top-1/2 -translate-y-1/2 bg-white/80 hover:bg-white rounded-full w-8 h-8 flex items-center justify-center shadow"
            aria-label="이전 이미지"
          >
            ‹
          </button>
          <button
            type="button"
            onClick={(e) => { e.stopPropagation(); next(); }}
            className="absolute right-2 top-1/2 -translate-y-1/2 bg-white/80 hover:bg-white rounded-full w-8 h-8 flex items-center justify-center shadow"
            aria-label="다음 이미지"
          >
            ›
          </button>

          <div className="absolute bottom-2 left-1/2 -translate-x-1/2 flex gap-1">
            {images.map((_, i) => (
              <button
                key={i}
                type="button"
                onClick={(e) => { e.stopPropagation(); setIdx(i); }}
                className={`w-2.5 h-2.5 rounded-full ${i === idx ? "bg-white" : "bg-white/50"} ring-1 ring-black/10`}
                aria-label={`이미지 ${i + 1}`}
              />
            ))}
          </div>
        </>
      )}
    </div>
  );
};

export default FacilityListComponent;