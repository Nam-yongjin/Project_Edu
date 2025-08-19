import { useState, useEffect } from "react";
import { ko } from "date-fns/locale";
import { postAdd } from "../../api/demApi";
import useMove from "../../hooks/useMove";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { useSelector } from "react-redux";

const AddComponent = () => {
  const initState = { demName: "", demMfr: "", itemNum: 0, demInfo: "", expDate: new Date(),category:"" };
  const isCompany = useSelector((state) => state.loginState?.role === "COMPANY");
  const isAdmin = useSelector((state) => state.loginState?.role === "ADMIN");
  const [images, setImages] = useState([]);
  const [dem, setDem] = useState({ ...initState });
  const [returnDate, setReturnDate] = useState(new Date());
  const { moveToPath, moveToReturn } = useMove();
  const [errors, setErrors] = useState({});
  const [fileInputKey, setFileInputKey] = useState(Date.now());
  // 권한 체크
  useEffect(() => {
    if (!isCompany && !isAdmin) {
      alert("권한이 없습니다.");
      moveToPath("/");
    }
  }, [isCompany, isAdmin]);

  // 날짜를 yyyy-MM-dd 문자열로 변환하는 함수
  const formatDate = (date) => {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, "0");
    const d = String(date.getDate()).padStart(2, "0");
    return `${y}-${m}-${d}`;
  };

  const addDem = () => {
    const newErrors = {};
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

    setErrors(newErrors);
    if (Object.keys(newErrors).length > 0) return;

    const formData = new FormData();
    const mainImageIndex = images.findIndex((img) => img.isMain === 1);

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

    postAdd(formData)
      .then(() => {
        alert("상품 등록 완료");
        moveToPath("/");
      })
      .catch((err) => {
        console.error(err);
        alert("상품 등록 실패");
      });
  };

  const handleChangeDem = (e) => {
    setDem(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleReturnDateChange = (date) => {
    // date 객체 그대로 사용
    setReturnDate(date);
    setDem(prev => ({ ...prev, expDate: date }));
  };

  const handleFileChange = (e) => {
    const files = Array.from(e.target.files);
    if (files.length > 8) {
      alert("이미지는 최대 8개까지만 업로드할 수 있습니다.");
      e.target.value = "";
      return;
    }
    const filePreviews = files.map((file, index) => ({
      file,
      url: URL.createObjectURL(file),
      name: file.name,
      isMain: index === 0
    }));
    setImages(filePreviews);
  };

  const fileDelete = (imgToDelete) => {
    setImages((prevImages) => {
      const newImages = prevImages.filter(img => img.url !== imgToDelete.url);
      if (newImages.length === 0) setFileInputKey(Date.now());
      return newImages;
    });
  };

  const handleCheckboxChange = (selectedIndex) => {
    setImages(images.map((img, idx) => ({
      ...img,
      isMain: idx === selectedIndex ? 1 : 0
    })));
  };

  return (
     <div className='max-w-screen-md mx-auto'>
            <div className="page-shadow min-blank my-10 p-10 space-y-6 text-center">
                <div className="newText-3xl font-bold">물품등록</div>
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

        <div className="flex items-center">
          <label className="text-xl font-semibold w-[120px]">카테고리:</label>
          <select
          name="category"
            className="border rounded p-2"
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

        {errors.category && <p className="text-red-600 text-sm mt-1 ml-[120px]">{errors.category}</p>}

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

        {/* 반납 날짜 */}
        <div className="flex items-center">
          <label className="text-xl font-semibold w-[120px]">반납 날짜:</label>
          <DatePicker
            className="border p-3 text-lg flex-1 min-w-0 box-border"
            selected={returnDate}
            onChange={handleReturnDateChange}
            dateFormat="yyyy-MM-dd"
            placeholderText="날짜를 선택하세요"
            minDate={new Date()}
            locale={ko}
          />
        </div>
        {errors.expDate && <p className="text-red-600 text-sm mt-1 ml-[120px]">{errors.expDate}</p>}

        {/* 이미지 선택 */}
        <div className="flex items-center mt-3">
          <label className="text-xl font-semibold w-[120px]">이미지:</label>
          <input
            key={fileInputKey}
            type="file"
            multiple
            accept="image/*"
            onChange={handleFileChange}
            className="border p-2 text-base flex-1 cursor-pointer min-w-0 box-border"
          />
        </div>
        {errors.images && <p className="text-red-600 text-sm ml-[120px]">{errors.images}</p>}

        {/* 이미지 미리보기 */}
        {images.length > 0 && (
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mt-4 ml-[120px]">
            {images.map((img, index) => (
              <div key={img.url} className="flex flex-col items-start border rounded p-2 shadow">
                <div className="flex justify-between w-full items-center mb-1">
                  <label className="text-xs font-medium">대표 이미지</label>
                  <input
                    type="checkbox"
                    checked={Boolean(img.isMain)}
                    onChange={() => handleCheckboxChange(index)}
                    className="w-4 h-4"
                  />
                </div>
                <img src={img.url} alt="preview" className="w-full h-32 object-cover rounded" />
                <p className="text-xs text-gray-500 break-all mt-1">{img.name}</p>
                <button
                  className="text-xs text-red-500 self-end mt-1 hover:underline"
                  onClick={() => fileDelete(img)}
                >
                  삭제
                </button>
              </div>
            ))}
          </div>
        )}

        <div className="mt-4 flex justify-end gap-4 pr-2">
          <button
            className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 shadow"
            onClick={addDem}
          >
            상품 등록
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
  );
};

export default AddComponent;