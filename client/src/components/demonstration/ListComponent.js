import defaultImg from "../../assets/logo.png";
import { useState, useEffect } from "react";
import { getList } from "../../api/demApi";
import PageComponent from "../common/PageComponent";
import ImageSliderModal from "./ImageSliderModal";
import useMove from "../../hooks/useMove";
import SearchComponent from "../demonstration/SearchComponent";
import { useSelector } from "react-redux";

const ListComponent = () => {
  const initState = { content: [], totalPages: 0, currentPage: 0 };
  const [pageData, setPageData] = useState(initState);
  const [current, setCurrent] = useState(0);
  const [listData, setListData] = useState([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedImages, setSelectedImages] = useState([]);
  const { moveToPath, moveToLogin } = useMove();
  const [searchType, setSearchType] = useState("demName");
  const [search, setSearch] = useState("");
  const loginState = useSelector((state) => state.loginState);

  const searchOptions = [
    { value: "demName", label: "상품명" },
    { value: "demMfr", label: "제조사명" },
  ];

  const fetchData = () => {
    getList(current, searchType, search).then((data) => {
      setPageData(data);
      setListData(data);
    });
  };

  useEffect(() => {
    fetchData();
  }, [current, searchType, search]);

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
        className="bg-white w-1/5 h-[500px] rounded-xl shadow-lg overflow-hidden flex flex-col hover:shadow-2xl transition-shadow duration-300"
      >
        {/* 이미지 */}
        <div
          className="h-[220px] w-full overflow-hidden cursor-pointer"
          onClick={() => {
            const urlList = item.imageList.map(
              (img) => `http://localhost:8090/view/${img.imageUrl}`
            );
            setSelectedImages(urlList);
            setModalOpen(true);
          }}
        >
          <img
            src={mainImageUrl}
            alt={`equipment-${item.demNum}`}
            className="w-full h-full object-cover transition-transform duration-300 hover:scale-105"
          />
        </div>

        {/* 내용 */}
        <div className="p-4 flex flex-col gap-2 flex-1 min-h-[140px]">
          <h3 className="text-lg font-bold text-blue-600 truncate">{item.demName}</h3>
          <p className="text-sm text-gray-600 truncate">제조사: {item.demMfr}</p>
          <p className="text-sm text-gray-600 truncate">수량: {item.itemNum}개</p>
          <p className="text-xs text-gray-500 line-clamp-3">{item.demInfo}</p>
        </div>

        {/* 버튼 */}
        <button
          disabled={item.itemNum === 0}
          className={`m-4 px-4 py-3 rounded-md text-white font-semibold transition-colors duration-300 ${item.itemNum !== 0
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
        <div className="newText-3xl font-bold ">실증 물품 대여 관리</div>
        <div className="py-2">
          <SearchComponent
            search={search}
            setSearch={setSearch}
            type={searchType}
            setType={setSearchType}
            onSearchClick={onSearchClick}
            searchOptions={searchOptions}
          />
        </div>

        {/* 카드 리스트 */}
        <div className="flex flex-wrap justify-center gap-10 my-5">
          {listData.content && listData.content.length === 0 ? (
            <p className="text-gray-500 newText-lg mt-20">등록된 상품이 없습니다.</p>
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
