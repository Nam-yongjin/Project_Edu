// src/pages/facility/FacilityListComponent.jsx
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { FacilityList } from "../../api/facilityApi";
import useMove from "../../hooks/useMove";
import { useSelector } from "react-redux";
import PageComponent from "../common/PageComponent";

const PLACEHOLDER = "/placeholder.svg";
const host = "http://localhost:8090/view";

/** 이미지 경로 안전 조립 (문자열/객체 모두 지원) */
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
  const { moveToLogin } = useMove();
  const loginState = useSelector((s) => s.loginState);

  // 1-based 로컬 페이지 (PageComponent는 0-based이므로 변환해서 전달)
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const size = 8; // ✅ 한 페이지 8개 고정

  const [list, setList] = useState([]);
  const [keyword, setKeyword] = useState("");
  const [searchTrigger, setSearchTrigger] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // 목록 가져오기
  useEffect(() => {
    const fetchList = async () => {
      setLoading(true);
      setError("");
      try {
        const res = await FacilityList({
          page: Math.max(page - 1, 0), // API 0-based
          size,
          keyword: keyword.trim(),
        });

        // axios 래퍼 차이 대응
        const data = res?.data ?? res;
        const content = Array.isArray(data) ? data : data?.content ?? [];
        const tp = Math.max(1, data?.totalPages ?? 1);

        setList(content);
        setTotalPages(tp);

        // 현재 페이지가 총 페이지보다 크면 마지막 페이지로 보정
        if (page > tp) setPage(tp);
      } catch (e) {
        setError(e?.response?.data?.message || e.message || "목록 조회에 실패했습니다.");
        setList([]);
        setTotalPages(1);
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

  return (
    <div className="max-w-screen-xl mx-auto my-10">
      <div className="min-blank">
        {/* 타이틀 */}
        <h1 className="newText-3xl font-bold text-center mb-8">공간/체험 신청</h1>

        {/* 검색 */}
        <div className="flex flex-wrap gap-2 mb-6 items-center justify-center">
          <input
            type="text"
            placeholder="공간명/소개로 검색"
            className="input-focus newText-base p-2 rounded w-60"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleSearch()}
          />
          <button onClick={handleSearch} className="positive-button newText-base px-4 py-2 rounded">
            검색
          </button>
        </div>

        {/* 오류 */}
        {error && <div className="text-center newText-base text-red-600 mb-4">{error}</div>}

        {/* 카드 목록 */}
        {loading ? (
          <div className="text-center py-16 newText-base text-gray-600">불러오는 중…</div>
        ) : list.length === 0 ? (
          <div className="text-center py-16 newText-base text-gray-500">등록된 공간이 없습니다.</div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {list.map((item) => (
              <FacilityCard
                key={item.facRevNum}
                item={item}
                onCardClick={() => {
                  if (loginState && loginState.memId) {
                    navigate(`/facility/detail/${item.facRevNum}`);
                  } else {
                    alert("로그인이 필요합니다.");
                    moveToLogin();
                  }
                }}
                onApplyClick={() => {
                  if (loginState && loginState.memId) {
                    navigate(`/facility/detail/${item.facRevNum}`);
                  } else {
                    alert("로그인이 필요합니다.");
                    moveToLogin();
                  }
                }}
              />
            ))}
          </div>
        )}

        {/* 페이지네이션 (공용 0-based 컴포넌트 사용) */}
        {totalPages > 1 && (
          <div className="mt-8 flex justify-center">
            <PageComponent
              totalPages={totalPages}
              current={page - 1}                 // 0-based로 전달
              setCurrent={(idx) => setPage(idx + 1)} // 콜백은 1-based로 환산
            />
          </div>
        )}
      </div>
    </div>
  );
};

const FacilityCard = ({ item, onCardClick, onApplyClick }) => {
  const { facName, facInfo, capacity } = item;
  const images = Array.isArray(item.images) ? item.images.filter(Boolean) : [];
  const srcs = (images.length ? images : [null]).map(buildImageUrl);

  return (
    <div
      className="page-shadow rounded-xl bg-white overflow-hidden hover:shadow-lg transition cursor-pointer"
      onClick={onCardClick}
    >
      <ImageSlider images={srcs} alt={facName} />
      <div className="p-4 flex flex-col gap-2">
        <h3 className="newText-lg font-semibold leading-snug line-clamp-2">{facName}</h3>
        <p className="newText-sm text-gray-600 min-h-[42px]">
          {facInfo?.length > 90 ? `${facInfo.slice(0, 90)}…` : facInfo || ""}
        </p>
        <p className="newText-sm text-gray-700">(수용인원 : {Number(capacity) || 0}명)</p>

        <button
          type="button"
          onClick={(e) => {
            e.stopPropagation();
            onApplyClick();
          }}
          className="positive-button mt-2 w-full newText-base"
        >
          신청하기
        </button>
      </div>
    </div>
  );
};

/** 리스트 썸네일 슬라이더 (4:3 비율) */
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

  return (
    <div
      className="relative w-full pb-[75%] bg-gray-100 select-none overflow-hidden"
      onTouchStart={onTouchStart}
      onTouchEnd={onTouchEnd}
      onMouseDown={onMouseDown}
      onMouseUp={onMouseUp}
    >
      {/* 슬라이드 트랙 */}
      <div
        className="absolute inset-0 h-full flex transition-transform duration-500"
        style={{ transform: `translateX(-${idx * 100}%)`, width: `${images.length * 100}%` }}
      >
        {images.map((src, i) => (
          <div key={`${src}-${i}`} className="relative w-full h-full shrink-0">
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
            className="normal-button !px-2 !py-0.5 rounded-full absolute left-2 top-1/2 -translate-y-1/2"
            aria-label="이전 이미지"
          >
            ‹
          </button>
          <button
            type="button"
            onClick={(e) => { e.stopPropagation(); next(); }}
            className="normal-button !px-2 !py-0.5 rounded-full absolute right-2 top-1/2 -translate-y-1/2"
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
                className={`w-2.5 h-2.5 rounded-full ${i === idx ? "bg-white" : "bg-white/60"} ring-1 ring-black/10`}
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
