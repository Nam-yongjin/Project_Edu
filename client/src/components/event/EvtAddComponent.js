import React, { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import DatePicker from "react-datepicker";
import { ko } from "date-fns/locale";
import "react-datepicker/dist/react-datepicker.css";

import defaultImage from "../../assets/default.jpg";
import { postAddEvent } from "../../api/eventApi";
import useMove from "../../hooks/useMove";

const EvtAddComponent = () => {
  const navigate = useNavigate();
  const { moveToPath } = useMove();
  const isAdmin = useSelector((state) => state.loginState?.role === "ADMIN");

  useEffect(() => {
    if (!isAdmin) {
      alert("권한이 없습니다.");
      navigate("/event/list");
    }
  }, [isAdmin, navigate]);

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
  const [mainImage, setMainImage] = useState(null);
  const [mainImagePreview, setMainImagePreview] = useState(null);
  const [subImages, setSubImages] = useState([]);
  const [subImagePreviews, setSubImagePreviews] = useState([]);
  const [mainFile, setMainFile] = useState(null);
  const [attachFiles, setAttachFiles] = useState([]);
  const [submitting, setSubmitting] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setEvt((prev) => ({ ...prev, [name]: value }));
  };

  const handleDateChange = (name, date) => {
    setEvt((prev) => ({ ...prev, [name]: date }));
  };

  const formatDateTime = (date) => {
    const yyyy = date.getFullYear();
    const MM = String(date.getMonth() + 1).padStart(2, "0");
    const dd = String(date.getDate()).padStart(2, "0");
    const HH = String(date.getHours()).padStart(2, "0");
    const mm = String(date.getMinutes()).padStart(2, "0");
    return `${yyyy}-${MM}-${dd} ${HH}:${mm}`;
  };

  const handleMainImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setMainImage(file);
      setMainImagePreview(URL.createObjectURL(file));
    }
  };

  const handleSubImagesChange = (e) => {
    const files = Array.from(e.target.files);
    setSubImages((prev) => [...prev, ...files]);
    setSubImagePreviews((prev) => [...prev, ...files.map((f) => URL.createObjectURL(f))]);
  };

  const removeMainImage = () => {
    setMainImage(null);
    setMainImagePreview(null);
  };

  const removeSubImage = (index) => {
    setSubImages((prev) => prev.filter((_, i) => i !== index));
    setSubImagePreviews((prev) => prev.filter((_, i) => i !== index));
  };

  const handleAttachChange = (e) => {
    const files = Array.from(e.target.files || []);
    if (files.length > 0) {
      setMainFile(files[0]);
      setAttachFiles(files.slice(1));
    }
  };

  const urlToFile = async (url, name = "default.jpg", mime = "image/jpeg") => {
    const res = await fetch(url);
    const blob = await res.blob();
    return new File([blob], name, { type: mime });
  };

  const register = async () => {
    try {
      setSubmitting(true);
      const formData = new FormData();

      formData.append("mainImage", mainImage ?? await urlToFile(defaultImage));
      subImages.forEach((img) => formData.append("imageList", img));
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

      await postAddEvent(formData);
      alert("등록 완료");
      moveToPath("/event/list");
    } catch (err) {
      console.error(err);
      alert("등록 실패: " + (err.response?.data?.message || err.message));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="max-w-screen-xl mx-auto my-10">
      <div className="min-blank bg-white rounded-lg shadow page-shadow p-10">
        <h2 className="text-center newText-3xl font-bold mb-10">프로그램 등록</h2>

        <div className="grid grid-cols-1 gap-4">
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

          <div className="flex items-center gap-4">
            <label className="w-32 newText-base font-semibold">대표 이미지</label>
            <input type="file" accept="image/*" onChange={handleMainImageChange} />
          </div>
          {mainImagePreview && (
            <div className="ml-32 w-32 h-32 relative">
              <img src={mainImagePreview} alt="대표" className="rounded object-cover w-full h-full" />
              <button
                onClick={removeMainImage}
                className="absolute top-1 right-1 text-white bg-red-500 rounded-full px-2 text-xs"
              >
                X
              </button>
            </div>
          )}

          <div className="flex items-center gap-4">
            <label className="w-32 newText-base font-semibold">서브 이미지</label>
            <input type="file" multiple accept="image/*" onChange={handleSubImagesChange} />
          </div>
          <div className="ml-32 grid grid-cols-3 gap-2">
            {subImagePreviews.map((src, i) => (
              <div key={i} className="relative w-24 h-24">
                <img src={src} alt={`sub-${i}`} className="rounded object-cover w-full h-full" />
                <button
                  onClick={() => removeSubImage(i)}
                  className="absolute top-1 right-1 text-white bg-red-500 rounded-full px-1 text-xs"
                >
                  X
                </button>
              </div>
            ))}
          </div>

          <div className="flex items-center gap-4">
            <label className="w-32 newText-base font-semibold">첨부파일</label>
            <input type="file" accept=".pdf,.doc,.hwp" multiple onChange={handleAttachChange} />
          </div>
          <div className="ml-32 space-y-1">
            {mainFile && <p className="newText-sm">대표: {mainFile.name}</p>}
            {attachFiles.map((f, i) => (
              <p key={i} className="newText-sm">{f.name}</p>
            ))}
          </div>

          <div className="flex justify-end gap-4 mt-6">
            <button
              onClick={register}
              disabled={submitting}
              className="positive-button"
            >
              {submitting ? "등록 중..." : "프로그램 등록"}
            </button>
            <button onClick={() => navigate(-1)} className="normal-button">
              뒤로가기
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EvtAddComponent;
