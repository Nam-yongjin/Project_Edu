import { useState, useEffect } from "react";
import { ko } from "date-fns/locale";
import { getOne, putUpdate } from "../../api/demApi";
import { urlListToFileList } from "../../api/urlToFileApi";
import useMove from "../../hooks/useMove";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

const UpdateComponent = ({ demNum }) => {
    const initState = { demName: "", demMfr: "", itemNum: 0, demInfo: "" }; // form에서 받을 데이터 초기값
    const [fileInputKey, setFileInputKey] = useState(Date.now()); // input 태그 리랜더링 위한 초기값
    const [images, setImages] = useState([]); // 이미지 배열 공백 지정
    const [dem, setDem] = useState({ ...initState }); // from받을 데이터 초기값 지정
    const [returnDate, setReturnDate] = useState(new Date()); // react datepicker 상태값 저장
    const [errors, setErrors] = useState({}); // 폼창 예외 처리 메시지를 띄우기 위한 상태값
    const { moveToPath, moveToReturn } = useMove(); // useMove에서 가져온 모듈들
    const serverHost = "http://localhost:8090/";
   useEffect(() => {
  const loadData = async () => {
    const data = await getOne(demNum);
    setDem(data);
    setReturnDate(new Date(data.expDate + "T00:00:00"));

    if (data.imageUrlList && data.imageUrlList.length > 0) {
        const fullUrls = data.imageUrlList.map(img => serverHost + img);
      const fileList = await urlListToFileList(fullUrls,data.imageNameList);
      setImages(fileList); // file, name, url 포함됨
    }
  };
  loadData();
}, [demNum]);
 // useEffect를 사용하면 최초 1회, 그리고 의존성 배열이 있을경우,
    // 의존성 배열 변경 시 실행된다.


    const handleChangeDem = (e) => {
        dem[e.target.name] = e.target.value;
        setDem({ ...dem });
    };

    const handleReturnDateChange = (date) => {
        setReturnDate(date);
        setDem((prev) => ({ ...prev, expDate: date }));
    };

    const handleFileChange = (e) => { // 파일 선택이 됬을 경우, 
        const files = Array.from(e.target.files); // 현재 파일을 files에 담고
        const filePreviews = files.map(file => ({
            file,
            url: URL.createObjectURL(file),
            name: file.name
        }));
        setImages(filePreviews);
    };

    const fileDelete = (imgToDelete) => {
        setImages((prevImages) => {
            const newImages = prevImages.filter(img => img.url !== imgToDelete.url);
            // 만약 삭제 후 이미지가 하나도 없으면 input 리셋용 key 변경
            if (newImages.length === 0) {
                setFileInputKey(Date.now()); // input 태그 재렌더링 유도
            }
            return newImages;
        });
    };


    const update = () => {
        const newErrors = {};
        if (!dem.demName.trim()) newErrors.demName = "물품명은 필수입니다.";
        if (!dem.demMfr.trim()) newErrors.demMfr = "제조사는 필수입니다.";
        if (!dem.demInfo.trim()) newErrors.demInfo = "물품 설명은 필수입니다.";
        // 이미지 1개 이상 체크
        if (images.length === 0) newErrors.images = "이미지는 최소 1장 등록해야 합니다.";
        // 수량 1 이상 정수 체크
        if (!Number.isInteger(Number(dem.itemNum)) || Number(dem.itemNum) < 1) {
            newErrors.itemNum = "수량은 0이상이여야 합니다.";
        }

        // 날짜가 오늘 이후인지 체크
        const today = new Date();
        // 비교할 때 시간 제거 (00:00:00으로 맞춤)
        const selectedDate = new Date(dem.expDate);
        selectedDate.setHours(0, 0, 0, 0);
        today.setHours(0, 0, 0, 0);

        if (selectedDate <= today) {
            newErrors.expDate = "반납 날짜는 오늘 이후여야 합니다.";
        }

        setErrors(newErrors);

        // 2) 오류가 있으면 submit 중단
        if (Object.keys(newErrors).length > 0) return;
        const formData = new FormData();
        for (let i = 0; i < images.length; i++) {
            formData.append("imageList", images[i].file);
        }
        formData.append("demName", dem.demName);
        formData.append("demMfr", dem.demMfr);
        formData.append("itemNum", dem.itemNum);
        formData.append("demInfo", dem.demInfo);
        formData.append("expDate", returnDate.toISOString().split("T")[0]);
        formData.append("demNum", demNum);
        //"2025-07-28T06:00:00.000Z" 를 T로 나누면 yyyy-mm-dd 형태의 문자열로 받을수 잇음.
        putUpdate(formData).then(data => {
        });
        alert("상품 수정 완료");
        moveToPath("/")
    }
    return (

        <div className="flex mt-10 max-w-6xl mx-auto">
            <div className="space-y-6 w-2/3">
                <div className="flex items-center">
                    <label className="text-xl font-semibold w-[120px]">물품명:</label>
                    <input
                        type="text"
                        placeholder="제품 이름을 입력해주세요."
                        className="border p-3 text-lg flex-1 min-w-0 box-border"
                        name="demName"
                        value={dem.demName}
                        onChange={handleChangeDem}
                    />
                </div>
                {errors.demName && <p className="text-red-600 text-sm mt-1 ml-[120px]">{errors.demName}</p>}
                <div className="flex items-center">
                    <label className="text-xl font-semibold w-[120px]">제조사:</label>
                    <input
                        type="text"
                        placeholder="제조사를 입력해주세요."
                        className="border p-3 text-lg flex-1 min-w-0 box-border"
                        name="demMfr"
                        value={dem.demMfr}
                        onChange={handleChangeDem}
                    />
                </div>
                {errors.demMfr && <p className="text-red-600 text-sm mt-1 ml-[120px]">{errors.demMfr}</p>}
                <div className="flex items-center">
                    <label className="text-xl font-semibold w-[120px]">개수:</label>
                    <input
                        type="text"
                        placeholder="개수를 입력해주세요."
                        className="border p-3 text-lg flex-1 min-w-0 box-border"
                        name="itemNum"
                        value={dem.itemNum}
                        onChange={handleChangeDem}
                    />
                </div>
                {errors.itemNum && <p className="text-red-600 text-sm mt-1 ml-[120px]">{errors.itemNum}</p>}
                <div className="flex items-start">
                    <label className="text-xl font-semibold w-[120px] pt-3">소개:</label>
                    <textarea
                        rows={5}
                        placeholder="제품 소개를 입력해주세요."
                        className="border p-3 text-lg flex-1 resize-y min-w-0 box-border"
                        name="demInfo"
                        value={dem.demInfo}
                        onChange={handleChangeDem}
                    />
                </div>
                {errors.demInfo && <p className="text-red-600 text-sm mt-1 ml-[120px]">{errors.demInfo}</p>}
                <div className="flex items-center">
                    <label className="text-xl font-semibold w-[120px]">반납 날짜:</label>
                    <DatePicker
                        className="border p-3 text-lg flex-1 min-w-0 box-border"
                        selected={returnDate}
                        onChange={handleReturnDateChange}
                        dateFormat="yyyy-MM-dd"
                        placeholderText="날짜를 선택하세요"
                        minDate={new Date()}
                        name="expDate"
                        locale={ko}
                    />
                </div>
                {errors.expDate && <p className="text-red-600 text-sm mt-1 ml-[120px]">{errors.expDate}</p>}

                <div>
                    <div className="flex items-center mt-3">
                        <label className="text-xl font-semibold w-[120px]">이미지:</label>
                        <input
                            key={fileInputKey}
                            type="file"
                            multiple
                            accept="image/*"
                            onChange={(e) => handleFileChange(e)}
                            className="border p-2 text-base flex-1 cursor-pointer min-w-0 box-border"
                        />
                        {errors.images && <p className="text-red-600 text-sm mt-1 ml-[120px]">{errors.images}</p>}
                    </div>

                    <div className="mt-4 flex justify-end gap-4 pr-2">
                        <button
                            className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 shadow"
                            onClick={update}
                        >
                            상품 수정
                        </button>
                        <button
                            className="bg-gray-400 text-white px-4 py-2 rounded-md hover:bg-gray-500 shadow"
                            onClick={moveToReturn}
                        >
                            뒤로가기
                        </button>
                    </div>
                </div>
            </div>

            <div className="w-1/3 pl-10 flex flex-col gap-4 items-start">
                {images.map((img) => (
                    <div className="flex flex-col items-start">
                        <button className="w-full text-right" onClick={() => fileDelete(img)}>x</button>
                        <img
                            src={img.file ? img.url : `http://localhost:8090/${img.url}`}
                            className="w-32 h-32 object-cover rounded-md border shadow mb-1"
                        />
                        <p className="text-sm text-gray-600 break-all">{img.name}</p>
                    </div>
                ))}
            </div>
        </div>
    );
}
export default UpdateComponent;