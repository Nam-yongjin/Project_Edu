const SearchComponent = ({ search, setSearch, type, setType, onSearchClick }) => {
  const handleSubmit = (e) => {
    e.preventDefault();  // 폼 제출시 페이지 리로드 방지
    onSearchClick();     // 부모 컴포넌트에서 전달된 검색 함수 실행
  };

  return (
    <form
      onSubmit={handleSubmit}  // onSubmit 이벤트에 함수 연결
      className="flex items-center border border-gray-300 rounded w-full max-w-md overflow-hidden"
    >
      <input
        type="text"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        placeholder="검색어를 입력하세요"
        className="flex-1 min-w-0 px-4 py-2 text-sm focus:outline-none"
      />
      <select
        value={type}
        onChange={(e) => setType(e.target.value)}
        className="px-3 py-2 text-sm bg-white focus:outline-none border-l border-gray-300"
        aria-label="검색 옵션 선택"
      >
        <option value="companyName">회사명</option>
        <option value="demName">상품명</option>
      </select>
      <button
        type="submit"  // 버튼은 submit 타입 유지
        className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 flex items-center justify-center border-l border-gray-300"
        aria-label="검색"
      >검색
      </button>
    </form>
  );
};
export default SearchComponent;
