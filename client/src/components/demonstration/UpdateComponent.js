import { useState, useEffect } from "react";
import { ko } from "date-fns/locale";
import { getOne, putUpdate } from "../../api/demApi";
import { urlListToFileList } from "../../api/urlToFileApi";
import useMove from "../../hooks/useMove";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { useSelector } from "react-redux";

const UpdateComponent = ({ demNum }) => {
  const isCompany = useSelector((state) => state.loginState?.role === "COMPANY");
  const isAdmin = useSelector((state) => state.loginState?.role === "ADMIN");
  const { moveToPath, moveToReturn } = useMove();

  const initState = { demName: "", demMfr: "", itemNum: 0, demInfo: "" };
  const [dem, setDem] = useState({ ...initState });
  const [returnDate, setReturnDate] = useState(new Date());
  const [images, setImages] = useState([]);
  const [fileInputKey, setFileInputKey] = useState(Date.now());
  const [errors, setErrors] = useState({});
  const serverHost = "http://localhost:8090/";

  useEffect(() => {
    if (!isCompany && !isAdmin) {
      alert("권한이 없습니다.");
      moveToPath("/");
    }
  }, []);

  useEffect(() => {
    const loadData = async () => {
      const data = await getOne(demNum);
      setDem(data);

      // 반납 날짜 처리 (string → Date)
      const expDate = new Date(data.expDate);
      setReturnDate(expDate);

      if (data.imageUrlList && data.imageUrlList.length > 0) {
        const fullUrls = data.imageUrlList.map(img => serverHost + 'view/' + img);
        const fileList = await urlListToFileList(fullUrls, data.imageNameList);

        const fileListWithIsMain = fileList.map((fileObj, index) => ({
          ...fileObj,
          isMain: data.isMain ? data.isMain[index] === 'true' : false
        }));

        setImages(fileListWithIsMain);
      }
    };
    loadData();
  }, [demNum]);

  const handleChangeDem = (e) => {
    setDem(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleReturnDateChange = (date) => {
    // DatePicker에서 선택한 Date 객체 그대로 사용
    setReturnDate(date);
    setDem(prev => ({ ...prev, expDate: date }));
  };

  // 날짜를 yyyy-MM-dd 형식 문자열로 변환
  const formatDate = (date) => {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, "0");
    const d = String(date.getDate()).padStart(2, "0");
    return `${y}-${m}-${d}`;
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
    setImages(prev => {
      const newImages = prev.filter(img => img.url !== imgToDelete.url);
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

  const update = () => {
    const newErrors = {};
    if (!dem.demName.trim()) newErrors.demName = "물품명은 필수입니다.";
    if (!dem.demMfr.trim()) newErrors.demMfr = "제조사는 필수입니다.";
    if (!dem.demInfo.trim()) newErrors.demInfo = "물품 설명은 필수입니다.";
    if (images.length === 0) newErrors.images = "이미지는 최소 1장 등록해야 합니다.";
    if (!Number.isInteger(Number(dem.itemNum)) || Number(dem.itemNum) < 0) newErrors.itemNum = "수량은 0이상이어야 합니다.";

    const today = new Date();
    today.setHours(0,0,0,0);
    const selectedDate = new Date(dem.expDate);
    selectedDate.setHours(0,0,0,0);
    if (selectedDate <= today) newErrors.expDate = "반납 날짜는 오늘 이후여야 합니다.";

    setErrors(newErrors);
    if (Object.keys(newErrors).length > 0) return;

    const formData = new FormData();
    const mainImageIndex = images.findIndex((img) => img.isMain === 1);

    const demCopy = {
      ...dem,
      expDate: formatDate(selectedDate),
      mainImageIndex: mainImageIndex === -1 ? 0 : mainImageIndex
    };

    formData.append("demonstrationFormDTO", new Blob([JSON.stringify(demCopy)], { type: "application/json" }));
    images.forEach(img => formData.append("imageList", img.file));

    putUpdate(formData)
      .then(() => {
        alert("상품 수정 완료");
        moveToPath("/");
      })
      .catch(err => {
        console.error(err);
        alert("상품 수정 실패");
      });
  };

  return (
    <div className="flex mt-10 max-w-6xl mx-auto">
      <div className="space-y-6 w-full">
        {/* 이름/제조사/개수/소개 */}
        <div className="flex items-center">
          <label className="text-xl font-semibold w-[120px]">물품명:</label>
          <input type="text" name="demName" value={dem.demName} onChange={handleChangeDem}
            placeholder="제품 이름을 입력해주세요." className="border p-3 text-lg flex-1 min-w-0 box-border"/>
        </div>
        {errors.demName && <p className="text-red-600 text-sm mt-1 ml-[120px]">{errors.demName}</p>}

        <div className="flex items-center">
          <label className="text-xl font-semibold w-[120px]">제조사:</label>
          <input type="text" name="demMfr" value={dem.demMfr} onChange={handleChangeDem}
            placeholder="제조사를 입력해주세요." className="border p-3 text-lg flex-1 min-w-0 box-border"/>
        </div>
        {errors.demMfr && <p className="text-red-600 text-sm mt-1 ml-[120px]">{errors.demMfr}</p>}

        <div className="flex items-center">
          <label className="text-xl font-semibold w-[120px]">개수:</label>
          <input type="text" name="itemNum" value={dem.itemNum} onChange={handleChangeDem}
            placeholder="개수를 입력해주세요." className="border p-3 text-lg flex-1 min-w-0 box-border"/>
        </div>
        {errors.itemNum && <p className="text-red-600 text-sm mt-1 ml-[120px]">{errors.itemNum}</p>}

        <div className="flex items-start">
          <label className="text-xl font-semibold w-[120px] pt-3">소개:</label>
          <textarea rows={5} name="demInfo" value={dem.demInfo} onChange={handleChangeDem}
            placeholder="제품 소개를 입력해주세요." className="border p-3 text-lg flex-1 resize-y min-w-0 box-border"/>
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
            minDate={new Date()}
            placeholderText="날짜를 선택하세요"
            locale={ko}
          />
        </div>
        {errors.expDate && <p className="text-red-600 text-sm mt-1 ml-[120px]">{errors.expDate}</p>}

        {/* 이미지 선택 */}
        <div className="flex items-center mt-3">
          <label className="text-xl font-semibold w-[120px]">이미지:</label>
          <button type="button" onClick={() => document.getElementById("fileInput").click()}
            className="border p-2 cursor-pointer">
            파일 선택
          </button>
          <input
            id="fileInput"
            key={fileInputKey}
            type="file"
            multiple
            accept="image/*"
            onChange={handleFileChange}
            style={{ display: "none" }}
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
                  <input type="checkbox" checked={Boolean(img.isMain)}
                    onChange={() => handleCheckboxChange(index)}
                    className="w-4 h-4"/>
                </div>
                <img src={img.url} alt="preview" className="w-full h-32 object-cover rounded"/>
                <p className="text-sm mt-1 break-words">{img.name}</p>
                <button type="button" onClick={() => fileDelete(img)}
                  className="text-red-600 text-xs mt-1 self-end hover:text-red-800">
                  삭제
                </button>
              </div>
            ))}
          </div>
        )}

        {/* 버튼 */}
        <div className="mt-4 flex justify-end gap-4 pr-2">
          <button className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 shadow" onClick={update}>
            상품 수정
          </button>
          <button className="bg-gray-400 text-white px-4 py-2 rounded-md hover:bg-gray-500 shadow" onClick={moveToReturn}>
            뒤로가기
          </button>
        </div>
      </div>
    </div>
  );
};

export default UpdateComponent;
