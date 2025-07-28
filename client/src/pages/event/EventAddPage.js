import { useState } from "react";
import { ko } from "date-fns/locale";
import EvtTitleComponent from "../../components/event/EvtTitleComponent";
import useMove from "../../hooks/useMove";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

const AddEvtPage = () => {
  const initState = { evtName: "", maxPerson: 0, eventInfo: "", expDate: new Date() };

  const [fileInputs, setFileInputs] = useState([Date.now()]);
  const [images, setImages] = useState([]);
  const [evt, setEvt] = useState({ ...initState });
  const [applyStartDate, setApplyStartDate] = useState(new Date());
  const [applyEndDate, setApplyEndDate] = useState(new Date());
  const [startDate, setStartDate] = useState(new Date());
  const [endDate, setEndDate] = useState(new Date());
  const { moveToPath, moveToReturn } = useMove();

  const handleFileChange = (id, e) => {
    const files = Array.from(e.target.files);
    if (files.length === 0) return;

    const newPreviews = files.map((file) => ({
      file,
      url: URL.createObjectURL(file),
      name: file.name,
    }));

    setImages((prev) => [...prev, ...newPreviews]);

    if (fileInputs[fileInputs.length - 1] === id) {
      setFileInputs((prev) => [...prev, Date.now()]);
    }
  };

  const addEvt = () => {
    const formData = new FormData();
    for (let i = 0; i < images.length; i++) {
      formData.append("imageList", images[i]);
    }
    formData.append("evtName", evt.evtName);
    formData.append("maxPerson", evt.maxPerson);
    formData.append("eventInfo", evt.eventInfo);
    formData.append("applyStartPeriod", evt.applyStartPeriod.toISOString().split("T")[0]);
    formData.append("applyEndPeriod", evt.applyEndPeriod.toISOString().split("T")[0]);
    formData.append("startPeriod", evt.startPeriod.toISOString().split("T")[0]);
    formData.append("endPeriod", evt.endPeriod.toISOString().split("T")[0]);
  };

    const handleChangeEvt = (e) => {
        evt[e.target.name] = e.target.value;
        setEvt({ ...evt });
    };

    const handleApplyStartDate = (date) => {
        setApplyStartDate(date);
        setEvt((prev) => ({ ...prev, applyStartPeriod: date }));
    };

    const handleAppltEndDate = (date) => {
        setApplyEndDate(date);
        setEvt((prev) => ({ ...prev, applyEndPeriod: date }));
    };

    const handleStartDateChange = (date) => {
        setStartDate(date);
        setEvt((prev) => ({ ...prev, startPeriod: date }));
    };

    const handleEndDateChange = (date) => {
        setEndDate(date);
        setEvt((prev) => ({ ...prev, endPeriod: date }));
    };

  return (
    <div className="w-full">
      <EvtTitleComponent title="행사 등록" />

      <div className="flex mt-10 max-w-6xl mx-auto">
        <div className="space-y-6 w-2/3">
          <div className="flex items-center">
            <label className="text-xl font-semibold w-[120px]">행사명:</label>
            <input
              type="text"
              placeholder="행사 이름을 입력해주세요."
              className="border p-3 text-lg flex-1 min-w-0 box-border"
              name="evtName"
              value={evt.evtName}
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
            <label className="text-xl font-semibold w-[120px]">모집 시작 날짜:</label>
            <DatePicker
              className="border p-3 text-lg flex-1 min-w-0 box-border"
              selected={applyStartDate}
              onChange={handleApplyStartDate}
              dateFormat="yyyy-MM-dd"
              placeholderText="날짜를 선택하세요"
              minDate={new Date()}
              name="startPeriod"
              locale={ko}
            />
          </div>
          <div className="flex items-center">
            <label className="text-xl font-semibold w-[120px]">모집 종료 날짜:</label>
            <DatePicker
              className="border p-3 text-lg flex-1 min-w-0 box-border"
              selected={applyEndDate}
              onChange={handleAppltEndDate}
              dateFormat="yyyy-MM-dd"
              placeholderText="날짜를 선택하세요"
              minDate={new Date()}
              name="endPeriod"
              locale={ko}
            />
          </div>
          <div className="flex items-center">
            <label className="text-xl font-semibold w-[120px]">최대 인원:</label>
            <input
              type="text"
              placeholder="최대인원을 입력해주세요."
              className="border p-3 text-lg flex-1 min-w-0 box-border"
              name="maxPerson"
              value={evt.maxPerson}
              onChange={handleChangeEvt}
            />
          </div>
          <div className="flex items-center">
            <label className="text-xl font-semibold w-[120px]">행사 시작 날짜:</label>
            <DatePicker
              className="border p-3 text-lg flex-1 min-w-0 box-border"
              selected={startDate}
              onChange={handleStartDateChange}
              dateFormat="yyyy-MM-dd"
              placeholderText="날짜를 선택하세요"
              minDate={new Date()}
              name="startPeriod"
              locale={ko}
            />
          </div>
          <div className="flex items-center">
            <label className="text-xl font-semibold w-[120px]">행사 종료 날짜:</label>
            <DatePicker
              className="border p-3 text-lg flex-1 min-w-0 box-border"
              selected={endDate}
              onChange={handleEndDateChange}
              dateFormat="yyyy-MM-dd"
              placeholderText="날짜를 선택하세요"
              minDate={new Date()}
              name="endPeriod"
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
                onClick={addEvt}
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
        </div>

        <div className="w-1/3 pl-10 flex flex-col gap-4 items-start">
          {images.map((img, index) => (
            <div key={index} className="flex flex-col items-start">
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
    </div>
  );
};

export default AddEvtPage;
