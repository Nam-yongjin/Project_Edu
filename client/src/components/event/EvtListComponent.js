import { useEffect, useState } from "react";
import { getSearchList } from "../../api/eventApi";
import { useNavigate } from "react-router-dom";

const EventListComponent = () => {
  const [events, setEvents] = useState([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  // 검색/필터 조건 상태
  const [keyword, setKeyword] = useState("");
  const [searchType, setSearchType] = useState("eventName");
  const [state, setState] = useState("");
  const [category, setCategory] = useState("");
  const [sortOrder, setSortOrder] = useState("DESC");

  const [searchTrigger, setSearchTrigger] = useState(false); // 검색 버튼 클릭 시 트리거

  const navigate = useNavigate();
  const host = "http://localhost:8090/view";

  // 검색 API 호출 (keyword는 제외)
  useEffect(() => {
  const fetchData = async () => {
    const data = await getSearchList({
      page,
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

// eslint-disable-next-line react-hooks/exhaustive-deps
}, [page, state, category, sortOrder, searchTrigger]);

  const formatDate = (dateStr) => {
    if (!dateStr) return "";
    const date = new Date(dateStr);
    return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, "0")}.${String(date.getDate()).padStart(2, "0")}`;
  };

  const getApplyStatus = (event) => {
    if (event.revState === "APPROVED") return { text: "신청 완료", style: "bg-green-500" };
    if (event.revState === "CANCEL") return { text: "신청 취소", style: "bg-red-500" };

    const now = new Date();
    const start = new Date(event.applyStartPeriod);
    const end = new Date(event.applyEndPeriod);

    if (now < start) return { text: "신청 시작 전", style: "bg-gray-400" };
    if (now <= end) return { text: "신청 가능", style: "bg-blue-600" };
    return { text: "신청 종료", style: "bg-gray-500" };
  };

  const handleCardClick = (eventNum) => {
    navigate(`/event/detail/${eventNum}`);
  };

  // 검색 버튼 클릭 시에만 keyword 검색
  const handleSearch = () => {
    setPage(1);
    setSearchTrigger((prev) => !prev); // useEffect 트리거
  };

  const pageBlockSize = 10;
  const currentBlock = Math.floor((page - 1) / pageBlockSize);
  const blockStart = currentBlock * pageBlockSize + 1;
  const blockEnd = Math.min(blockStart + pageBlockSize - 1, totalPages);

  return (
    <div className="max-w-6xl mx-auto mt-10">
      {/* 카테고리 필터 */}
      <div className="flex gap-2 mb-6 justify-center">
        {[
          { label: "전체", value: "" },
          { label: "교사", value: "TEACHER" },
          { label: "학생참여", value: "STUDENT" },
          { label: "시민참여", value: "USER" },
        ].map(({ label, value }) => (
          <button
            key={value}
            onClick={() => {
              setCategory(value);
              setPage(1);
            }}
            className={`px-4 py-1 rounded-full border ${category === value ? "bg-gray-700 text-white" : "bg-white text-gray-700"}`}
          >
            {label}
          </button>
        ))}
      </div>

      {/* 검색 필터 + 정렬 */}
      <div className="flex flex-wrap gap-2 mb-6 items-center justify-center">
        <select className="border p-2 rounded" value={searchType} onChange={(e) => setSearchType(e.target.value)}>
          <option value="eventName">행사명</option>
          <option value="eventInfo">내용</option>
          <option value="all">전체</option>
        </select>

        <input
          type="text"
          placeholder="검색어를 입력하세요"
          className="border p-2 rounded w-60"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
        />

        {/* 신청 상태 드롭다운 (신청취소 제외) */}
        <select
          className="border p-2 rounded"
          value={state}
          onChange={(e) => {
            setState(e.target.value);
            setPage(1);
          }}
        >
          <option value="">전체 상태</option>
          <option value="BEFORE">신청전</option>
          <option value="OPEN">신청중</option>
          <option value="CLOSED">신청마감</option>
        </select>

        <select
          className="border p-2 rounded"
          value={sortOrder}
          onChange={(e) => {
            setSortOrder(e.target.value);
            setPage(1);
          }}
        >
          <option value="DESC">최신순</option>
          <option value="ASC">오래된순</option>
        </select>

        <button onClick={handleSearch} className="bg-blue-500 text-white px-4 py-2 rounded">
          검색
        </button>
      </div>

      {/* 행사 카드 목록 */}
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-6">
        {events.map((event) => {
          const status = getApplyStatus(event);
          return (
            <div
              key={event.eventNum}
              className="border rounded-lg shadow p-4 hover:shadow-lg transition cursor-pointer"
              onClick={() => handleCardClick(event.eventNum)}
            >
              <div className="text-sm bg-blue-100 text-blue-700 inline-block px-2 py-1 rounded-full mb-2">
                {event.category === "TEACHER"
                  ? "교사"
                  : event.category === "STUDENT"
                  ? "학생"
                  : "일반인"}
              </div>

              {event.mainImagePath && /\.(jpg|jpeg|png|gif)$/i.test(event.mainImagePath) ? (
                <img
                  src={`${host}/${event.mainImagePath}`}
                  alt="대표 이미지"
                  className="w-full h-40 object-cover rounded mb-2"
                />
              ) : (
                <div className="w-full h-40 bg-gray-200 flex items-center justify-center rounded mb-2 text-sm text-gray-500">
                  이미지 없음
                </div>
              )}

              <h3 className="text-lg font-semibold mb-1 line-clamp-2">{event.eventName}</h3>
              <p className="text-sm text-gray-500 mb-1">
                신청기간: {formatDate(event.applyStartPeriod)} ~ {formatDate(event.applyEndPeriod)}
              </p>
              <p className="text-sm text-gray-500 mb-3">모집인원: {event.maxCapacity}명</p>

              <div className={`text-white px-3 py-1 rounded font-semibold text-sm text-center w-full ${status.style}`}>
                {status.text}
              </div>
            </div>
          );
        })}
      </div>

      {/* 페이지네이션 */}
      <div className="mt-6 flex justify-center gap-2 text-blue-600 font-semibold">
        {blockStart > 1 && <button onClick={() => setPage(blockStart - 1)}>{"<"}</button>}

        {Array.from({ length: blockEnd - blockStart + 1 }, (_, idx) => {
          const pageNum = blockStart + idx;
          return (
            <button
              key={pageNum}
              onClick={() => setPage(pageNum)}
              className={page === pageNum ? "underline text-blue-800" : "hover:text-blue-800"}
            >
              {pageNum}
            </button>
          );
        })}

        {blockEnd < totalPages && <button onClick={() => setPage(blockEnd + 1)}>{">"}</button>}
      </div>
    </div>
  );
};

export default EventListComponent;
