import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import useMove from "../../hooks/useMove";
import { registerFacility } from "../../api/facilityApi";

const initialForm = {
  facName: "",
  facInfo: "",
  capacity: "",
  facItem: "",
  etc: "",
  reserveStart: "",
  reserveEnd: "",
};

const FacilityAddComponent = () => {
  const navigate = useNavigate();
  const { moveToReturn } = useMove();
  const isAdmin = useSelector((state) => state.loginState?.role === "ADMIN");

  const [form, setForm] = useState(initialForm);
  const [mainImage, setMainImage] = useState(null);
  const [subImages, setSubImages] = useState([]);
  const [errors, setErrors] = useState({});
  const fileInputRef = useRef(null);

  useEffect(() => {
    if (!isAdmin) {
      alert("권한이 없습니다.");
      navigate("/facility/list");
    }
  }, [isAdmin, navigate]);

  useEffect(() => () => {
    if (mainImage?.url) URL.revokeObjectURL(mainImage.url);
    subImages.forEach((s) => s.url && URL.revokeObjectURL(s.url));
  }, [mainImage, subImages]);

  const onChange = useCallback((e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }, []);

  const validate = useCallback(() => {
    const next = {};
    if (!form.facName.trim()) next.facName = "시설명을 입력하세요.";
    if (!form.facInfo.trim()) next.facInfo = "소개를 입력하세요.";
    if (!String(form.capacity).trim()) next.capacity = "수용인원을 입력하세요.";
    const capNum = Number(form.capacity);
    if (String(form.capacity).trim() && (Number.isNaN(capNum) || capNum <= 0)) next.capacity = "수용인원은 1 이상 숫자입니다.";
    if (!mainImage) next.images = "대표 이미지를 추가하세요.";
    if (form.reserveStart && form.reserveEnd && form.reserveStart >= form.reserveEnd) {
      next.reserveTime = "예약 시작/종료 시간을 확인하세요.";
    }
    setErrors(next);
    return Object.keys(next).length === 0;
  }, [form, mainImage]);

  const onFilePick = useCallback((e) => {
    const files = Array.from(e.target.files || []);
    if (!files.length) return;

    const previews = files.map((file) => ({ file, url: URL.createObjectURL(file), name: file.name }));

    if (!mainImage) {
      const [first, ...rest] = previews;
      setMainImage(first);
      setSubImages((prev) => [...prev, ...rest]);
    } else {
      setSubImages((prev) => [...prev, ...previews]);
    }

    if (fileInputRef.current) fileInputRef.current.value = "";
  }, [mainImage, subImages]);

  const deleteMainImage = useCallback(() => {
    if (mainImage?.url) URL.revokeObjectURL(mainImage.url);
    setMainImage((prev) => null);
    setSubImages((prev) => {
      if (!prev.length) return [];
      const [first, ...rest] = prev;
      setMainImage(first);
      return rest;
    });
  }, [mainImage]);

  const deleteSubImage = useCallback((idx) => {
    setSubImages((prev) => {
      const next = [...prev];
      const [removed] = next.splice(idx, 1);
      if (removed?.url) URL.revokeObjectURL(removed.url);
      return next;
    });
  }, []);

  const buildFormData = useCallback(() => {
    const payload = {
      facName: form.facName.trim(),
      facInfo: form.facInfo.trim(),
      capacity: Number(form.capacity),
      facItem: form.facItem.trim(),
      etc: form.etc.trim(),
      reserveStart: form.reserveStart || undefined,
      reserveEnd: form.reserveEnd || undefined,
    };

    const fd = new FormData();
    fd.append("dto", new Blob([JSON.stringify(payload)], { type: "application/json" }));

    if (mainImage) fd.append("images", mainImage.file, mainImage.name || mainImage.file.name);
    subImages.forEach((img) => fd.append("images", img.file, img.name || img.file.name));

    return fd;
  }, [form, mainImage, subImages]);

  const handleRegister = useCallback(async () => {
    if (!validate()) return;
    try {
      const formData = buildFormData();
      await registerFacility(formData);
      alert("시설이 등록되었습니다.");
      navigate("/facility/list");
    } catch (err) {
      console.error(err);
      const msg = err?.response?.data?.message || err?.message || "서버 오류";
      alert(`등록 실패: ${msg}`);
    }
  }, [buildFormData, navigate, validate]);

  const canSubmit = useMemo(() => {
    return (
      !!form.facName.trim() &&
      !!form.facInfo.trim() &&
      !!String(form.capacity).trim() &&
      !!mainImage
    );
  }, [form, mainImage]);

  return (
    <div className="flex mt-10 max-w-6xl mx-auto">
      <div className="space-y-6 w-2/3">
        <h2 className="text-2xl font-bold mb-4">시설 등록</h2>

        {[{ label: "시설명", name: "facName", type: "text" }, { label: "수용인원", name: "capacity", type: "number" }].map(
          ({ label, name, type }) => (
            <div className="flex items-center" key={name}>
              <label className="w-[120px] font-semibold">{label}</label>
              <input
                type={type}
                name={name}
                value={form[name]}
                onChange={onChange}
                className={`border p-3 flex-1 ${errors[name] ? "border-red-500" : ""}`}
                min={name === "capacity" ? 1 : undefined}
              />
            </div>
          )
        )}

        {[{ label: "소개", name: "facInfo", rows: 4 }, { label: "구비품목", name: "facItem", rows: 3 }, { label: "유의사항", name: "etc", rows: 2 }].map(
          ({ label, name, rows }) => (
            <div className="flex items-start" key={name}>
              <label className="w-[120px] font-semibold pt-3">{label}</label>
              <textarea
                name={name}
                value={form[name]}
                onChange={onChange}
                rows={rows}
                className={`border p-3 flex-1 resize-y ${errors[name] ? "border-red-500" : ""}`}
              />
            </div>
          )
        )}

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="font-semibold">예약 시작 시간</label>
            <input
              type="time"
              name="reserveStart"
              value={form.reserveStart}
              onChange={onChange}
              className={`w-full border p-2 rounded ${errors.reserveTime ? "border-red-500" : ""}`}
            />
          </div>
          <div>
            <label className="font-semibold">예약 종료 시간</label>
            <input
              type="time"
              name="reserveEnd"
              value={form.reserveEnd}
              onChange={onChange}
              className={`w-full border p-2 rounded ${errors.reserveTime ? "border-red-500" : ""}`}
            />
          </div>
        </div>
        {errors.reserveTime && (
          <p className="text-sm text-red-600">{errors.reserveTime}</p>
        )}

        <div className="flex items-center">
          <label className="w-[120px] font-semibold">이미지</label>
          <input
            ref={fileInputRef}
            type="file"
            accept="image/*"
            multiple
            onChange={onFilePick}
            className={`border p-2 flex-1 ${errors.images ? "border-red-500" : ""}`}
          />
        </div>
        {errors.images && <p className="text-sm text-red-600">{errors.images}</p>}

        <div className="mt-4 flex justify-end gap-4">
          <button
            onClick={handleRegister}
            disabled={!canSubmit}
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-60"
          >
            시설 등록
          </button>
          <button
            onClick={moveToReturn}
            className="bg-gray-400 text-white px-4 py-2 rounded hover:bg-gray-500"
          >
            뒤로가기
          </button>
        </div>
      </div>

      <div className="w-1/3 pl-10 flex flex-col gap-4">
        {mainImage && (
          <ImagePreview
            title="대표 이미지"
            image={mainImage}
            onDelete={deleteMainImage}
          />
        )}

        {subImages.map((img, idx) => (
          <ImagePreview
            key={idx}
            title={`서브 이미지 ${idx + 1}`}
            image={img}
            onDelete={() => deleteSubImage(idx)}
          />
        ))}
      </div>
    </div>
  );
};

const ImagePreview = React.memo(({ title, image, onDelete }) => (
  <div className="border rounded p-2 shadow">
    <div className="flex justify-between items-center">
      <p className="text-sm font-semibold text-blue-600">{title}</p>
      <button className="text-red-500 text-sm" onClick={onDelete} type="button">
        삭제
      </button>
    </div>
    <img src={image.url} alt={title} className="w-32 h-32 object-cover rounded" />
    <p className="text-sm break-words">{image.name}</p>
  </div>
));

export default FacilityAddComponent;