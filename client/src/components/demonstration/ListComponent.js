import defaultImg from "../../assets/logo.png";
import { useState, useEffect } from "react";
import { getList } from "../../api/demApi";
import PageComponent from "../common/PageComponent";
import ImageSliderModal from "./ImageSliderModal";
import useMove from "../../hooks/useMove";
const ListComponent = () => {
    const initState = { // 페이지 초기화
        content: [], totalPages: 0, currentPage: 0
    }

    const [pageData, setPageData] = useState(initState); // 페이지 데이터
    const [current, setCurrent] = useState(pageData.currentPage); // 현재 페이지 값
    const [listData, setListData] = useState([]); // 받아올 content 값
    const [modalOpen, setModalOpen] = useState(false);
    const [selectedImages, setSelectedImages] = useState([]);
     const { moveToPath} = useMove();
    useEffect(() => {
        getList(current).then(data => {
            setPageData(data);
            setListData(data);
        })
    }, [current])

    return (
        <>
            <div className="w-full">
                <div className="flex gap-2">
                    {listData.content?.map((item) => {
                        const mainImageObj = item.imageList.find(img => String(img.isMain) === "true");
                        const mainImageUrl = mainImageObj ? `http://localhost:8090/view/${mainImageObj.imageUrl}` : defaultImg;
                        return (
                            <div
                                key={item.demNum}
                                className="flex-1 h-[500px] bg-blue-200 p-4 flex flex-col items-center justify-center gap-4 text-center"
                            >
                                <img
                                    onClick={() => {
                                        const urlList = item.imageList.map(img => `http://localhost:8090/view/${img.imageUrl}`);
                                        setSelectedImages(urlList);
                                        setModalOpen(true);
                                    }}
                                    src={mainImageUrl}
                                    alt={`equipment-${item.demNum}`}
                                    className="w-40 h-[200px] object-cover cursor-pointer"
                                />
                                <div className="w-full h-1 bg-black mt-2"></div>

                                <ImageSliderModal
                                    open={modalOpen}
                                    onClose={() => setModalOpen(false)}
                                    imageList={selectedImages}
                                />

                                <div className="self-start text-left mt-[50px]">
                                    <span className="text-blue-600">장비명:</span> {item.demName}
                                    <br />
                                    <span className="text-blue-600">개수:</span> {item.itemNum}개
                                    <br />
                                    <span className="text-blue-600">제조사:</span> {item.demMfr}
                                </div>
                                <button
                                    disabled={!(item.itemNum!==0)}
                                    className={`mt-auto px-4 py-2 rounded-md shadow text-white ${item.itemNum!==0
                                        ? "bg-blue-600 hover:bg-blue-700 active:bg-blue-800"
                                        : "bg-gray-400 cursor-not-allowed"
                                        }`}
                                   onClick={() => moveToPath(`../detail/${item.demNum}`)}
                                >
                                    {item.itemNum!==0 ? "대여 가능" : "대여 불가능"}
                                </button>
                            </div>
                        );
                    })}
                </div>
            </div >

            <div className="flex justify-center">
                <PageComponent totalPages={pageData.totalPages} current={current} setCurrent={setCurrent} />
            </div>
        </>
    );




}
export default ListComponent;