import { useState, useEffect } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { ko } from "date-fns/locale";
import { getEventById, updateEvent } from "../../api/eventApi";
import useMove from "../../hooks/useMove";

const EvtUpdateComponent = ({ eventNum }) => {
  const { moveToPath, moveToReturn } = useMove();

  const [evt, setEvt] = useState(null);
  const [mainImage, setMainImage] = useState(null);            // {file,url,name}
  const [imageList, setImageList] = useState([]);               // [{file,url,name}]
  const [mainFile, setMainFile] = useState(null);
  const [attachFiles, setAttachFiles] = useState([]);
  const [submitting, setSubmitting] = useState(false);

  // 이벤트 정보 불러오기
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
        console.error("❌ 프로그램 조회 실패", err);
        alert("프로그램 정보를 불러오는 데 실패했습니다.");
        moveToReturn();
      }
    };
    fetchEvent();
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
    const files = Array.from(e.target.files || []);
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
    const files = Array.from(e.target.files || []);
    if (!files.length) return;
    setMainFile(files[0]);
    setAttachFiles(files.slice(1));
  };

  const deleteMainImage = () => setMainImage(null);
  const deleteSubImage = (idx) =>
    setImageList((prev) => prev.filter((_, i) => i !== idx));

  const formatDateTime = (date) => {
    if (!(date instanceof Date) || isNaN(date)) return "";
    const yyyy = date.getFullYear();
    const MM = String(date.getMonth() + 1).padStart(2, "0");
    const dd = String(date.getDate()).padStart(2, "0");
    const HH = String(date.getHours()).padStart(2, "0");
    const mm = String(date.getMinutes()).padStart(2, "0");
    return `${yyyy}-${MM}-${dd} ${HH}:${mm}`;
  };

  const handleUpdate = async () => {
    try {
      setSubmitting(true);
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
      formData.append("dto", new Blob([JSON.stringify(dto)], { type: "application/json" }));

      await updateEvent(eventNum, formData);
      alert("프로그램 수정이 완료되었습니다.");
      moveToPath("/event/list");
    } catch (err) {
      console.error("수정 실패", err);
      alert("수정 실패: " + (err.response?.data?.message || err.message));
    } finally {
      setSubmitting(false);
    }
  };

  if (!evt) {
    return (
      <div className="max-w-screen-lg mx-auto my-10">
        <div className="min-blank page-shadow bg-white rounded-lg p-10 text-center newText-base">
          프로그램 정보를 불러오는 중...
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-screen-lg mx-auto my-10">
      <div className="min-blank bg-white rounded-lg shadow page-shadow p-10">
        <h2 className="text-center newText-3xl font-bold mb-10">프로그램 수정</h2>

        <div className="grid grid-cols-1 gap-4">
          {/* 기본 텍스트/숫자/텍스트에어리어 */}
          {[
            { label: "프로그램명", name: "eventName", type: "text", required: true, placeholder: "프로그램명 입력" },
            { label: "소개", name: "eventInfo", type: "textarea", required: true, placeholder: "프로그램 소개 입력" },
            { label: "장소", name: "place", type: "text", required: true, placeholder: "장소 입력" },
            { label: "모집 인원", name: "maxCapacity", type: "number", required: true, placeholder: "최대 인원 수" },
            { label: "유의사항", name: "etc", type: "textarea", required: false, placeholder: "주의사항 또는 안내사항" },
          ].map(({ label, name, type, required, placeholder }) => (
            <div key={name} className="flex items-start gap-4">
              <label className="w-32 newText-base font-semibold pt-2">
                {label} {required && <span className="text-red-500">*</span>}
              </label>

              {type === "textarea" ? (
                <textarea
                  name={name}
                  value={evt[name] ?? ""}
                  onChange={handleChangeEvt}
                  rows={name === "eventInfo" ? 4 : 3}
                  placeholder={placeholder}
                  className="input-focus newText-base flex-1 resize-y placeholder-gray-400"
                />
              ) : (
                <input
                  type={type}
                  name={name}
                  value={evt[name] ?? ""}
                  onChange={handleChangeEvt}
                  placeholder={placeholder}
                  className="input-focus newText-base flex-1 placeholder-gray-400"
                />
              )}
            </div>
          ))}

          {/* 모집 대상 */}
          <div className="flex items-center gap-4">
            <label className="w-32 newText-base font-semibold">모집 대상 *</label>
            <select
              name="category"
              value={evt.category}
              onChange={handleChangeEvt}
              className="input-focus newText-base flex-1"
            >
              <option value="USER">일반인</option>
              <option value="STUDENT">학생</option>
              <option value="TEACHER">교사</option>
            </select>
          </div>

          {/* 날짜 필드 */}
          {[
            { name: "applyStartPeriod", label: "모집 시작일" },
            { name: "applyEndPeriod", label: "모집 종료일" },
            { name: "eventStartPeriod", label: "행사 시작일" },
            { name: "eventEndPeriod", label: "행사 종료일" },
          ].map(({ name, label }) => (
            <div key={name} className="flex items-center gap-4">
              <label className="w-32 newText-base font-semibold">{label} *</label>
              <DatePicker
                selected={evt[name]}
                onChange={(date) => handleDateChange(name, date)}
                showTimeSelect
                dateFormat="yyyy-MM-dd HH:mm"
                timeFormat="HH:mm"
                locale={ko}
                minDate={new Date()}
                className="input-focus newText-base flex-1 placeholder-gray-400"
                placeholderText={`${label} 선택`}
              />
            </div>
          ))}
          
          {/* 파일 업로드 */}
          <div className="flex items-center gap-4">
            <label className="w-32 newText-base font-semibold">이미지</label>
            <input type="file" multiple accept="image/*" onChange={handleImageChange} />
          </div>

          <div className="flex items-center gap-4">
            <label className="w-32 newText-base font-semibold">첨부파일</label>
            <input type="file" multiple accept=".pdf,.hwp,.doc,.docx" onChange={handleAttachChange} />
          </div>

          {/* 미리보기 (오른쪽 영역과 같은 느낌으로 아래에 배치하거나, 필요 시 별도 컬럼으로 이동 가능) */}
          <div className="ml-32 grid sm:grid-cols-3 grid-cols-2 gap-3">
            {mainImage && (
              <PreviewImageCard
                title="대표 이미지"
                name={mainImage.name}
                url={mainImage.url}
                onDelete={deleteMainImage}
              />
            )}
            {imageList.map((img, idx) => (
              <PreviewImageCard
                key={idx}
                title={`이미지 ${idx + 1}`}
                name={img.name}
                url={img.url}
                onDelete={() => deleteSubImage(idx)}
              />
            ))}
          </div>

          <div className="ml-32 grid grid-cols-1 gap-2">
            {mainFile && <PreviewTextCard title="대표 첨부파일" name={mainFile.name} />}
            {attachFiles.map((f, i) => (
              <PreviewTextCard key={i} title="기타 첨부파일" name={f.name} />
            ))}
          </div>

          {/* 버튼 영역 */}
          <div className="flex justify-end gap-4 mt-6">
            <button onClick={handleUpdate} disabled={submitting} className="newText-base green-button">
              {submitting ? "수정 중..." : "프로그램 수정"}
            </button>
            <button onClick={moveToReturn} className="newText-base normal-button">
              뒤로가기
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

const PreviewImageCard = ({ title, name, url, onDelete }) => (
  <div className="page-shadow border rounded p-2 bg-white">
    <div className="flex justify-between items-center">
      <p className="newText-sm font-semibold text-blue-600">{title}</p>
      <button className="newText-sm nagative-button px-2 py-0.5" onClick={onDelete}>
        삭제
      </button>
    </div>
    <img src={url} alt={title} className="w-32 h-32 object-cover rounded mt-1" />
    <p className="newText-sm break-words mt-1">{name}</p>
  </div>
);

const PreviewTextCard = ({ title, name }) => (
  <div className="page-shadow border rounded p-2 bg-white">
    <p className="newText-sm font-semibold text-blue-600">{title}</p>
    <p className="newText-sm break-words mt-1">{name}</p>
  </div>
);

export default EvtUpdateComponent;
