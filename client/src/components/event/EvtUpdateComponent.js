import { useState, useEffect } from "react";
import { ko } from "date-fns/locale";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import useMove from "../../hooks/useMove";
import { getEventById, updateEvent } from "../../api/eventApi"; // 실제 API에 맞게 조정하세요

const EvtUpdateComponent = ({ evtNum }) => {
  const initState = {
    evtName: "",
    maxPerson: 0,
    eventInfo: "",
    applyStartPeriod: new Date(),
    applyEndPeriod: new Date(),
    eventStartPeriod: new Date(),
    eventEndPeriod: new Date(),
  };

  const [fileInputs, setFileInputs] = useState([Date.now()]);
  const [images, setImages] = useState([]);
  const [evt, setEvt] = useState({ ...initState });
  const { moveToPath, moveToReturn } = useMove();

  useEffect(() => {
    getEventById(evtNum).then((data) => {
      setEvt({
        ...data,
        applyStartPeriod: new Date(data.applyStartPeriod),
        applyEndPeriod: new Date(data.applyEndPeriod),
        eventStartPeriod: new Date(data.eventStartPeriod),
        eventEndPeriod: new Date(data.eventEndPeriod),
      });
    });
  }, [evtNum]);

  const handleChange = (e) => {
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
    setImages((prevImages) => {
      const removedInputId = prevImages[indexToRemove]?.inputId;
      const newImages = prevImages.filter((_, idx) => idx !== indexToRemove);
      const stillHasInputId = newImages.some((img) => img.inputId === removedInputId);

      setFileInputs((prevInputs) => {
        if (!stillHasInputId) {
          const filtered = prevInputs.filter((id) => id !== removedInputId);
          return filtered.length > 0 ? filtered : [Date.now()];
        }
        return prevInputs;
      });

      return newImages;
    });
  };

  const update = () => {
    const formData = new FormData();
    images.forEach((img) => formData.append("imageList", img.file));

    formData.append("evtName", evt.evtName);
    formData.append("maxPerson", evt.maxPerson);
    formData.append("eventInfo", evt.eventInfo);
    formData.append("applyStartPeriod", evt.applyStartPeriod.toISOString().split("T")[0]);
    formData.append("applyEndPeriod", evt.applyEndPeriod.toISOString().split("T")[0]);
    formData.append("eventStartPeriod", evt.eventStartPeriod.toISOString().split("T")[0]);
    formData.append("eventEndPeriod", evt.eventEndPeriod.toISOString().split("T")[0]);
    formData.append("evtNum", evtNum);

    updateEvent(formData).then(() => {
      alert("행사 수정 완료");
      moveToPath("/event");
    });
  };

  return (
    <div className="flex mt-10 max-w-6xl mx-auto">
      <div className="space-y-6 w-2/3">
        <div className="flex items-center">
          <label className="text-xl font-semibold w-[120px]">행사명:</label>
          <input
            type="text"
            name="evtName"
            value={evt.evtName}
            onChange={handleChange}
            placeholder="행사 이름을 입력해주세요."
            className="border p-3 text-lg flex-1 min-w-0 box-border"
          />
        </div>

        <div className="flex items-start">
          <label className="text-xl font-semibold w-[120px] pt-3">소개:</label>
          <textarea
            rows={5}
            name="eventInfo"
            value={evt.eventInfo}
            onChange={handleChange}
            placeholder="행사 소개를 입력해주세요."
            className="border p-3 text-lg flex-1 resize-y min-w-0 box-border"
          />
        </div>

        {[
          { name: "applyStartPeriod", label: "모집 시작 날짜" },
          { name: "applyEndPeriod", label: "모집 종료 날짜" },
          { name: "eventStartPeriod", label: "행사 시작 날짜" },
          { name: "eventEndPeriod", label: "행사 종료 날짜" },
        ].map(({ name, label }) => (
          <div key={name} className="flex items-center">
            <label className="text-xl font-semibold w-[120px]">{label}:</label>
            <DatePicker
              className="border p-3 text-lg flex-1 min-w-0 box-border"
              selected={evt[name]}
              onChange={(date) => handleDateChange(name, date)}
              dateFormat="yyyy-MM-dd"
              placeholderText="날짜를 선택하세요"
              minDate={new Date()}
              locale={ko}
            />
          </div>
        ))}

        <div className="flex items-center">
          <label className="text-xl font-semibold w-[120px]">최대 인원:</label>
          <input
            type="number"
            name="maxPerson"
            value={evt.maxPerson}
            onChange={handleChange}
            placeholder="최대 인원 입력"
            className="border p-3 text-lg flex-1 min-w-0 box-border"
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

        <div className="mt-4 flex justify-end gap-4 pr-2">
          <button
            className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 shadow"
            onClick={update}
          >
            행사 수정
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

export default EvtUpdateComponent;
