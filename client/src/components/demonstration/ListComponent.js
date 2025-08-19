import defaultImg from "../../assets/logo.png";
import { useState, useEffect } from "react";
import { getList } from "../../api/demApi";
import PageComponent from "../common/PageComponent";
import ImageSliderModal from "./ImageSliderModal";
import useMove from "../../hooks/useMove";
import SearchComponent from "../demonstration/SearchComponent";
import { useSelector } from "react-redux";

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
    const mainImageObj = item.imageList.find((img) => String(img.isMain) === "true");
    const mainImageUrl = mainImageObj
      ? `http://localhost:8090/view/${mainImageObj.imageUrl}`
      : defaultImg;

    return item.state === "ACCEPT" ? (
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
          className={`mt-auto px-4 py-2 rounded-md shadow text-white ${item.itemNum !== 0
              ? "bg-blue-600 hover:bg-blue-700 active:bg-blue-800"
              : "bg-gray-400 cursor-not-allowed"
            }`}
          onClick={() => {
            if (loginState && loginState.memId) {
              moveToPath(`../detail/${item.demNum}`)
            } else {
              alert("로그인이 필요합니다.");
              moveToLogin();
            }
          }}
        >
          {item.itemNum !== 0 ? "대여 가능" : "대여 불가능"}
        </button>
      </div>
    ) : (
      <></>
    );
  });

  return (
    <>
      <div className="w-full px-4 mb-4 ml-[120px]">
        <SearchComponent
          search={search}
          setSearch={setSearch}
          type={searchType}
          setType={setSearchType}
          onSearchClick={onSearchClick}
          searchOptions={searchOptions}
        />
      </div>

      <div className="flex flex-wrap justify-center gap-4">
        {listData.content && listData.content.length === 0 ? (
          <p className="text-gray-500 text-lg mt-20">등록된 상품이 없습니다.</p>
        ) : (
          mainContent
        )}
      </div>

      <div className="flex justify-center mt-[50px] mb-[50px]">
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
