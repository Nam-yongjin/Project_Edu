const SearchComponent = ({ search, setSearch, type, setType, onSearchClick, searchOptions }) => {
  const handleSubmit = (e) => {
    e.preventDefault();
    onSearchClick();
  };

  return (
    <form
      onSubmit={handleSubmit}
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
        {searchOptions.map((opt) => (
          <option key={opt.value} value={opt.value}>
            {opt.label}
          </option>
        ))}
      </select>
      <button
        type="submit"
        className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 flex items-center justify-center border-l border-gray-300"
        aria-label="검색"
      >
        검색
      </button>
    </form>
  );
};

export default SearchComponent;
