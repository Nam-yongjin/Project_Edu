// EvtAddComponent.jsx
// 이미지 업로드/미리보기를 "하나"로 통합
// - 사용자는 한 곳에서 여러 이미지를 선택
// - 첫 번째(또는 사용자가 지정한) 이미지를 대표로 사용
// - 오른쪽 카드에서 모든 이미지가 함께 보이고, 각 썸네일에서 '대표지정' / '삭제' 가능

import React, { useEffect, useState, useCallback } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import DatePicker from "react-datepicker";
import { ko } from "date-fns/locale";
import "react-datepicker/dist/react-datepicker.css";

import defaultImage from "../../assets/default.jpg";
import { postAddEvent } from "../../api/eventApi";
import useMove from "../../hooks/useMove";

// 오른쪽 카드 내부의 썸네일 타일
const ImageTile = React.memo(function ImageTile({
  src,
  title,
  isMain,
  onSetMain,
  onDelete,
}) {
  return (
    <div
      className={[
        "rounded-2xl border bg-white p-2 w-[160px]",
        isMain ? "ring-2 ring-blue-400" : "",
      ].join(" ")}
    >
      <div className="flex justify-between items-center mb-1">
        <p className="newText-sm font-semibold text-blue-600">
          {isMain ? "대표" : title}
        </p>
        <button
          type="button"
          onClick={onDelete}
          className="nagative-button newText-xs px-2 py-0.5 rounded"
        >
          삭제
        </button>
      </div>
      <img
        src={src}
        alt={title}
        className="w-[140px] h-[140px] object-cover rounded"
      />
      {!isMain && (
        <button
          type="button"
          onClick={onSetMain}
          className="mt-2 positive-button w-full newText-xs px-2 py-1 rounded"
        >
          대표지정
        </button>
      )}
    </div>
  );
});

const EvtAddComponent = () => {
  const navigate = useNavigate();
  const { moveToPath } = useMove();
  const isAdmin = useSelector((state) => state.loginState?.role === "ADMIN");

  // 권한 체크
  useEffect(() => {
    if (!isAdmin) {
      alert("권한이 없습니다.");
      navigate("/event/list");
    }
  }, [isAdmin, navigate]);

  // 폼 상태
  const initState = {
    eventName: "",
    eventInfo: "",
    place: "",
    category: "USER",
    maxCapacity: 0,
    etc: "",
    applyStartPeriod: new Date(),
    applyEndPeriod: new Date(),
    eventStartPeriod: new Date(),
    eventEndPeriod: new Date(),
  };
  const [evt, setEvt] = useState(initState);

  // 이미지 통합 상태
  // images: [{file, url, name}]
  const [images, setImages] = useState([]);
  const [mainIndex, setMainIndex] = useState(0);

  // 첨부파일 상태
  const [mainFile, setMainFile] = useState(null);
  const [attachFiles, setAttachFiles] = useState([]);

  const [submitting, setSubmitting] = useState(false);

  // 입력 변경
  const handleChange = (e) => {
    const { name, value } = e.target;
    setEvt((prev) => ({ ...prev, [name]: value }));
  };

  // 날짜 변경
  const handleDateChange = (name, date) => {
    setEvt((prev) => ({ ...prev, [name]: date }));
  };

  // yyyy-MM-dd HH:mm
  const formatDateTime = (date) => {
    const yyyy = date.getFullYear();
    const MM = String(date.getMonth() + 1).padStart(2, "0");
    const dd = String(date.getDate()).padStart(2, "0");
    const HH = String(date.getHours()).padStart(2, "0");
    const mm = String(date.getMinutes()).padStart(2, "0");
    return `${yyyy}-${MM}-${dd} ${HH}:${mm}`;
  };

  // 이미지 선택(누적)
  const handleImagesChange = (e) => {
    const files = Array.from(e.target.files || []);
    if (!files.length) return;

    const previews = files.map((file) => ({
      file,
      url: URL.createObjectURL(file),
      name: file.name,
    }));

    setImages((prev) => {
      const next = [...prev, ...previews];
      // 기존 이미지가 없었다면 대표는 0으로 유지
      return next;
    });
    if (images.length === 0) setMainIndex(0);
    // input 재선택 대비
    e.target.value = "";
  };

  // 대표 지정
  const setAsMain = (idx) => {
    setMainIndex(idx);
  };

  // 이미지 삭제
  const removeImage = (idx) => {
    setImages((prev) => {
      const next = [...prev];
      const removed = next.splice(idx, 1)[0];
      if (removed?.url) URL.revokeObjectURL(removed.url);

      // 대표 인덱스 보정
      if (idx === mainIndex) {
        setMainIndex(0);
      } else if (idx < mainIndex) {
        setMainIndex((i) => Math.max(0, i - 1));
      }
      return next;
    });
  };

  // 첨부파일 선택(첫 파일은 대표)
  const handleAttachChange = (e) => {
    const files = Array.from(e.target.files || []);
    if (files.length > 0) {
      setMainFile(files[0]);
      setAttachFiles(files.slice(1));
    }
  };

  // 언마운트 시 미리보기 URL 해제
  useEffect(
    () => () => {
      images.forEach((i) => i?.url && URL.revokeObjectURL(i.url));
    },
    [images]
  );

  // 기본 이미지 파일 생성
  const urlToFile = async (url, name = "default.jpg", mime = "image/jpeg") => {
    const res = await fetch(url);
    const blob = await res.blob();
    return new File([blob], name, { type: mime });
  };

  // 등록
  const register = useCallback(async () => {
    try {
      setSubmitting(true);

      const formData = new FormData();

      // 대표/그 외 구분만 서버 형식에 맞춰 전송
      if (images.length === 0) {
        formData.append("mainImage", await urlToFile(defaultImage));
      } else {
        const main = images[mainIndex];
        formData.append("mainImage", main.file, main.name || main.file.name);
        images.forEach((img, i) => {
          if (i !== mainIndex) {
            formData.append("imageList", img.file, img.name || img.file.name);
          }
        });
      }

      if (mainFile) formData.append("mainFile", mainFile);
      attachFiles.forEach((f) => formData.append("attachList", f));

      const dto = {
        ...evt,
        applyStartPeriod: formatDateTime(evt.applyStartPeriod),
        applyEndPeriod: formatDateTime(evt.applyEndPeriod),
        eventStartPeriod: formatDateTime(evt.eventStartPeriod),
        eventEndPeriod: formatDateTime(evt.eventEndPeriod),
      };
      formData.append("dto", new Blob([JSON.stringify(dto)], { type: "application/json" }));

      await postAddEvent(formData);
      alert("등록 완료");
      moveToPath("/event/list");
    } catch (err) {
      console.error(err);
      alert("등록 실패: " + (err.response?.data?.message || err.message));
    } finally {
      setSubmitting(false);
    }
  }, [attachFiles, evt, images, mainFile, mainIndex, moveToPath]);

  return (
    <div className="max-w-screen-xl mx-auto my-10">
      <div className="min-blank">
        <h2 className="text-center newText-3xl font-bold mb-10">프로그램 등록</h2>

        {/* 좌(폼) / 우(이미지 미리보기 카드) */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* 좌측: 폼 */}
          <div className="lg:col-span-2 page-shadow rounded-2xl border bg-white p-6">
            <div className="grid grid-cols-1 gap-4">
              {/* 텍스트 입력들 */}
              {[
                { label: "프로그램명", name: "eventName", type: "text", required: true, placeholder: "프로그램명 입력" },
                { label: "소개", name: "eventInfo", type: "textarea", required: true, placeholder: "프로그램 소개 입력" },
                { label: "장소", name: "place", type: "text", required: true, placeholder: "장소 입력" },
                { label: "모집 인원", name: "maxCapacity", type: "number", required: true, placeholder: "최대 인원 수" },
                { label: "유의사항", name: "etc", type: "textarea", placeholder: "주의사항 또는 안내사항" },
              ].map(({ label, name, type, required, placeholder }) => (
                <div key={name} className="flex items-start gap-4">
                  <label className="w-32 newText-base font-semibold pt-2">
                    {label} {required && <span className="text-red-500">*</span>}
                  </label>
                  {type === "textarea" ? (
                    <textarea
                      name={name}
                      value={evt[name]}
                      onChange={handleChange}
                      rows={3}
                      placeholder={placeholder}
                      className="input-focus newText-base flex-1 resize-y placeholder-gray-400"
                    />
                  ) : (
                    <input
                      type={type}
                      name={name}
                      value={evt[name]}
                      onChange={handleChange}
                      placeholder={placeholder}
                      className="input-focus newText-base flex-1 placeholder-gray-400"
                    />
                  )}
                </div>
              ))}

              {/* 날짜/시간 */}
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

              {/* 모집 대상 */}
              <div className="flex items-center gap-4">
                <label className="w-32 newText-base font-semibold">모집 대상 *</label>
                <select
                  name="category"
                  value={evt.category}
                  onChange={handleChange}
                  className="input-focus newText-base flex-1 placeholder-gray-400"
                >
                  <option value="USER">일반인</option>
                  <option value="STUDENT">학생</option>
                  <option value="TEACHER">교사</option>
                </select>
              </div>

              {/* 이미지 업로드(통합) */}
              <div className="flex items-center gap-4">
                <label className="w-32 newText-base font-semibold">이미지</label>
                <input type="file" accept="image/*" multiple onChange={handleImagesChange} />
              </div>

              {/* 첨부파일 */}
              <div className="flex items-center gap-4">
                <label className="w-32 newText-base font-semibold">첨부파일</label>
                <input type="file" accept=".pdf,.doc,.hwp" multiple onChange={handleAttachChange} />
              </div>
              <div className="ml-32 space-y-1">
                {mainFile && <p className="newText-sm">대표: {mainFile.name}</p>}
                {attachFiles.map((f, i) => (
                  <p key={i} className="newText-sm">
                    {f.name}
                  </p>
                ))}
              </div>

              {/* 버튼 */}
              <div className="flex justify-end gap-4 mt-6">
                <button
                  onClick={register}
                  disabled={submitting}
                  className="newText-base positive-button"
                >
                  {submitting ? "등록 중..." : "프로그램 등록"}
                </button>
                <button onClick={() => navigate(-1)} className="newText-base normal-button">
                  뒤로가기
                </button>
              </div>
            </div>
          </div>

          {/* 우측: 이미지 미리보기 카드(통합) */}
          <aside className="lg:col-span-1 lg:sticky lg:top-6">
            <section className="page-shadow rounded-2xl border bg-white p-4">
              <h4 className="newText-lg font-semibold mb-2">이미지 미리보기</h4>

              {images.length > 0 ? (
                <div className="grid grid-cols-2 gap-3">
                  {images.map((img, idx) => (
                    <ImageTile
                      key={idx}
                      src={img.url}
                      title={`이미지 ${idx + 1}`}
                      isMain={idx === mainIndex}
                      onSetMain={() => setAsMain(idx)}
                      onDelete={() => removeImage(idx)}
                    />
                  ))}
                </div>
              ) : (
                <p className="newText-sm text-gray-500">추가된 이미지가 없습니다.</p>
              )}
            </section>
          </aside>
        </div>
      </div>
    </div>
  );
};

export default EvtAddComponent;
