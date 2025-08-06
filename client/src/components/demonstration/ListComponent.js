import defaultImg from "../../assets/logo.png";
import { useState, useEffect } from "react";
import { getList } from "../../api/demApi";
import PageComponent from "../common/PageComponent";
import ImageSliderModal from "./ImageSliderModal";
import useMove from "../../hooks/useMove";

const ListComponent = () => {
  const initState = {
    content: [],
    totalPages: 0,
    currentPage: 0,
  };

  const [pageData, setPageData] = useState(initState);
  const [current, setCurrent] = useState(0);
  const [listData, setListData] = useState([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedImages, setSelectedImages] = useState([]);
  const { moveToPath } = useMove();
  const [searchType, setSearchType] = useState("demName");
  const [search, setSearch] = useState("");

  useEffect(() => {
    getList(current, searchType, search).then((data) => {
      setPageData(data);
      setListData(data);
    });
  }, [current]);

  const handleSearch = (e) => {
 e.preventDefault(); 
     getList(current, searchType, search).then((data) => {
      setPageData(data);
      setListData(data);
    });
  };

  const mainContent = listData?.content?.map((item) => {
    const mainImageObj = item.imageList.find((img) => String(img.isMain) === "true");
    const mainImageUrl = mainImageObj
      ? `http://localhost:8090/view/${mainImageObj.imageUrl}`
      : defaultImg;

    return (
      <div
        key={item.demNum}
        className="w-[300px] h-[500px] bg-blue-200 p-4 flex flex-col items-center justify-center gap-4 text-center rounded-md shadow-md"
      >
        <img
          onClick={() => {
            const urlList = item.imageList.map(
              (img) => `http://localhost:8090/view/${img.imageUrl}`
            );
            setSelectedImages(urlList);
            setModalOpen(true);
          }}
          src={mainImageUrl}
          alt={`equipment-${item.demNum}`}
          className="w-40 h-[200px] object-cover cursor-pointer rounded"
        />

        <div className="w-full h-1 bg-black mt-2" />

        <ImageSliderModal
          open={modalOpen}
          onClose={() => setModalOpen(false)}
          imageList={selectedImages}
        />

        <div className="self-start text-left mt-[50px] text-sm">
          <span className="text-blue-600 font-semibold">장비명:</span> {item.demName}
          <br />
          <span className="text-blue-600 font-semibold">개수:</span> {item.itemNum}개
          <br />
          <span className="text-blue-600 font-semibold">제조사:</span> {item.demMfr}
        </div>

        <button
          disabled={item.itemNum === 0}
          className={`mt-auto px-4 py-2 rounded-md shadow text-white ${
            item.itemNum !== 0
              ? "bg-blue-600 hover:bg-blue-700 active:bg-blue-800"
              : "bg-gray-400 cursor-not-allowed"
          }`}
          onClick={() => moveToPath(`../detail/${item.demNum}`)}
        >
          {item.itemNum !== 0 ? "대여 가능" : "대여 불가능"}
        </button>
      </div>
    );
  });

  return (
    <>
      <div className="w-full px-4 mb-4 ml-[230px]">
        <form
      onSubmit={handleSearch}  
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
        value={searchType}
        onChange={(e) => setSearchType(e.target.value)}
        className="px-3 py-2 text-sm bg-white focus:outline-none border-l border-gray-300"
        aria-label="검색 옵션 선택"
      >
        <option value="demName">상품명</option>
        <option value="demMfr">제조사명</option>
      </select>
      <button
        type="submit"  
        className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 flex items-center justify-center border-l border-gray-300"
        aria-label="검색"
      >검색
      </button>
    </form>
      </div>
        <div className="flex flex-wrap justify-center gap-4">
          {mainContent}
        </div>
      <div className="flex justify-center">
        <PageComponent
          totalPages={pageData.totalPages}
          current={current}
          setCurrent={setCurrent}
        />
      </div>
    </>
  );
};

export default ListComponent;
