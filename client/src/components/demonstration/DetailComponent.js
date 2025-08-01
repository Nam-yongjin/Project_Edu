import CalendarComponent from "./CalendarComponent";
import { useState, useEffect } from "react";
import { getDetail, getResDate } from "../../api/demApi";
const DetailComponent = ({ demNum }) => {

    const [dem, setDem] = useState([]); // 서버에서 받아올 여러가지 값들
    const [fileList, setFileList] = useState({imageList: []}); // 서버에서 받아올 이미지 리스트
    const [mainImageUrl, setMainImageUrl] = useState(); // 대표 이미지
    const serverHost = "http://localhost:8090/";

    useEffect(() => {
        const loadData = async () => {
            const data = await getDetail(demNum);
            setDem(data);
            setFileList(data);
            //console.log(data);
        //const mainImageObj = data.imageList.find(img => img.isMain);
       // setMainImageUrl(mainImageObj ? `${serverHost}${mainImageObj.imageUrl}` : '');
        //console.log(mainImageUrl);
        data.imageList.map(img => img.isMain);
        }
        loadData();
    }, [demNum]);


    return (
        <>
            <div className="w-full">
                <div className="flex items-start gap-4">
                    <img src={mainImageUrl} alt="default" className="w-40 h-40 object-cover" />

                    <div className="space-y-1 mt-3">
                        <span className="text-blue-600">장비명:</span> {dem.demName}
                        <br />
                        <span className="text-blue-600">제조사:</span> {dem.demMfr}
                        <br />
                        <span className="text-blue-600">물품소개:</span> {dem.demInfo}
                    </div>
                </div>
                <div>
                    <h2>날짜 선택</h2> (신청기간은 연속으로 최소 하루 부터 신청 가능합니다.(최대90일))
                    <div className="flex gap-2 mt-2 text-[10px]">
                        <div className="w-10 h-5 rounded-full bg-gray-300 flex items-center justify-center border-2 border-black border-solid">
                            불가
                        </div>
                        <div className="w-10 h-5 rounded-full bg-white flex items-center justify-center border-2 border-black border-solid">가능</div>
                        <div className="w-10 h-5 rounded-full bg-emerald-100 flex items-center justify-center border-2 border-black border-solid">선택</div>
                    </div>
                </div>
            </div>
            <CalendarComponent />
        </>
    );
}
export default DetailComponent;