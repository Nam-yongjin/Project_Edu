// FacilityAddComponent.js
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import useMove from "../../hooks/useMove";
import { registerFacility } from "../../api/facilityApi";

const FacilityAddComponent = () => {
  const navigate = useNavigate();
  const { moveToReturn } = useMove();

  const isAdmin = useSelector((state) => state.loginState?.role === "ADMIN");

  useEffect(() => {
    if (!isAdmin) {
      alert("권한이 없습니다.");
      navigate("/facility/list");
    }
  }, [isAdmin, navigate]);

  const [form, setForm] = useState({
    facName: "",
    facInfo: "",
    capacity: "",
    facItem: "",
    etc: "",
    reserveStart: "",
    reserveEnd: "",
  });

  const [mainImage, setMainImage] = useState(null);
  const [subImages, setSubImages] = useState([]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleImageChange = (e) => {
    const files = Array.from(e.target.files);
    if (files.length === 0) return;

    const previewList = files.map((file) => ({
      file,
      url: URL.createObjectURL(file),
      name: file.name,
    }));

    setMainImage(previewList[0]);
    setSubImages(previewList.slice(1));
  };

  const deleteMainImage = () => setMainImage(null);
  const deleteSubImage = (index) =>
    setSubImages((prev) => prev.filter((_, i) => i !== index));

  const buildFormData = () => {
    const formData = new FormData();

    // form object to JSON, then to Blob
    const json = JSON.stringify(form);
    const blob = new Blob([json], { type: "application/json" });
    formData.append("dto", blob);

    // images
    if (mainImage) formData.append("images", mainImage.file);
    subImages.forEach((img) => formData.append("images", img.file));

    return formData;
  };

  const handleRegister = async () => {
    try {
      const formData = buildFormData();
      await registerFacility(formData);
      alert("시설이 등록되었습니다.");
      navigate("/facility/list");
    } catch (err) {
      console.error(err);
      alert("등록 실패: " + (err.response?.data?.message || "서버 오류"));
    }
  };

  return (
    <div className="flex mt-10 max-w-6xl mx-auto">
      {/* 입력 폼 */}
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
                onChange={handleChange}
                className="border p-3 flex-1"
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
                onChange={handleChange}
                rows={rows}
                className="border p-3 flex-1 resize-y"
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
              onChange={handleChange}
              className="w-full border p-2 rounded"
            />
          </div>
          <div>
            <label className="font-semibold">예약 종료 시간</label>
            <input
              type="time"
              name="reserveEnd"
              value={form.reserveEnd}
              onChange={handleChange}
              className="w-full border p-2 rounded"
            />
          </div>
        </div>

        <div className="flex items-center">
          <label className="w-[120px] font-semibold">이미지</label>
          <input
            type="file"
            accept="image/*"
            multiple
            onChange={handleImageChange}
            className="border p-2 flex-1"
          />
        </div>

        <div className="mt-4 flex justify-end gap-4">
          <button
            onClick={handleRegister}
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
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

      {/* 이미지 미리보기 */}
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

const ImagePreview = ({ title, image, onDelete }) => (
  <div className="border rounded p-2 shadow">
    <div className="flex justify-between items-center">
      <p className="text-sm font-semibold text-blue-600">{title}</p>
      <button className="text-red-500 text-sm" onClick={onDelete}>
        삭제
      </button>
    </div>
    <img src={image.url} alt={title} className="w-32 h-32 object-cover rounded" />
    <p className="text-sm break-words">{image.name}</p>
  </div>
);

export default FacilityAddComponent;