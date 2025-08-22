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

  const initState = { demName: "", demMfr: "", itemNum: 0, demInfo: "", category: "" };
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
    const newErrors = {};
    if (!dem.demName.trim()) newErrors.demName = "물품명은 필수입니다.";
    if (!dem.demMfr.trim()) newErrors.demMfr = "제조사는 필수입니다.";
    if (!dem.demInfo.trim()) newErrors.demInfo = "물품 설명은 필수입니다.";
    if (images.length === 0) newErrors.images = "이미지는 최소 1장 등록해야 합니다.";
    if (!Number.isInteger(Number(dem.itemNum)) || Number(dem.itemNum) < 0) newErrors.itemNum = "수량은 0이상이어야 합니다.";
    if (!dem.category) {
      newErrors.category = "카테고리를 선택해주세요.";
    }
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const selectedDate = new Date(dem.expDate);
    selectedDate.setHours(0, 0, 0, 0);
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
        alert("물품 수정 완료");
        moveToPath("/");
      })
      .catch(err => {
        console.error(err);
        alert("물품 수정 실패");
      });
  };

  return (

    <div className='max-w-screen-md mx-auto'>
      <div className="page-shadow min-blank my-10 p-10 space-y-6 text-center">
        <div className="newText-3xl font-bold">실증 물품 수정</div>

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
              type="text"
              placeholder="개수를 입력해주세요."
              className="flex-1 input-focus"
              name="itemNum"
              value={dem.itemNum}
              onChange={handleChangeDem}
            />
          </div>
          {errors.itemNum && <p className="text-red-600 mt-1 newText-base text-left ml-32">{errors.itemNum}</p>}
        </div>

        <div>
          <div className="flex items-center newText-base">
            <label className="newText-xl font-semibold w-32">카테고리:</label>
            <select
              name="category"
              className="w-32 border border-black input-focus"
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


        {/* 이미지 선택 */}
        <div className="flex items-center">
          <label className="newText-xl font-semibold w-32">이미지:</label>
          <button type="button" onClick={() => document.getElementById("fileInput").click()}
            className="border p-2 cursor-pointer input-focus">
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
        {errors.images && <p className="text-red-600 mt-1 newText-base text-left ml-32">{errors.images}</p>}

        {/* 이미지 미리보기 */}
        {images.length > 0 && (
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mt-4 W-32">
            {images.map((img, index) => (
              <div key={img.url} className="flex flex-col items-start border rounded p-2 shadow">
                <div className="flex justify-between w-full items-center mb-1">
                  <label className="newText-base ">대표 이미지</label>
                  <input type="checkbox" checked={Boolean(img.isMain)}
                    onChange={() => handleCheckboxChange(index)}
                    className="w-4 h-4" />
                </div>
                <img src={img.url} alt="preview" className="w-full h-32 object-cover rounded" />
                <p className="newText-sm mt-1">{img.name}</p>
                <button type="button" onClick={() => fileDelete(img)}
                  className="newText-base text-red-600 mt-1 hover:text-red-800">
                  삭제
                </button>
              </div>
            ))}
          </div>
        )}

        {/* 버튼 */}
        <div className="mt-4 flex justify-end gap-4 pr-2">
          <button className="positive-button hover:positive-button:hover active:positive-button:active" onClick={update}>
            물품수정
          </button>
          <button className="normal-button hover:normal-button:hover active:normal-button:active" onClick={moveToReturn}>
            뒤로가기
          </button>
        </div>
      </div>
    </div>
  );
};

export default UpdateComponent;
