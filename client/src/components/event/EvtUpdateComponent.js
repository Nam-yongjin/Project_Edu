import { useState, useEffect, useCallback } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { ko } from "date-fns/locale";
import { getEventById, updateEvent } from "../../api/eventApi";
import useMove from "../../hooks/useMove";

const EvtUpdateComponent = ({ eventNum }) => {
  const { moveToPath, moveToReturn } = useMove();

  const [evt, setEvt] = useState(null);
  const [mainImage, setMainImage] = useState(null); // {file,url,name}
  const [imageList, setImageList] = useState([]); // [{file,url,name}]
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

  // 이미지 선택 (첫번째 = 대표, 나머지 = 서브)
  const handleImageChange = (e) => {
    const files = Array.from(e.target.files || []);
    if (!files.length) return;
    const previews = files.map((file) => ({
      file,
      url: URL.createObjectURL(file),
      name: file.name,
    }));
    // 기존 미리보기 URL 정리
    if (mainImage?.url) URL.revokeObjectURL(mainImage.url);
    imageList.forEach((img) => img?.url && URL.revokeObjectURL(img.url));

    setMainImage(previews[0]);
    setImageList(previews.slice(1));
    e.target.value = "";
  };

  // 첨부파일 선택 (첫번째 = 대표, 나머지 = 서브)
  const handleAttachChange = (e) => {
    const files = Array.from(e.target.files || []);
    if (!files.length) return;
    setMainFile(files[0]);
    setAttachFiles(files.slice(1));
    e.target.value = "";
  };

  const deleteMainImage = () => {
    if (mainImage?.url) URL.revokeObjectURL(mainImage.url);
    setMainImage(null);
  };
  const deleteSubImage = (idx) => {
    setImageList((prev) => {
      const next = [...prev];
      const removed = next.splice(idx, 1)[0];
      if (removed?.url) URL.revokeObjectURL(removed.url);
      return next;
    });
  };

  // 언마운트 시 미리보기 URL 해제
  useEffect(
    () => () => {
      if (mainImage?.url) URL.revokeObjectURL(mainImage.url);
      imageList.forEach((img) => img?.url && URL.revokeObjectURL(img.url));
    },
    [mainImage, imageList]
  );

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

  // --- 상단 요약 텍스트 (등록 화면과 톤 맞춤) ---
  const imageCount = (mainImage ? 1 : 0) + imageList.length;
  const imageSummary = imageCount > 0 ? `${imageCount}장 선택됨` : "선택된 파일 없음";

  const fileCount = (mainFile ? 1 : 0) + attachFiles.length;
  const attachSummaryText = fileCount > 0 ? `${fileCount}개 첨부됨` : "선택된 파일 없음";

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
      <div className="min-blank bg-white rounded-2xl page-shadow p-10">
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
            { name: "eventStartPeriod", label: "프로그램 시작" },
            { name: "eventEndPeriod", label: "프로그램 종료" },
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

          {/* 이미지 업로드 (라벨 버튼 + 숨김 input) */}
          <div className="flex items-center gap-4">
            <label className="w-32 newText-base font-semibold">이미지</label>
            <label htmlFor="update-image-input" className="positive-button newText-base cursor-pointer">
              파일 선택
            </label>
            <input
              id="update-image-input"
              type="file"
              multiple
              accept="image/*"
              onChange={handleImageChange}
              className="hidden"
            />
            <span className="newText-sm text-gray-500">{imageSummary}</span>
          </div>

          {/* 첨부파일 업로드 (라벨 버튼 + 숨김 input) */}
          <div className="flex items-center gap-4">
            <label className="w-32 newText-base font-semibold">첨부파일</label>
            <label htmlFor="update-attach-input" className="positive-button newText-base cursor-pointer">
              파일 선택
            </label>
            <input
              id="update-attach-input"
              type="file"
              multiple
              accept=".pdf,.hwp,.doc,.docx"
              onChange={handleAttachChange}
              className="hidden"
            />
            <span className="newText-sm text-gray-500">{attachSummaryText}</span>
          </div>

          {/* 선택된 파일 실제 목록 */}
          <div className="ml-32 grid grid-cols-1 gap-2">
            {/* 이미지 목록은 아래의 미리보기 카드로 대체 */}
            {mainFile && <PreviewTextCard title="대표 첨부파일" name={mainFile.name} />}
            {attachFiles.map((f, i) => (
              <PreviewTextCard key={i} title="기타 첨부파일" name={f.name} />
            ))}
          </div>

          {/* 이미지 미리보기 카드들 */}
          <div className="ml-32 grid sm:grid-cols-3 grid-cols-2 gap-3">
            {mainImage && (
              <PreviewImageCard
                title="대표 이미지"
                name={mainImage.name}
                url={mainImage.url}
                onDelete={deleteMainImage}
              />)
            }
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

          {/* 버튼 영역 */}
          <div className="flex justify-end gap-4 mt-6">
            <button onClick={handleUpdate} disabled={submitting} className="positive-button newText-base">
              {submitting ? "수정 중..." : "프로그램 수정"}
            </button>
            <button onClick={moveToReturn} className="normal-button newText-base">
              뒤로가기
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

const PreviewImageCard = ({ title, name, url, onDelete }) => (
  <div className="page-shadow border rounded-2xl p-3 bg-white">
    <div className="flex justify-between items-center">
      <p className="newText-sm font-semibold text-blue-600">{title}</p>
      <button className="newText-sm nagative-button px-2 py-0.5" onClick={onDelete}>
        삭제
      </button>
    </div>
    <img src={url} alt={title} className="w-32 h-32 object-cover rounded mt-2" />
    <p className="newText-sm break-words mt-1">{name}</p>
  </div>
);

const PreviewTextCard = ({ title, name }) => (
  <div className="page-shadow border rounded-2xl p-3 bg-white">
    <p className="newText-sm font-semibold text-blue-600">{title}</p>
    <p className="newText-sm break-words mt-1">{name}</p>
  </div>
);

export default EvtUpdateComponent;
