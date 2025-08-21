import { useEffect, useState } from "react";
import { getSearchList } from "../../api/eventApi";
import { useNavigate } from "react-router-dom";
import useMove from "../../hooks/useMove";
import { useSelector } from "react-redux";

// 0-based 페이지네이션 컴포넌트
import PageComponent from "../common/PageComponent";

const EventListComponent = () => {
  // 데이터 및 페이지 상태
  const [events, setEvents] = useState([]);
  const [page, setPage] = useState(1); // API는 1-based로 유지
  const [totalPages, setTotalPages] = useState(1);

  // 검색/필터 상태
  const [keyword, setKeyword] = useState("");
  const [searchType, setSearchType] = useState("eventName");
  const [state, setState] = useState("");
  const [category, setCategory] = useState("");
  const [sortOrder, setSortOrder] = useState("DESC");
  const [searchTrigger, setSearchTrigger] = useState(false);

  // 전역
  const navigate = useNavigate();
  const host = "http://localhost:8090/view";
  const { moveToLogin } = useMove();
  const loginState = useSelector((s) => s.loginState);

  // 목록 조회
  useEffect(() => {
    const fetchData = async () => {
      const data = await getSearchList({
        page, // 그대로 1-based 전달
        searchType,
        keyword,
        state,
        category,
        sortOrder,
      });
      setEvents(data.content);
      setTotalPages(data.totalPages);
    };
    fetchData();
  }, [page, state, category, sortOrder, searchTrigger, searchType, keyword]);

  // 날짜 포맷
  const formatDate = (dateStr) => {
    if (!dateStr) return "";
    const d = new Date(dateStr);
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, "0");
    const day = String(d.getDate()).padStart(2, "0");
    const hh = String(d.getHours()).padStart(2, "0");
    const mm = String(d.getMinutes()).padStart(2, "0");
    return `${y}.${m}.${day} ${hh}:${mm}`;
  };

  // 상태 라벨/스타일
  const getApplyStatus = (event) => {
    if (event.revState === "APPROVED") return { text: "신청 완료", style: "green-button" };
    if (event.revState === "CANCEL") return { text: "신청 취소", style: "nagative-button" };

    const now = new Date();
    const start = new Date(event.applyStartPeriod);
    const end = new Date(event.applyEndPeriod);

    if (now < start) return { text: "신청 시작 전", style: "normal-button" };
    if (now <= end) return { text: "신청 가능", style: "positive-button" };
    // 종료 상태는 normal-button을 기본으로 하고, 시각적 구분을 위해 약한 회색 톤으로 보강
    return {
      text: "신청 종료",
      style: "normal-button !bg-gray-400 !text-white !border-gray-500 cursor-not-allowed",
    };
  };

  // 카드 클릭 시 이동 (로그인 필요)
  const handleCardClick = (eventNum) => {
    if (loginState && loginState.memId) {
      navigate(`/event/detail/${eventNum}`);
    } else {
      alert("로그인이 필요합니다.");
      moveToLogin();
    }
  };

  // 검색 실행
  const handleSearch = () => {
    setPage(1); // 검색 시 1페이지로
    setSearchTrigger((p) => !p);
  };

  return (
    // 최상단 레이아웃: 고정 클래스 사용
    <div className="max-w-screen-xl mx-auto my-10">
      {/* 좌우 여백: 고정 클래스 사용 (헤더/콘텐츠 모두 포함) */}
      <div className="min-blank">
        {/* 페이지 컨테이너: 페이지 그림자 적용 */}
          {/* 헤더 */}
          <div className="text-center mb-8">
            <h1 className="newText-3xl font-bold">프로그램 신청</h1>
          </div>

          {/* 카테고리 필터 */}
          <div className="flex gap-2 mb-6 justify-center">
            {[
              { label: "전체", value: "" },
              { label: "교사", value: "TEACHER" },
              { label: "학생참여", value: "STUDENT" },
              { label: "시민참여", value: "USER" },
            ].map(({ label, value }) => {
              const isSelected = category === value;
              return (
                <button
                  key={value}
                  onClick={() => {
                    setCategory(value);
                    setPage(1);
                  }}
                  className={`normal-button newText-sm px-4 py-1 rounded-full border transition-all duration-150
                    ${
                      isSelected
                        ? "!bg-blue-600 !text-white !border-blue-600 font-semibold shadow-sm ring-2 ring-blue-200"
                        : "bg-white text-gray-800 border-gray-300 hover:bg-gray-100"
                    }`}
                  aria-pressed={isSelected}
                >
                  {label}
                </button>
              );
            })}
          </div>

          {/* 검색/정렬 필터 */}
          <div className="flex flex-wrap gap-2 mb-8 items-center justify-center">
            <select
              className="input-focus newText-base p-2 rounded"
              value={searchType}
              onChange={(e) => setSearchType(e.target.value)}
            >
              <option className="newText-base" value="eventName">프로그램명</option>
              <option className="newText-base" value="eventInfo">내용</option>
              <option className="newText-base" value="all">전체</option>
            </select>

            <input
              type="text"
              placeholder="검색어를 입력하세요"
              className="input-focus newText-base p-2 rounded w-60"
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") handleSearch();
              }}
            />

            <select
              className="input-focus newText-base p-2 rounded"
              value={state}
              onChange={(e) => {
                setState(e.target.value);
                setPage(1);
              }}
            >
              <option className="newText-base" value="">전체 상태</option>
              <option className="newText-base" value="BEFORE">신청전</option>
              <option className="newText-base" value="OPEN">신청중</option>
              <option className="newText-base" value="CLOSED">신청마감</option>
            </select>

            <select
              className="input-focus newText-base p-2 rounded"
              value={sortOrder}
              onChange={(e) => {
                setSortOrder(e.target.value);
                setPage(1);
              }}
            >
              <option className="newText-base" value="DESC">최신순</option>
              <option className="newText-base" value="ASC">오래된순</option>
            </select>

            <button
              onClick={handleSearch}
              className="positive-button newText-base px-4 py-2 rounded"
            >
              검색
            </button>
          </div>

          {/* 프로그램 카드 목록 */}
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-6">
            {events.map((event) => {
              const status = getApplyStatus(event);
              const categoryLabel =
                event.category === "TEACHER"
                  ? "교사"
                  : event.category === "STUDENT"
                  ? "학생참여"
                  : "시민참여";

              return (
                <div
                  key={event.eventNum}
                  className="group shadow-lg rounded-lg p-4 hover:shadow-2xl transition cursor-pointer bg-white"
                  onClick={() => handleCardClick(event.eventNum)}
                >
                  {/* 카테고리 배지 */}
                  <div className="newText-sm bg-blue-100 text-blue-700 inline-block px-2 py-1 rounded-full mb-2">
                    {categoryLabel}
                  </div>

                  {/* 대표 이미지 또는 플레이스홀더 */}
                  <div className="w-full h-40 rounded mb-2 overflow-hidden">
                    {event.mainImagePath && /\.(jpg|jpeg|png|gif|webp)$/i.test(event.mainImagePath) ? (
                      <img
                        src={`${host}/${event.mainImagePath}`}
                        alt="대표 이미지"
                        className="w-full h-full object-cover transition-transform duration-300 ease-out transform-gpu group-hover:scale-[1.1]"
                      />
                    ) : (
                      <div className="w-full h-full bg-gray-200 flex items-center justify-center newText-sm text-gray-500
                                      transition-transform duration-300 ease-out transform-gpu group-hover:scale-[1.03]">
                        이미지 없음
                      </div>
                    )}
                  </div>
                  {/* 제목 및 정보 */}
                  <h3 className="newText-lg font-semibold mb-1 line-clamp-2">
                    {event.eventName}
                  </h3>
                  <p className="newText-sm text-gray-500 mb-1">
                    신청기간: {formatDate(event.applyStartPeriod)} ~ {formatDate(event.applyEndPeriod)}
                  </p>
                  <p className="newText-sm text-gray-500 mb-3">
                    모집인원: {event.maxCapacity}명
                  </p>

                  {/* 상태 표시 */}
                  <div
                    className={`newText-sm text-center w-full px-3 py-2 rounded font-semibold ${status.style}`}
                  >
                    {status.text}
                  </div>
                </div>
              );
            })}
          </div>

          {/* 페이지네이션 (공용 컴포넌트 사용: 0-based) */}
          <div className="mt-6 flex justify-center">
            <PageComponent
              totalPages={totalPages}                 // 총 페이지 수 (정수)
              current={page - 1}                      // 0-based로 변환
              setCurrent={(idx) => setPage(idx + 1)}  // 1-based로 환산하여 set
            />
          </div>
      </div>
    </div>
  );
};

export default EventListComponent;