import { useState } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { ko } from "date-fns/locale";
import { postAddEvent } from "../../api/eventApi";
import useMove from "../../hooks/useMove";

const EvtAddComponent = () => {
  const initState = {
    eventName: "",
    maxCapacity: 0,
    eventInfo: "",
    place: "",
    etc: "",
    category: "USER",
    daysOfWeek: [],
    applyStartPeriod: new Date(),
    applyEndPeriod: new Date(),
    eventStartPeriod: new Date(),
    eventEndPeriod: new Date(),
  };

  const [evt, setEvt] = useState(initState);
  const [mainImage, setMainImage] = useState(null);
  const [imageList, setImageList] = useState([]);
  const [attachFiles, setAttachFiles] = useState([]);
  const [mainFile, setMainFile] = useState(null);
  const { moveToPath, moveToReturn } = useMove();

  const handleChangeEvt = (e) => {
    const { name, value } = e.target;
    setEvt((prev) => ({ ...prev, [name]: value }));
  };

  const handleDateChange = (name, date) => {
    setEvt((prev) => ({ ...prev, [name]: date }));
  };

  const handleImageChange = (e) => {
    const files = Array.from(e.target.files);
    if (files.length === 0) return;

    const previews = files.map((file) => ({
      file,
      url: URL.createObjectURL(file),
      name: file.name,
    }));

    setMainImage(previews[0]);
    setImageList(previews.slice(1));
  };

  const deleteMainImage = () => {
    setMainImage(null);
  };

  const deleteSubImage = (index) => {
    setImageList((prev) => prev.filter((_, i) => i !== index));
  };

  const handleAttachChange = (e) => {
    const files = Array.from(e.target.files);
    if (files.length > 0) {
      setMainFile(files[0]);
      setAttachFiles(files.slice(1));
    }
  };

  const formatDateTime = (date) => {
    if (!(date instanceof Date) || isNaN(date)) return "";
    const yyyy = date.getFullYear();
    const MM = String(date.getMonth() + 1).padStart(2, "0");
    const dd = String(date.getDate()).padStart(2, "0");
    const HH = String(date.getHours()).padStart(2, "0");
    const mm = String(date.getMinutes()).padStart(2, "0");
    return `${yyyy}-${MM}-${dd} ${HH}:${mm}`;
  };

  const register = () => {
  const formData = new FormData();

  // 디버깅 로그 - 대표 및 서브 이미지/파일 확인
  console.log("== FormData 체크 ==");
  console.log("대표 이미지:", mainImage);
  console.log("대표 첨부파일:", mainFile);
  console.log("서브 이미지 리스트:", imageList);
  console.log("서브 첨부파일 리스트:", attachFiles);
  console.log("DTO 내용:", evt);

  // 대표 이미지 추가
  if (mainImage) {
    formData.append("mainImage", mainImage.file);
  }

  // 서브 이미지 추가
  imageList.forEach((img) => {
    formData.append("imageList", img.file);
  });

  // 대표 첨부파일 추가
  if (mainFile) {
    formData.append("mainFile", mainFile);
  }

  // 서브 첨부파일 추가
  attachFiles.forEach((file) => {
    formData.append("attachList", file);
  });

  // DTO 구성
  const dto = {
    ...evt,
    applyStartPeriod: formatDateTime(evt.applyStartPeriod),
    applyEndPeriod: formatDateTime(evt.applyEndPeriod),
    eventStartPeriod: formatDateTime(evt.eventStartPeriod),
    eventEndPeriod: formatDateTime(evt.eventEndPeriod),
  };

  // JSON Blob으로 DTO 추가
  const jsonBlob = new Blob([JSON.stringify(dto)], {
    type: "application/json",
  });
  formData.append("dto", jsonBlob);

  // 디버깅 로그 - 최종 FormData 확인
  console.log("== FormData 최종 구성 확인 ==");
  for (let pair of formData.entries()) {
    console.log(pair[0], pair[1]);
  }

  // API 전송
  postAddEvent(formData)
    .then(() => {
      alert("행사 등록 완료");
      moveToPath("/event/list");
    })
    .catch((error) => {
      console.error("등록 실패", error);
      alert(
        "등록 실패: " +
          (error.response?.data?.message ||
            JSON.stringify(error.response?.data) ||
            error.message)
      );
    });
};

  return (
    <div className="flex mt-10 max-w-6xl mx-auto">
      <div className="space-y-6 w-2/3">
        {/* 행사명 */}
        <div className="flex items-center">
          <label className="text-xl font-semibold w-[120px]">행사명:</label>
          <input
            type="text"
            name="eventName"
            value={evt.eventName}
            onChange={handleChangeEvt}
            placeholder="행사명을 입력하세요"
            className="border p-3 text-lg flex-1"
          />
        </div>

        {/* 소개 */}
        <div className="flex items-start">
          <label className="text-xl font-semibold w-[120px] pt-3">소개:</label>
          <textarea
            name="eventInfo"
            value={evt.eventInfo}
            onChange={handleChangeEvt}
            rows={5}
            className="border p-3 text-lg flex-1 resize-y"
            placeholder="행사 소개 입력"
          />
        </div>

        {/* 장소 */}
        <div className="flex items-center">
          <label className="text-xl font-semibold w-[120px]">장소:</label>
          <input
            type="text"
            name="place"
            value={evt.place}
            onChange={handleChangeEvt}
            placeholder="행사 장소"
            className="border p-3 text-lg flex-1"
          />
        </div>

        {/* 모집 대상 */}
        <div className="flex items-center">
          <label className="text-xl font-semibold w-[120px]">모집 대상:</label>
          <select
            name="category"
            value={evt.category}
            onChange={handleChangeEvt}
            className="border p-3 text-lg flex-1"
          >
            <option value="USER">일반인</option>
            <option value="STUDENT">학생</option>
            <option value="TEACHER">교수</option>
          </select>
        </div>

        {/* 날짜 선택 */}
        {[
          { label: "모집 시작", name: "applyStartPeriod", value: evt.applyStartPeriod },
          { label: "모집 종료", name: "applyEndPeriod", value: evt.applyEndPeriod },
          { label: "행사 시작", name: "eventStartPeriod", value: evt.eventStartPeriod },
          { label: "행사 종료", name: "eventEndPeriod", value: evt.eventEndPeriod },
        ].map((item) => (
          <div key={item.name} className="flex items-center">
            <label className="text-xl font-semibold w-[120px]">{item.label}:</label>
            <DatePicker
              selected={item.value}
              onChange={(date) => handleDateChange(item.name, date)}
              showTimeSelect
              dateFormat="yyyy-MM-dd HH:mm"
              timeFormat="HH:mm"
              className="border p-3 text-lg flex-1"
              locale={ko}
              minDate={new Date()}
            />
          </div>
        ))}

        {/* 요일 선택 */}
        <div className="flex items-center">
          <label className="text-xl font-semibold w-[120px]">요일 선택:</label>
          <div className="flex gap-2">
            {[1, 2, 3, 4, 5].map((day) => (
              <label key={day} className="flex items-center gap-1">
                <input
                  type="checkbox"
                  checked={evt.daysOfWeek.includes(day)}
                  onChange={(e) => {
                    const newDays = e.target.checked
                      ? [...evt.daysOfWeek, day]
                      : evt.daysOfWeek.filter((d) => d !== day);
                    setEvt((prev) => ({ ...prev, daysOfWeek: newDays }));
                  }}
                />
                {"월화수목금"[day - 1]}
              </label>
            ))}
          </div>
        </div>

        {/* 최대 인원 */}
        <div className="flex items-center">
          <label className="text-xl font-semibold w-[120px]">최대 인원:</label>
          <input
            type="number"
            name="maxCapacity"
            value={evt.maxCapacity}
            onChange={handleChangeEvt}
            className="border p-3 text-lg flex-1"
          />
        </div>

        {/* 기타 유의사항 */}
        <div className="flex items-start">
          <label className="text-xl font-semibold w-[120px] pt-3">유의사항:</label>
          <textarea
            name="etc"
            value={evt.etc}
            onChange={handleChangeEvt}
            rows={3}
            className="border p-3 text-lg flex-1 resize-y"
            placeholder="기타 유의사항 입력"
          />
        </div>

        {/* 이미지 업로드 */}
        <div className="flex items-center mt-3">
          <label className="text-xl font-semibold w-[120px]">이미지:</label>
          <input
            type="file"
            multiple
            accept="image/*"
            onChange={handleImageChange}
            className="border p-2 text-base flex-1"
          />
        </div>

        {/* 첨부파일 */}
        <div className="flex items-center mt-3">
          <label className="text-xl font-semibold w-[120px]">첨부파일:</label>
          <input
            type="file"
            multiple
            accept=".pdf,.hwp,.doc,.docx"
            onChange={handleAttachChange}
            className="border p-2 text-base flex-1"
          />
        </div>

        {/* 버튼 */}
        <div className="mt-4 flex justify-end gap-4">
          <button onClick={register} className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700">
            행사 등록
          </button>
          <button onClick={moveToReturn} className="bg-gray-400 text-white px-4 py-2 rounded-md hover:bg-gray-500">
            뒤로가기
          </button>
        </div>
      </div>

      {/* 미리보기 */}
      <div className="w-1/3 pl-10 flex flex-col gap-4">
        {/* 대표 이미지 */}
        {mainImage && (
          <div className="border rounded p-2 shadow">
            <div className="flex justify-between items-center">
              <p className="text-sm font-semibold text-blue-600">대표 이미지</p>
              <button className="text-red-500 text-sm" onClick={deleteMainImage}>삭제</button>
            </div>
            <img src={mainImage.url} alt="대표이미지" className="w-32 h-32 object-cover rounded" />
            <p className="text-sm break-words">{mainImage.name}</p>
          </div>
        )}

        {/* 서브 이미지 */}
        {imageList.map((img, idx) => (
          <div key={idx} className="border rounded p-2 shadow">
            <div className="flex justify-between items-center">
              <p className="text-sm text-gray-600">이미지 {idx + 1}</p>
              <button className="text-red-500 text-sm" onClick={() => deleteSubImage(idx)}>삭제</button>
            </div>
            <img src={img.url} alt={`미리보기${idx}`} className="w-32 h-32 object-cover rounded" />
            <p className="text-sm break-words">{img.name}</p>
          </div>
        ))}

        {/* 첨부파일 */}
        {mainFile && (
          <div className="border rounded p-2 shadow">
            <p className="text-sm font-semibold text-blue-600">대표 첨부파일</p>
            <p className="text-sm break-words">{mainFile.name}</p>
          </div>
        )}
        {attachFiles.map((file, index) => (
          <div key={index} className="border rounded p-2 shadow">
            <p className="text-sm text-gray-700">기타 첨부파일</p>
            <p className="text-sm break-words">{file.name}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default EvtAddComponent;