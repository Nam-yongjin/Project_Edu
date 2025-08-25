const SearchComponent = ({ search, setSearch, type, setType, onSearchClick, searchOptions,  sortType, setSortType,showSort = false  }) => {
  const handleSubmit = (e) => {
    e.preventDefault();
    onSearchClick();
  };

  return (
  <form
    onSubmit={handleSubmit}
    className="flex items-center rounded w-full max-w-xl px-3 h-10"
  >
    <input
      type="text"
      value={search}
      onChange={(e) => setSearch(e.target.value)}
      placeholder="검색어를 입력하세요"
      className="flex-1 newText-sm input-focus px-3 h-10 border border-gray-300 rounded-md mr-1"
    />

    <select
      value={type}
      onChange={(e) => setType(e.target.value)}
      className="px-3 h-10 newText-sm input-focus bg-white focus:outline-none border border-gray-300 rounded-md mr-1"
      aria-label="검색 옵션 선택"
    >
      {searchOptions.map((opt) => (
        <option key={opt.value} value={opt.value}>
          {opt.label}
        </option>
      ))}
    </select>

    {showSort && (
      <select
        value={sortType}
        onChange={({ target }) => setSortType(target.value)}
        className="px-3 h-10 newText-sm input-focus bg-white focus:outline-none border border-gray-300 rounded-md mr-1"
      >
        <option value="asc">종료빠른순</option>
        <option value="desc">종료느린순</option>
      </select>
    )}

    <button
      type="submit"
      className="positive-button newText-base h-10"
      aria-label="검색"
    >
      검색
    </button>
  </form>
);

};

export default SearchComponent;
