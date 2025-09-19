import defaultImg from "../../assets/logo.png";
import { useState, useEffect } from "react";
import { getList } from "../../api/demApi";
import PageComponent from "../common/PageComponent";
import ImageSliderModal from "./ImageSliderModal";
import useMove from "../../hooks/useMove";
import SearchComponent from "../demonstration/SearchComponent";
import { useSelector } from "react-redux";

const ListComponent = () => {
const initState = { content: [], totalPages: 0, currentPage: 0 }; // 초기 페이지 데이터 구조
const [pageData, setPageData] = useState(initState); // 현재 페이지 데이터
const [current, setCurrent] = useState(0); // 현재 페이지 번호
const [listData, setListData] = useState([]); // 실제 리스트 데이터
const [modalOpen, setModalOpen] = useState(false); // 이미지 모달 열림 여부
const [selectedImages, setSelectedImages] = useState([]); // 모달에서 보여줄 이미지 리스트
const { moveToPath, moveToLogin } = useMove(); // 페이지 이동 훅
const [searchType, setSearchType] = useState("demName"); // 검색 기준
const [search, setSearch] = useState(""); // 검색어
const loginState = useSelector((state) => state.loginState); // 로그인 상태 정보
const [sortType, setSortType] = useState("desc"); // 정렬 방식
const searchOptions = [ // 검색 옵션 목록
  { value: "demName", label: "상품명" },
  { value: "demMfr", label: "제조사명" },
  { value: "companyName", label: "기업명" },
];


  const fetchData = () => {
    getList(current, searchType, search, sortType).then((data) => {
      console.log(data);
      setPageData(data);
      setListData(data);
    });
  };

  useEffect(() => {
    fetchData();
  }, [current]);

  const onSearchClick = () => {
    setCurrent(0);
    fetchData();
  };

  const mainContent = listData?.content?.map((item) => {
    if (item.state !== "ACCEPT") return null;

    const mainImageObj = item.imageList.find((img) => String(img.isMain) === "true");
    const mainImageUrl = mainImageObj
      ? `http://localhost:8090/view/${mainImageObj.imageUrl}`
      : defaultImg;

    return (

      <div
        key={item.demNum}
        className="bg-white w-1/6 min-w-[180px] rounded-xl shadow-lg overflow-hidden flex flex-col hover:shadow-2xl transition-shadow duration-300"
      >
        {/* 이미지 */}
        <div
          className="aspect-[4/3] w-full overflow-hidden cursor-pointer"
          onClick={() => {
            const urlList = [
              ...item.imageList
                .filter(img => img.isMain)           // 메인 이미지만 먼저
                .map(img => `http://localhost:8090/view/${img.imageUrl}`),
              ...item.imageList
                .filter(img => !img.isMain)          // 나머지 이미지
                .map(img => `http://localhost:8090/view/${img.imageUrl}`)
            ];
            setSelectedImages(urlList);
            setModalOpen(true);
          }}
        >
          <img
            src={mainImageUrl}
            alt={`equipment-${item.demNum}`}

          />
        </div>

        {/* 내용 */}
        <div className="p-4 flex flex-col gap-2 flex-1 min-h-[140px]">
          <h3 className="newText-lg font-bold text-blue-600 truncate">{item.demName}</h3>
          <p className="newText-sm text-gray-600 truncate">제조사: {item.demMfr}</p>
          <p className="newText-sm text-gray-600 truncate">수량: {item.itemNum}개</p>
          <p className="newText-sm text-gray-600 line-clamp-3">기업명: {item.companyName}</p>
          <p className="newText-sm text-gray-600 line-clamp-3">마감일: {item.expDate}</p>
        </div>

        {/* 버튼 */}
        <button
          disabled={item.itemNum === 0}
          className={`newText-base m-4 px-4 py-3 rounded-md text-white font-semibold transition-colors duration-300 ${item.itemNum !== 0
            ? "bg-blue-600 hover:bg-blue-700 active:bg-blue-800"
            : "bg-gray-400 cursor-not-allowed"
            }`}
          onClick={() => {
            if (loginState && loginState.memId) {
              moveToPath(`../detail/${item.demNum}`);
            } else {
              alert("로그인이 필요합니다.");
              moveToLogin();
            }
          }}
        >
          {item.itemNum !== 0 ? "대여 가능" : "대여 불가"}
        </button>

      </div>
    );
  });

  return (
    <div className="max-w-screen-xl mx-auto my-10">
      <div className="min-blank">
        <div className="mx-auto text-center">
          {/* 제목 + 설명 */}
          <div className="newText-3xl font-bold">실증 물품</div>
          <p className="text-gray-700 my-3 newText-base px-4 py-2 rounded-md inline-block">
            전체 <span className="font-bold text-blue-600">{pageData.totalElements}</span>건의 물품이 있습니다.
          </p>

          {/* 검색창도 가운데 */}
          <div className="py-2 flex justify-center">
            <SearchComponent
              search={search}
              setSearch={setSearch}
              type={searchType}
              setType={setSearchType}
              onSearchClick={onSearchClick}
              searchOptions={searchOptions}
              sortType={sortType}
              setSortType={setSortType}
              showSort={true}
            />

          </div>
        </div>


        {/* 카드 리스트 */}
        <div className="flex flex-wrap justify-center gap-10 my-5">
          {listData.content && listData.content.length === 0 ? (
            <div className="w-full border flex items-center justify-center min-h-[300px]">
              <p className="text-gray-500 newText-3xl">등록된 상품이 없습니다.</p>
            </div>
          ) : (
            mainContent
          )}
        </div>

        {/* 페이지네이션 */}
        <div className="flex justify-center mt-10 mb-10">
          <PageComponent
            totalPages={pageData.totalPages}
            current={current}
            setCurrent={setCurrent}
          />
        </div>

        <ImageSliderModal
          open={modalOpen}
          onClose={() => setModalOpen(false)}
          imageList={selectedImages}
        />
      </div>
    </div>
  );
};

export default ListComponent;
