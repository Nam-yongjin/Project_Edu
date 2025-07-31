import { useState } from "react";
import { ko } from "date-fns/locale";
import { postAdd } from "../../api/demApi";
import useMove from "../../hooks/useMove";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

const AddComponent = () => {
    const initState = { demName: "", demMfr: "", itemNum: 0, demInfo: "", expDate: new Date() }; // form에서 받을 데이터 초기값

    const [images, setImages] = useState([]); // 이미지 배열 공백 지정
    const [dem, setDem] = useState({ ...initState }); // from받을 데이터 초기값 지정
    const [returnDate, setReturnDate] = useState(new Date()); // react datepicker 상태값 저장
    const { moveToPath, moveToReturn } = useMove(); // useMove에서 가져온 모듈들
    const [errors, setErrors] = useState({});
    const [fileInputKey, setFileInputKey] = useState(Date.now());


    const addDem = () => {

        const newErrors = {};
        if (!dem.demName.trim()) newErrors.demName = "물품명은 필수입니다.";
        if (!dem.demMfr.trim()) newErrors.demMfr = "제조사는 필수입니다.";
        if (!dem.demInfo.trim()) newErrors.demInfo = "물품 설명은 필수입니다.";
        // 이미지 1개 이상 체크
        if (images.length === 0) newErrors.images = "이미지는 최소 1장 등록해야 합니다.";
        // 수량 1 이상 정수 체크
        if (!Number.isInteger(Number(dem.itemNum)) || Number(dem.itemNum) < 0) {
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
       const mainIndex = images.findIndex(img => img.isMain === 1);
formData.append("mainImageIndex", mainIndex === -1 ? 0 : mainIndex);


        formData.append("demName", dem.demName);
        formData.append("demMfr", dem.demMfr);
        formData.append("itemNum", dem.itemNum);
        formData.append("demInfo", dem.demInfo);
        formData.append("expDate", dem.expDate.toISOString().split("T")[0]);
        //"2025-07-28T06:00:00.000Z" 를 T로 나누면 yyyy-mm-dd 형태의 문자열로 받을수 잇음.
        postAdd(formData).then(data => {
        });
        alert("상품 등록 완료");
        //moveToPath("/")
    };

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
        const filePreviews = files.map((file, index) => ({
            file,
            url: URL.createObjectURL(file), // 이 파일을 메모리 내 임시 URL로 만들어서 보여줌
            name: file.name,
            isMain: index === 0 // 디폴트로 첫번째 이미지를 대표이미지로 설정
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

    const handleCheckboxChange = (selectedIndex) => {
        const currentMainIndex = images.findIndex(img => img.isMain === 1); // 메인 이미지 인덱스 가져오기

        if (currentMainIndex === selectedIndex) {
            // 같은 대표 이미지 클릭 시 체크 해제
            setImages(images.map((img, idx) => ({
                ...img,
                isMain: 0
            })));
            return;
        }

        // 다른 이미지 클릭 시 대표 이미지 변경
        setImages(images.map((img, idx) => ({
            ...img,
            isMain: idx === selectedIndex ? 1 : 0
        })));
    };

    return (
        <div className="flex mt-10 max-w-6xl mx-auto">
            <div className="space-y-6 w-2/3">
                <div className="flex items-center">
                    <label className="text-xl font-semibold w-[120px]">물품명:</label>
                    <input
                        type="text"
                        placeholder="물품 이름을 입력해주세요."
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
                        placeholder="물품 소개를 입력해주세요."
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
                            className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 active:bg-blue-800 shadow"
                            onClick={addDem}>
                            상품 등록
                        </button>
                        <button
                            className="bg-gray-400 text-white px-4 py-2 rounded-md hover:bg-gray-500 active:bg-gray-600 shadow"
                            onClick={moveToReturn}>
                            뒤로가기
                        </button>
                    </div>
                </div>
            </div>

            <div className="w-1/3 pl-10 flex flex-col gap-4 items-start">
                {images.map((img, index) => (
                    <div key={img.url} className="flex flex-col items-start">
                        <h6>대표 이미지설정</h6>
                        <input
                            type="checkbox"
                            checked={Boolean(img.isMain)}
                            onChange={() => handleCheckboxChange(index)}
                            className="w-3 h-3"></input>
                        <button className="w-full text-right" onClick={() => fileDelete(img)}>x</button>
                        <img
                            src={img.url}
                            className="w-32 h-32 object-cover rounded-md border shadow mb-1"
                        />
                        <p className="text-sm text-gray-600 break-all">{img.name}</p>
                    </div>
                ))}
            </div>
        </div>
    );
};


export default AddComponent;