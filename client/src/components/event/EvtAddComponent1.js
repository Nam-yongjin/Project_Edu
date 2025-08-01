import { useState } from "react";
import { ko } from "date-fns/locale";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import useMove from "../../hooks/useMove";
import { postAddEvent } from "../../api/eventApi"; // 실제 API 함수에 맞게 수정
import axios from "axios";

const EvtAddComponent1 = () => {
  const initState = {
    eventName: "",
    maxCapacity: 0,
    eventInfo: "",
    place: "",
    category: "USER",
    daysOfWeek: [],
    applyStartPeriod: new Date(),
    applyEndPeriod: new Date(),
    eventStartPeriod: new Date(),
    eventEndPeriod: new Date(),
  };

  const [fileInputs, setFileInputs] = useState([Date.now()]);
  const [images, setImages] = useState([]);
  const [evt, setEvt] = useState({ ...initState });
  const [attachFiles, setAttachFiles] = useState([]);
  const { moveToPath, moveToReturn } = useMove();

  const handleChangeEvt = (e) => {
    const { name, value } = e.target;
    setEvt((prev) => ({ ...prev, [name]: value }));
  };

  const handleDateChange = (name, date) => {
    setEvt((prev) => ({ ...prev, [name]: date }));
  };

  const handleFileChange = (id, e) => {
    const files = Array.from(e.target.files);
    if (files.length === 0) {
      setFileInputs((prev) => {
        if (prev.length > 1) {
          setImages((prevImages) =>
            prevImages.filter((img) => img.inputId !== id)
          );
          return prev.filter((inputId) => inputId !== id);
        }
        return prev;
      });
      return;
    }

    const previews = files.map((file) => ({
      file,
      url: URL.createObjectURL(file),
      name: file.name,
      inputId: id,
    }));

    setImages((prev) => [...prev, ...previews]);

    if (fileInputs[fileInputs.length - 1] === id) {
      setFileInputs((prev) => [...prev, Date.now()]);
    }
  };

  const fileDelete = (indexToRemove) => {
    setImages((prev) => {
      const targetId = prev[indexToRemove]?.inputId;
      const updated = prev.filter((_, idx) => idx !== indexToRemove);
      const hasInput = updated.some((img) => img.inputId === targetId);

      if (!hasInput) {
        setFileInputs((prevInputs) =>
          prevInputs.length > 1
            ? prevInputs.filter((id) => id !== targetId)
            : [Date.now()]
        );
      }

      return updated;
    });
  };

  const register = () => {
  const formData = new FormData();

  // 📸 이미지 추가
  for (const { file } of images) {
    formData.append("imageList", file);
  }

  // 📎 첨부파일 추가
  for (const file of attachFiles) {
    formData.append("attachList", file);
  }

  // 🧠 날짜는 ISO 문자열로 변환
  const dto = {
    ...evt,
    applyStartPeriod: evt.applyStartPeriod.toISOString(),
    applyEndPeriod: evt.applyEndPeriod.toISOString(),
    eventStartPeriod: evt.eventStartPeriod.toISOString(),
    eventEndPeriod: evt.eventEndPeriod.toISOString(),
  };

  // 🧱 JSON DTO 추가 (Blob)
  const jsonBlob = new Blob([JSON.stringify(dto)], { type: "application/json" });
  formData.append("dto", jsonBlob);

  // 🚀 등록 요청
  postAddEvent(formData)
    .then(() => {
      alert("행사 등록 완료");
      moveToPath("/event");
    })
    .catch((error) => {
      console.error("🔥 등록 실패", error);
      if (error.response) {
        console.error("📡 응답 상태:", error.response.status);
        console.error("📄 응답 데이터:", error.response.data);
        alert("등록 실패: " + (
          error.response.data.message ||
          JSON.stringify(error.response.data)
        ));
      } else {
        alert("에러: " + error.message);
      }
    });
};

  return (
    <div className="flex mt-10 max-w-6xl mx-auto">
      <div className="space-y-6 w-2/3">
        <div className="flex items-center">
          <label className="text-xl font-semibold w-[120px]">행사명:</label>
          <input
            type="text"
            placeholder="행사 이름을 입력해주세요."
            className="border p-3 text-lg flex-1 min-w-0 box-border"
            name="eventName"
            value={evt.eventName}
            onChange={handleChangeEvt}
          />
        </div>

        <div className="flex items-start">
          <label className="text-xl font-semibold w-[120px] pt-3">소개:</label>
          <textarea
            rows={5}
            placeholder="행사 소개를 입력해주세요."
            className="border p-3 text-lg flex-1 resize-y min-w-0 box-border"
            name="eventInfo"
            value={evt.eventInfo}
            onChange={handleChangeEvt}
          />
        </div>

        <div className="flex items-center">
          <label className="text-xl font-semibold w-[120px]">장소:</label>
          <input
            type="text"
            placeholder="행사 장소 입력"
            className="border p-3 text-lg flex-1 min-w-0 box-border"
            name="place"
            value={evt.place}
            onChange={handleChangeEvt}
          />
        </div>

        {/* 모집대상 선택 */}
        <div className="flex items-center">
        <label className="text-xl font-semibold w-[120px]">모집 대상:</label>
          <select
            name="category"
            value={evt.category}
            onChange={handleChangeEvt}
            className="border p-3 text-lg flex-1 min-w-0 box-border"
          >
            <option value="USER">일반인</option>
            <option value="STUDENT">학생</option>
            <option value="TEACHER">교수</option>
          </select>
        </div>

        {[
          { label: "모집 시작 날짜", name: "applyStartPeriod", value: evt.applyStartPeriod },
          { label: "모집 종료 날짜", name: "applyEndPeriod", value: evt.applyEndPeriod },
          { label: "행사 시작 날짜", name: "eventStartPeriod", value: evt.eventStartPeriod },
          { label: "행사 종료 날짜", name: "eventEndPeriod", value: evt.eventEndPeriod },
        ].map((item) => (
          <div key={item.name} className="flex items-center">
            <label className="text-xl font-semibold w-[120px]">{item.label}:</label>
            <DatePicker
              className="border p-3 text-lg flex-1 min-w-0 box-border"
              selected={item.value}
              onChange={(date) => handleDateChange(item.name, date)}
              dateFormat="yyyy-MM-dd HH:mm"
              showTimeSelect
              timeFormat="HH:mm"
              timeIntervals={30} // 30분 단위 (원하면 15, 60 등으로 변경 가능)
              placeholderText="날짜와 시간을 선택하세요"
              minDate={new Date()}
              locale={ko}
            />
          </div>
        ))}

        <div className="flex items-center">
          <label className="text-xl font-semibold w-[120px]">요일 선택:</label>
          <div className="flex gap-2">
            {[
              { label: "월", value: 1 }, 
              { label: "화", value: 2 }, 
              { label: "수", value: 3 }, 
              { label: "목", value: 4 }, 
              { label: "금", value: 5 }
            ].map((day) => (
              <label key={day.value} className="flex items-center gap-1">
                <input
                  type="checkbox"
                  checked={evt.daysOfWeek.includes(day.value)}
                  onChange={(e) => {
                    const newDays = e.target.checked
                      ? [...evt.daysOfWeek, day.value]
                      : evt.daysOfWeek.filter((d) => d !== day.value);
                    setEvt((prev) => ({ ...prev, daysOfWeek: newDays }));
                  }}
                />
                {day.label}
              </label>
            ))
            }
          </div>
        </div>

        <div className="flex items-center">
          <label className="text-xl font-semibold w-[120px]">최대 인원:</label>
          <input
            type="number"
            placeholder="최대인원을 입력해주세요."
            className="border p-3 text-lg flex-1 min-w-0 box-border"
            name="maxCapacity"
            value={evt.maxCapacity}
            onChange={handleChangeEvt}
          />
        </div>

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

        <div className="flex items-center mt-3">
          <label className="text-xl font-semibold w-[120px]">첨부파일:</label>
          <input
            type="file"
            multiple
            accept=".pdf,.hwp,.doc,.docx"
            onChange={(e) => setAttachFiles(Array.from(e.target.files))}
            className="border p-2 text-base flex-1 cursor-pointer min-w-0 box-border"
          />
        </div>

        <div className="mt-4 flex justify-end gap-4 pr-2">
          <button
            className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 shadow"
            onClick={register}
          >
            행사 등록
          </button>
          <button
            className="bg-gray-400 text-white px-4 py-2 rounded-md hover:bg-gray-500 shadow"
            onClick={moveToReturn}
          >
            뒤로가기
          </button>
        </div>
      </div>

      <div className="w-1/3 pl-10 flex flex-col gap-4 items-start">
        {images.map((img, index) => (
          <div key={index} className="flex flex-col items-start">
            <button className="w-full text-right text-red-500" onClick={() => fileDelete(index)}>x</button>
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
};

export default EvtAddComponent1;
