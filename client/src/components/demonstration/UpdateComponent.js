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
    const [images, setImages] = useState([]); // 이미지 정보를 담을 상태 배열 지정
    const [dem, setDem] = useState({ ...initState }); // from받을 데이터 초기값 지정
    const [returnDate, setReturnDate] = useState(new Date()); // react datepicker 상태값 저장
    const [errors, setErrors] = useState({}); // 폼창 예외 처리 메시지를 띄우기 위한 상태값
    const { moveToPath, moveToReturn } = useMove(); // useMove에서 가져온 모듈들
    const serverHost = "http://localhost:8090/";
    useEffect(() => {
        // useEffect에서 비동기 처리를 위해 만든 loadData함수
        /* Promise는 나중에 완료되는 비동기 작업 결과를 담은 객체이고, clean-up 함수는 React 컴포넌트가 사라질 때 실행되는 정리용 함수입니다.
            db에 저장하는 행위는 비동기 처리이므로 await를 사용해야함(아니면 데이터 값이 아닌 그냥 promise객체 값이 할당 될 수잇음.)
            참고로 then도 비동기 처리 방식이다. 어느 방식 사용해도 무방!
        */
        const loadData = async () => {
            const data = await getOne(demNum);
            setDem(data);
            setReturnDate(new Date(data.expDate + "T00:00:00"));

            if (data.imageUrlList && data.imageUrlList.length > 0) {
                const fullUrls = data.imageUrlList.map(img => serverHost + 'view/' + img); // 서버 측에서 요청할 경로 지정
                const fileList = await urlListToFileList(fullUrls, data.imageNameList); // 파일 객체를 받아옴

                const fileListWithIsMain = fileList.map((fileObj, index) => ({
                    ...fileObj,
                    isMain: data.isMain ? data.isMain[index] === 'true' : false  // data.isMainList가 있으면 해당 인덱스 값, 없으면 0으로 기본 설정
                }));

                setImages(fileListWithIsMain);
            }

        };
        loadData();
    }, [demNum]);
    // useEffect를 사용하면 최초 1회, 그리고 의존성 배열이 있을경우,
    // 의존성 배열 변경 시 실행된다.

    useEffect(() => {
        console.log("렌더링 직후 images 상태: ", images);
    }, [images]);

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
         if (files.length > 8) { // 한 번에 선택한 이미지가 9개 이상일 때
            alert("이미지는 최대 8개까지만 업로드할 수 있습니다.");
            e.target.value = ""; // 선택 초기화 (필요시)
            return;
        }
        const filePreviews = files.map((file, index) => ({
            file,
            url: URL.createObjectURL(file),
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


    const update = () => {
        const newErrors = {};

        // 검증 로직 (생략 가능)
        if (!dem.demName.trim()) newErrors.demName = "물품명은 필수입니다.";
        if (!dem.demMfr.trim()) newErrors.demMfr = "제조사는 필수입니다.";
        if (!dem.demInfo.trim()) newErrors.demInfo = "물품 설명은 필수입니다.";
        if (images.length === 0) newErrors.images = "이미지는 최소 1장 등록해야 합니다.";
        if (!Number.isInteger(Number(dem.itemNum)) || Number(dem.itemNum) < 0) {
            newErrors.itemNum = "수량은 0이상이여야 합니다.";
        }

        // 날짜가 오늘 이후인지 체크
        const today = new Date();
        // 비교할 때 시간을 (00:00:00으로 맞춤)
        today.setHours(0, 0, 0, 0); // 
        const selectedDate = new Date(dem.expDate);
        selectedDate.setHours(0, 0, 0, 0);
        if (selectedDate <= today) {
            newErrors.expDate = "반납 날짜는 오늘 이후여야 합니다.";
        }

        setErrors(newErrors);
        // object.key 객체의 key를 배열 형태로 변환 후, length로 오류가 잇다면 return 시킴
        if (Object.keys(newErrors).length > 0) return;

        const formData = new FormData();
        const mainImageIndex = images.findIndex((img) => img.isMain === 1);
        // dem 객체+반납일 객체 생성
        const demCopy = {
            ...dem,
            expDate: selectedDate.toISOString().split("T")[0],
            mainImageIndex: mainImageIndex === -1 ? 0 : mainImageIndex
        };

        // demCopy를 JSON 문자열로 만들고 Blob 생성해서 append (기존에 formdata에 한번에 보내는 것보다 업로드 가능한 파일갯수가 2배늘어남)
        formData.append(
            "demonstrationFormDTO",
            new Blob([JSON.stringify(demCopy)], { type: "application/json" })
        );

        // formData에 이미지 넣기
        images.forEach((img) => {
            formData.append("imageList", img.file);
        });

        putUpdate(formData)
            .then(() => {
                alert("상품 수정 완료");
                moveToPath("/");
            })
            .catch((err) => {
                console.error(err);
                alert("상품 수정 실패");
            });


    }


    const handleCheckboxChange = (selectedIndex) => {
        const currentMainIndex = images.findIndex(img => img.isMain === 1);
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
                {images.map((img, index) => 
                
                (
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
}
export default UpdateComponent;