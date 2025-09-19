import { useState, useEffect } from "react";
import { ko } from "date-fns/locale";
import { postAdd } from "../../api/demApi";
import useMove from "../../hooks/useMove";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { useSelector } from "react-redux";

const AddComponent = () => {
  const initState = { demName: "", demMfr: "", itemNum: 0, demInfo: "", expDate: new Date(), category: "", mainImageIndex: 0 }; // dem초기 설정값
  const isCompany = useSelector((state) => state.loginState?.role === "COMPANY"); // 기업 여부
  const isAdmin = useSelector((state) => state.loginState?.role === "ADMIN"); // 관리자 여부
  const [images, setImages] = useState([]); // 이미지
  const [dem, setDem] = useState({ ...initState }); // 보낼 상품 정보
  const [returnDate, setReturnDate] = useState(new Date()); // 만료 일자
  const { moveToPath, moveToReturn } = useMove(); // 이동 변수
  const [errors, setErrors] = useState({}); // input창 예외 처리를 위한 error 객체
  const [fileInputKey, setFileInputKey] = useState(Date.now()); // 파일 입력란 리마운트 위한 변수

  // 권한 체크
  useEffect(() => {
    if (!isCompany && !isAdmin) {
      alert("권한이 없습니다.");
      moveToPath("/");
    }
  }, [isCompany, isAdmin]);

  // 날짜를 yyyy-MM-dd 문자열로 변환하는 함수
  // 백에서 localdate로 받아야 하니 형식을 맞춤
  const formatDate = (date) => {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, "0");
    const d = String(date.getDate()).padStart(2, "0");
    return `${y}-${m}-${d}`;
  };

  // 실증 등록
  const addDem = () => {
    if (dem.demName.length > 100) {
      alert("물품명은 100자 이내여야 합니다.");
      return;
    }
    if (dem.demInfo.length > 1000) {
      alert("물품 소개는 1000자 이내여야 합니다.");
      return;
    }
    if (dem.demMfr.length > 200) {
      alert("제조사는 200자 이내여야 합니다.");
      return;
    }

    const newErrors = {}; // 에러 메시지를 담는 객체
    if (!dem.demName.trim()) newErrors.demName = "물품명은 필수입니다.";
    if (!dem.demMfr.trim()) newErrors.demMfr = "제조사는 필수입니다.";
    if (!dem.demInfo.trim()) newErrors.demInfo = "물품 설명은 필수입니다.";
    if (images.length === 0) newErrors.images = "이미지는 최소 1장 등록해야 합니다.";
    if (!Number.isInteger(Number(dem.itemNum)) || Number(dem.itemNum) < 0) {
      newErrors.itemNum = "수량은 0이상이여야 합니다.";
    }
    if (!dem.category) {
      newErrors.category = "카테고리를 선택해주세요.";
    }

    // 날짜 체크
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const selectedDate = new Date(dem.expDate);
    selectedDate.setHours(0, 0, 0, 0);
    if (selectedDate <= today) {
      newErrors.expDate = "반납 날짜는 오늘 이후여야 합니다.";
    }

    setErrors(newErrors); // error state변수에 추가한 error메시지 객체 set

    if (Object.keys(newErrors).length > 0) return; // error메시지가 있을 경우, 리턴

    const formData = new FormData();
    const mainImageIndex = images.findIndex((img) => img.isMain === 1); // 메인 이미지 여부 확인

    const demCopy = {
      ...dem,
      expDate: formatDate(selectedDate),
      mainImageIndex: mainImageIndex === -1 ? 0 : mainImageIndex
    };


    formData.append(
      "demonstrationFormDTO",
      new Blob([JSON.stringify(demCopy)], { type: "application/json" })
    );

    images.forEach((img) => {
      formData.append("imageList", img.file);
    });

    postAdd(formData) // 서버에 데이터 전달
      .then(() => {
        alert("실증 등록 완료");
        moveToPath("/");
      })
      .catch((err) => {
        console.error(err);
        alert("실증 등록 실패");
      });
  };

  // input의 내용이 바뀌었을때, 상태값 변경
  const handleChangeDem = (e) => {
    setDem(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  // datepicker의 값이 바뀌었을때 날짜 상태값 변경
  const handleReturnDateChange = (date) => {
    // date 객체 그대로 사용
    setReturnDate(date);
    setDem(prev => ({ ...prev, expDate: date }));
  };

  // 이미지 업로드 이벤트 리스너
  const handleFileChange = (e) => {
    const fileError = { error: [] };
    const ALLOWED_FILE_TYPES = ["jpg", "jpeg", "png"];
    const MAX_FILE_SIZE = 100 * 1024 * 1024;
    const files = Array.from(e.target.files);
    if (files.length > 8) {
      alert("이미지는 최대 8개까지만 업로드할 수 있습니다.");
      e.target.value = "";
      return;
    }
    files.forEach(file => {
      const ext = file.name.split('.').pop().toLowerCase();
      if (!ALLOWED_FILE_TYPES.includes(ext)) {
        fileError.error.push("jpg, jpeg, png 파일의 확장자만 업로드 가능합니다.");
        fileError.error.push(file.name);
        return;
      } else if (file.size > MAX_FILE_SIZE) {
        fileError.error.push("최대 100MB까지의 파일만 업로드 가능합니다.");
        fileError.error.push(file.name);
        return;
      }
    });

    if (fileError.error.length === 0) {
      const filePreviews = files.map((file, index) => ({
        file,
        url: URL.createObjectURL(file),
        name: file.name,
        isMain: index === 0
      }));
      setImages(filePreviews);
    }
    else {
      alert(fileError.error[0] + " 파일이름 : " + fileError.error[1]);
      setFileInputKey(Date.now());
      setImages([]);
    }
  };

  // 파일 삭제 리스너
  const fileDelete = (imgToDelete) => {
    setImages((prevImages) => {
      const newImages = prevImages.filter(img => img.url !== imgToDelete.url);
      if (newImages.length === 0) setFileInputKey(Date.now());
      return newImages;
    });
  };

  // 메인 이미지 설정
  const handleCheckboxChange = (selectedIndex) => {
    setImages(images.map((img, idx) => ({
      ...img,
      isMain: idx === selectedIndex ? 1 : 0
    })));
  };

  return (
    <div className='max-w-screen-md mx-auto'>
      <div className="page-shadow min-blank my-10 p-10 space-y-6 text-center">
        <div className="newText-3xl font-bold">실증 등록</div>

        <div>
          <div className="flex items-start newText-base">
            <label className="newText-xl font-semibold w-32">물품명:</label>
            <input
              type="text"
              placeholder="물품명을 입력해주세요."
              className="flex-1 input-focus"
              name="demName"
              value={dem.demName}
              onChange={handleChangeDem}
            />
          </div>
          {errors.demName && <p className="text-red-600 mt-1 newText-base text-left ml-32">{errors.demName}</p>}
        </div>

        <div>
          <div className="flex items-center newText-base">
            <label className="newText-xl font-semibold w-32">제조사:</label>
            <input
              type="text"
              placeholder="제조사를 입력해주세요."
              className="flex-1 input-focus"
              name="demMfr"
              value={dem.demMfr}
              onChange={handleChangeDem}
            />
          </div>
          {errors.demMfr && <p className="text-red-600 mt-1 newText-base text-left ml-32">{errors.demMfr}</p>}
        </div>

        <div>
          <div className="flex items-center newText-base">
            <label className="newText-xl font-semibold w-32">개수:</label>
            <input
              type="number"
              placeholder="개수를 입력해주세요."
              className="flex-1 input-focus"
              name="itemNum"
              value={dem.itemNum}
              onChange={handleChangeDem}
              min={0}
              step={1}
            />

          </div>
          {errors.itemNum && <p className="text-red-600 mt-1 newText-base text-left ml-32">{errors.itemNum}</p>}
        </div>

        <div>
          <div className="flex items-center newText-base">
            <label className="newText-xl font-semibold w-32">카테고리:</label>
            <select
              name="category"
              className="w-40 border border-black input-focus"
              value={dem.category}
              onChange={handleChangeDem}
            >
              <option value="">카테고리 선택</option>
              <option value="LAPTOP">노트북</option>
              <option value="TABLET">태블릿</option>
              <option value="PRINTER">프린터</option>
              <option value="VR">학습용VR</option>
              <option value="ROBOT">학습용AI로봇</option>
            </select>
          </div>
          {errors.category && <p className="text-red-600 mt-1 newText-base text-left ml-32">{errors.category}</p>}
        </div>

        <div>
          <div className="flex items-center newText-base">
            <label className="newText-xl font-semibold w-32">소개:</label>
            <textarea
              rows={5}
              placeholder="물품소개를 입력해주세요."
              className="flex-1 input-focus"
              name="demInfo"
              value={dem.demInfo}
              onChange={handleChangeDem}
            />
          </div>
          {errors.demInfo && <p className="text-red-600 mt-1 newText-base text-left ml-32">{errors.demInfo}</p>}
        </div>


        {/* 반납 날짜 */}
        <div>
          <div className="flex items-center">
            <label className="newText-xl font-semibold w-32">반납 날짜:</label>
            <DatePicker
              className="flex-1 border border-black input-focus"
              selected={returnDate}
              onChange={handleReturnDateChange}
              dateFormat="yyyy-MM-dd"
              placeholderText="날짜를 선택하세요"
              minDate={new Date()}
              locale={ko}
            />
          </div>
          {errors.expDate && <p className="text-red-600 mt-1 newText-base text-left ml-32">{errors.expDate}</p>}
        </div>

        <div>
          {/* 이미지 선택 */}
          <div className="flex items-center">
            <label className="newText-xl font-semibold w-32">이미지:</label>
            <input
              key={fileInputKey}
              type="file"
              multiple
              accept="image/*"
              onChange={handleFileChange}
              className="flex-1 border border-black input-focus"
            />
          </div>
          {errors.images && <p className="text-red-600 mt-1 newText-base text-left ml-32">{errors.images}</p>}
        </div>

        {/* 이미지 미리보기 */}
        {images.length > 0 && (
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mt-4 W-32">
            {images.map((img, index) => (
              <div key={img.url} className="flex flex-col items-start border rounded p-2 shadow">
                <div className="flex justify-between w-full items-center mb-1">
                  <label className="newText-base">대표 이미지</label>
                  <input
                    type="checkbox"
                    checked={Boolean(img.isMain)}
                    onChange={() => handleCheckboxChange(index)}
                    className="w-4 h-4"
                  />
                </div>
                <img src={img.url} alt="preview" className="w-full h-32 object-cover rounded" />
                <p className="newText-sm mt-1 truncate w-[120px]" title={img.name}>{img.name}</p>
                <button type="button" onClick={() => fileDelete(img)}
                  className="newText-base text-red-600 mt-1 hover:text-red-800">
                  삭제
                </button>
              </div>
            ))}
          </div>
        )}

        <div className="mt-4 flex justify-end gap-4 pr-2">
          <button
            className="positive-button newText-base hover:positive-button:hover active:positive-button:active"
            onClick={addDem}
          >
            실증등록
          </button>
          <button
            className="normal-button newText-base hover:normal-button:hover active:normal-button:active"
            onClick={moveToReturn}
          >
            뒤로가기
          </button>
        </div>
      </div>
    </div>
  );
};

export default AddComponent;