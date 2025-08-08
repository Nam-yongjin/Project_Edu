import { useState, useEffect } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { ko } from "date-fns/locale";
import { getEventById, updateEvent } from "../../api/eventApi";
import useMove from "../../hooks/useMove";

const EvtUpdateComponent = ({ eventNum }) => {
  const { moveToPath, moveToReturn } = useMove();

  const [evt, setEvt] = useState(null);
  const [mainImage, setMainImage] = useState(null);
  const [imageList, setImageList] = useState([]);
  const [mainFile, setMainFile] = useState(null);
  const [attachFiles, setAttachFiles] = useState([]);

  // 이벤트 정보 불러오기
  // eslint-disable-next-line react-hooks/exhaustive-deps
  useEffect(() => {
  const fetchEvent = async () => {
    try {
      const data = await getEventById(eventNum);
      setEvt({
        ...data,
        applyStartPeriod: new Date(data.applyStartPeriod),
        applyEndPeriod: new Date(data.applyEndPeriod),
        eventStartPeriod: new Date(data.eventStartPeriod),
        eventEndPeriod: new Date(data.eventEndPeriod),
        daysOfWeek: data.daysOfWeek || [],
      });
    } catch (err) {
      console.error("행사 조회 실패", err);
      alert("행사 정보를 불러오는 데 실패했습니다.");
      moveToReturn();
    }
  };

  fetchEvent();

  // moveToReturn을 의존성에서 제외
  // eslint-disable-next-line react-hooks/exhaustive-deps
}, [eventNum]);

  const handleChangeEvt = (e) => {
    const { name, value } = e.target;
    setEvt((prev) => ({ ...prev, [name]: value }));
  };

  const handleDateChange = (name, date) => {
    setEvt((prev) => ({ ...prev, [name]: date }));
  };

  const handleImageChange = (e) => {
    const files = Array.from(e.target.files);
    if (!files.length) return;

    const previews = files.map((file) => ({
      file,
      url: URL.createObjectURL(file),
      name: file.name,
    }));

    setMainImage(previews[0]);
    setImageList(previews.slice(1));
  };

  const handleAttachChange = (e) => {
    const files = Array.from(e.target.files);
    if (!files.length) return;

    setMainFile(files[0]);
    setAttachFiles(files.slice(1));
  };

  const deleteMainImage = () => setMainImage(null);
  const deleteSubImage = (index) => {
    setImageList((prev) => prev.filter((_, i) => i !== index));
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

  const handleUpdate = () => {
    const formData = new FormData();
    if (mainImage) formData.append("mainImage", mainImage.file);
    imageList.forEach((img) => formData.append("imageList", img.file));
    if (mainFile) formData.append("mainFile", mainFile);
    attachFiles.forEach((file) => formData.append("attachList", file));

    const dto = {
      ...evt,
      applyStartPeriod: formatDateTime(evt.applyStartPeriod),
      applyEndPeriod: formatDateTime(evt.applyEndPeriod),
      eventStartPeriod: formatDateTime(evt.eventStartPeriod),
      eventEndPeriod: formatDateTime(evt.eventEndPeriod),
    };

    const dtoBlob = new Blob([JSON.stringify(dto)], {
      type: "application/json",
    });
    formData.append("dto", dtoBlob);

    updateEvent(eventNum, formData)
      .then(() => {
        alert("행사 수정이 완료되었습니다.");
        moveToPath("/event/list");
      })
      .catch((err) => {
        console.error("수정 실패", err);
        alert("수정 실패: " + (err.response?.data?.message || err.message));
      });
  };

  if (!evt) return <div className="text-center mt-20">행사 정보를 불러오는 중...</div>;

  return (
    <div className="flex mt-10 max-w-6xl mx-auto">
      <div className="space-y-6 w-2/3">
        {[{ label: "행사명", name: "eventName" }, { label: "장소", name: "place" }].map(({ label, name }) => (
          <div key={name} className="flex items-center">
            <label className="text-xl font-semibold w-[120px]">{label}:</label>
            <input
              type="text"
              name={name}
              value={evt[name] || ""}
              onChange={handleChangeEvt}
              className="border p-3 text-lg flex-1"
            />
          </div>
        ))}

        <div className="flex items-start">
          <label className="text-xl font-semibold w-[120px] pt-3">소개:</label>
          <textarea
            name="eventInfo"
            value={evt.eventInfo || ""}
            onChange={handleChangeEvt}
            rows={5}
            className="border p-3 text-lg flex-1 resize-y"
          />
        </div>

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

        {["applyStartPeriod", "applyEndPeriod", "eventStartPeriod", "eventEndPeriod"].map((name) => (
          <div key={name} className="flex items-center">
            <label className="text-xl font-semibold w-[120px]">
              {name.includes("apply") ? "모집" : "행사"} {name.includes("Start") ? "시작" : "종료"}:
            </label>
            <DatePicker
              selected={evt[name]}
              onChange={(date) => handleDateChange(name, date)}
              showTimeSelect
              dateFormat="yyyy-MM-dd HH:mm"
              timeFormat="HH:mm"
              className="border p-3 text-lg flex-1"
              locale={ko}
              minDate={new Date()}
            />
          </div>
        ))}

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

        <div className="flex items-start">
          <label className="text-xl font-semibold w-[120px] pt-3">유의사항:</label>
          <textarea
            name="etc"
            value={evt.etc || ""}
            onChange={handleChangeEvt}
            rows={3}
            className="border p-3 text-lg flex-1 resize-y"
          />
        </div>

        {[{ label: "이미지", accept: "image/*", onChange: handleImageChange }, { label: "첨부파일", accept: ".pdf,.hwp,.doc,.docx", onChange: handleAttachChange }].map(({ label, accept, onChange }) => (
          <div key={label} className="flex items-center mt-3">
            <label className="text-xl font-semibold w-[120px]">{label}:</label>
            <input
              type="file"
              multiple
              accept={accept}
              onChange={onChange}
              className="border p-2 text-base flex-1"
            />
          </div>
        ))}

        <div className="mt-4 flex justify-end gap-4">
          <button
            onClick={handleUpdate}
            className="bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700"
          >
            행사 수정
          </button>
          <button
            onClick={moveToReturn}
            className="bg-gray-400 text-white px-4 py-2 rounded-md hover:bg-gray-500"
          >
            뒤로가기
          </button>
        </div>
      </div>

      <div className="w-1/3 pl-10 flex flex-col gap-4">
        {mainImage && (
          <PreviewFile title="대표 이미지" name={mainImage.name} url={mainImage.url} onDelete={deleteMainImage} />
        )}
        {imageList.map((img, idx) => (
          <PreviewFile
            key={idx}
            title={`이미지 ${idx + 1}`}
            name={img.name}
            url={img.url}
            onDelete={() => deleteSubImage(idx)}
          />
        ))}
        {mainFile && <PreviewTextFile title="대표 첨부파일" name={mainFile.name} />}
        {attachFiles.map((file, index) => (
          <PreviewTextFile key={index} title="기타 첨부파일" name={file.name} />
        ))}
      </div>
    </div>
  );
};

const PreviewFile = ({ title, name, url, onDelete }) => (
  <div className="border rounded p-2 shadow">
    <div className="flex justify-between items-center">
      <p className="text-sm font-semibold text-blue-600">{title}</p>
      <button className="text-red-500 text-sm" onClick={onDelete}>삭제</button>
    </div>
    <img src={url} alt={title} className="w-32 h-32 object-cover rounded" />
    <p className="text-sm break-words">{name}</p>
  </div>
);

const PreviewTextFile = ({ title, name }) => (
  <div className="border rounded p-2 shadow">
    <p className="text-sm font-semibold text-blue-600">{title}</p>
    <p className="text-sm break-words">{name}</p>
  </div>
);

export default EvtUpdateComponent;