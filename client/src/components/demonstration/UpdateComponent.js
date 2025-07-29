import { useState, useEffect } from "react";
import { ko } from "date-fns/locale";
import { getOne, putUpdate } from "../../api/demApi";
import useMove from "../../hooks/useMove";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

const UpdateComponent = ({demNum}) => {
    const initState = { demName: "", demMfr: "", itemNum: 0, demInfo: "", expDate: new Date() }; // form에서 받을 데이터 초기값

    const [fileInputs, setFileInputs] = useState([Date.now()]); // fileInput의 값을 현재 시간으로 하여 구별할것 (처음 input은 잇어야 하므로)
    const [images, setImages] = useState([]); // 이미지 배열 공백 지정
    const [dem, setDem] = useState({ ...initState }); // from받을 데이터 초기값 지정
    const [returnDate, setReturnDate] = useState(new Date()); // react datepicker 상태값 저장
    const { moveToPath, moveToReturn } = useMove(); // useMove에서 가져온 모듈들
    useEffect(() => { getOne(demNum).then(data => setDem(data)) }, [demNum]) // useEffect를 사용하면 최초 1회, 그리고 의존성 배열이 있을경우,
    // 의존성 배열 변경 시 실행된다.
    const handleFileChange = (id, e) => { // 파일 선택이 됬을 경우, 
        const files = Array.from(e.target.files); // 현재 파일을 files에 담고
        if (files.length === 0) {
            setFileInputs((prev) => {
                if (prev.length > 1) {
                    setImages(prevImages => {
                        const removeIndex = prev.findIndex(inputId => inputId === id);
                        return prevImages.filter((_, index) => index !== removeIndex);
                    });
                    return prev.filter((inputId) => inputId !== id);
                }
                return prev;
            });
            return;
        }

        const newPreviews = files.map((file) => ({ // Images에 저장할 객체들
            file,
            url: URL.createObjectURL(file), // 브라우저에서 업로드한 파일의 임시 URL을 만들어주는 함수
            name: file.name,
            inputId: id
        }));

        setImages((prev) => [...prev, ...newPreviews]); // 기존 이미지 배열+새로 만든 이미지 추가

        if (fileInputs[fileInputs.length - 1] === id) { // fileInputs가 현재 input의 id라면 input태그 추가
            setFileInputs((prev) => [...prev, Date.now()]);
        }
    };

    const handleChangeDem = (e) => {
        dem[e.target.name] = e.target.value;
        setDem({ ...dem });
    };

    const handleReturnDateChange = (date) => {
        setReturnDate(date);
        setDem((prev) => ({ ...prev, expDate: date }));
    };

    const fileDelete = (indexToRemove) => {
        setImages((prevImages) => {
            const removedInputId = prevImages[indexToRemove]?.inputId;

            // 삭제할 이미지 제외
            const newImages = prevImages.filter((_, idx) => idx !== indexToRemove);

            // 해당 inputId와 연결된 이미지가 더 이상 없으면 fileInputs에서도 제거
            const stillHasInputId = newImages.some(img => img.inputId === removedInputId);

            setFileInputs((prevInputs) => {
                if (!stillHasInputId) {
                    const filteredInputs = prevInputs.filter(id => id !== removedInputId);
                    return filteredInputs.length > 0 ? filteredInputs : [Date.now()];
                }
                return prevInputs;
            });

            return newImages;
        });
    };

    const update = () => {
        const formData = new FormData();
        for (let i = 0; i < images.length; i++) {
            formData.append("imageList", images[i].file);
        }
        formData.append("demName", dem.demName);
        formData.append("demMfr", dem.demMfr);
        formData.append("itemNum", dem.itemNum);
        formData.append("demInfo", dem.demInfo);
        formData.append("expDate", dem.expDate.toISOString().split("T")[0]);
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

                <div>
                    {fileInputs.map((id, index) => (
                        <div key={id} className="flex items-center mt-3">
                            <label className="text-xl font-semibold w-[120px]">이미지{index + 1}:</label>
                            <input
                                type="file"
                                multiple
                                accept="image/*"
                                onChange={(e) => handleFileChange(id, e)}
                                className="border p-2 text-base flex-1 cursor-pointer min-w-0 box-border"
                            />
                        </div>
                    ))}

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
                {images.map((img, index) => (
                    <div key={index} className="flex flex-col items-start">
                        <button className="w-full text-right" onClick={() => fileDelete(index)}>x</button>
                        <img
                            src={img.url}
                            alt={`선택된 이미지 ${index + 1}`}
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