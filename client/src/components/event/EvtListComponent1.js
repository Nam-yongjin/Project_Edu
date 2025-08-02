import defaultImg from "../../assets/logo.png";
import { useState, useEffect } from "react";
import { getList1 } from "../../api/eventApi";
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
  const [selectedImages, setSelectedImages] = useState([]);
  const [modalOpen, setModalOpen] = useState(false);
  const { moveToPath } = useMove();

  useEffect(() => {
  getList(current).then((data) => {
    console.log("📦 eventList 응답:", data); // 👈 여기에 추가
    setPageData(data);
  });
}, [current]);

  const getCategoryLabel = (category) => {
    switch (category) {
      case "STUDENT":
        return "학생";
      case "TEACHER":
        return "교사";
      case "USER":
      default:
        return "일반인";
    }
  };

  return (
    <>
      <div className="w-full grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6 p-4">
        {pageData.content?.map((item) => {
          const mainImageObj = item.imageList?.find((img) => String(img.isMain) === "true");
          const mainImageUrl = mainImageObj
            ? `http://localhost:8090/view/${mainImageObj.imageUrl}`
            : defaultImg;

          const canApply = item.itemNum !== 0 && item.currCapacity < item.maxCapacity;

          return (
            <div
              key={item.eventNum}
              className="bg-white border rounded-xl shadow-md p-4 flex flex-col justify-between"
            >
              {/* 이미지 + 카테고리 뱃지 */}
              <div className="relative">
                <img
                  src={mainImageUrl}
                  alt={`event-${item.eventNum}`}
                  onClick={() => {
                    const urlList = item.imageList?.map(
                      (img) => `http://localhost:8090/view/${img.imageUrl}`
                    );
                    setSelectedImages(urlList);
                    setModalOpen(true);
                  }}
                  className="w-full h-[160px] object-cover rounded-md cursor-pointer"
                />
                <span className="absolute top-2 left-2 bg-blue-100 text-blue-800 text-xs font-semibold px-2 py-1 rounded-full">
                  {getCategoryLabel(item.category)}
                </span>
              </div>

              {/* 행사 정보 */}
              <div className="mt-4 text-left text-sm text-gray-700 space-y-1">
                <p className="text-base font-bold text-black">{item.eventName}</p>
                <p>📅 {item.applyStartPeriod} ~ {item.applyEndPeriod}</p>
                <p>👥 모집인원: {item.maxCapacity}명</p>
                <p>🧍 현재 신청: {item.currCapacity}명</p>
              </div>

              {/* 버튼 */}
              <button
                disabled={!canApply}
                className={`mt-4 w-full px-4 py-2 rounded-md shadow text-white text-sm font-semibold ${
                  canApply
                    ? "bg-blue-500 hover:bg-blue-600 active:bg-blue-700"
                    : "bg-gray-300 cursor-not-allowed"
                }`}
                onClick={() => moveToPath(`../detail/${item.eventNum}`)}
              >
                {canApply ? "참여 가능" : "참여 불가능"}
              </button>
            </div>
          );
        })}
      </div>

      {/* 이미지 모달 */}
      <ImageSliderModal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        imageList={selectedImages}
      />

      {/* 페이지네이션 */}
      <div className="flex justify-center mt-6">
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
