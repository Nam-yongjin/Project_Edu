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
};

// 00~23 시
const HOURS = Array.from({ length: 24 }, (_, i) => String(i).padStart(2, "0"));
const toTimeString = (h) => (h ? `${h}:00` : "");

const FacilityAddComponent = () => {
  const navigate = useNavigate();
  const { moveToReturn } = useMove();
  const isAdmin = useSelector((state) => state.loginState?.role === "ADMIN");

  const [form, setForm] = useState(initialForm);
  const [startH, setStartH] = useState("");
  const [endH, setEndH] = useState("");

  const [mainImage, setMainImage] = useState(null); // {file,url,name}
  const [subImages, setSubImages] = useState([]);   // [{file,url,name}]
  const [errors, setErrors] = useState({});
  const fileInputRef = useRef(null);

  // 권한 체크
  useEffect(() => {
    if (!isAdmin) {
      alert("권한이 없습니다.");
      navigate("/facility/list");
    }
  }, [isAdmin, navigate]);

  // 미리보기 URL 해제
  useEffect(
    () => () => {
      if (mainImage?.url) URL.revokeObjectURL(mainImage.url);
      subImages.forEach((s) => s?.url && URL.revokeObjectURL(s.url));
    },
    [mainImage, subImages]
  );

  const onChange = useCallback((e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }, []);

  const onChangeTime = useCallback((which, value) => {
    if (which === "start") setStartH(value);
    else setEndH(value);
  }, []);

  // 이미지 선택(첫 파일은 대표로, 나머지는 서브)
  const onFilePick = useCallback(
    (e) => {
      const files = Array.from(e.target.files || []);
      if (!files.length) return;

      const previews = files.map((file) => ({
        file,
        url: URL.createObjectURL(file),
        name: file.name,
      }));

      if (!mainImage) {
        const [first, ...rest] = previews;
        setMainImage(first);
        setSubImages((prev) => [...prev, ...rest]);
      } else {
        setSubImages((prev) => [...prev, ...previews]);
      }

      if (fileInputRef.current) fileInputRef.current.value = "";
    },
    [mainImage]
  );

  const deleteMainImage = useCallback(() => {
    if (mainImage?.url) URL.revokeObjectURL(mainImage.url);
    setMainImage(null);
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

  // 검증
  const validate = useCallback(() => {
    const next = {};
    if (!form.facName.trim()) next.facName = "공간명을 입력하세요.";
    if (!form.facInfo.trim()) next.facInfo = "소개를 입력하세요.";
    if (!String(form.capacity).trim()) next.capacity = "수용인원을 입력하세요.";

    const capNum = Number(form.capacity);
    if (String(form.capacity).trim() && (Number.isNaN(capNum) || capNum <= 0)) {
      next.capacity = "수용인원은 1 이상 숫자입니다.";
    }

    if (!mainImage) next.images = "대표 이미지를 추가하세요.";

    if (!startH) next.startTime = "시작 시간을 선택하세요.";
    if (!endH) next.endTime = "종료 시간을 선택하세요.";
    if (startH && endH && Number(startH) >= Number(endH)) {
      next.timeRange = "시작 시간은 종료 시간보다 앞서야 합니다.";
    }

    setErrors(next);
    return Object.keys(next).length === 0;
  }, [form, mainImage, startH, endH]);

  // FormData 구성
  const buildFormData = useCallback(() => {
    const payload = {
      facName: form.facName.trim(),
      facInfo: form.facInfo.trim(),
      capacity: Number(form.capacity),
      facItem: form.facItem.trim(),
      etc: form.etc.trim(),
      reserveStart: toTimeString(startH),
      reserveEnd: toTimeString(endH),
    };

    const fd = new FormData();
    fd.append("dto", new Blob([JSON.stringify(payload)], { type: "application/json" }));

    if (mainImage) {
      fd.append("images", mainImage.file, mainImage.name || mainImage.file.name);
    }
    subImages.forEach((img) =>
      fd.append("images", img.file, img.name || img.file.name)
    );

    return fd;
  }, [form, mainImage, subImages, startH, endH]);

  const handleRegister = useCallback(async () => {
    if (!validate()) return;
    try {
      const formData = buildFormData();
      await registerFacility(formData);
      alert("공간이 등록되었습니다.");
      navigate("/facility/list");
    } catch (err) {
      console.error(err);
      const msg = err?.response?.data?.message || err?.message || "서버 오류";
      alert(`등록 실패: ${msg}`);
    }
  }, [buildFormData, navigate, validate]);

  const canSubmit = useMemo(
    () =>
      !!form.facName.trim() &&
      !!form.facInfo.trim() &&
      !!String(form.capacity).trim() &&
      !!mainImage &&
      !!startH &&
      !!endH,
    [form, mainImage, startH, endH]
  );

  return (
    <div className="max-w-screen-xl mx-auto my-10">
      <div className="min-blank bg-white rounded-lg shadow page-shadow p-10">
        <h2 className="text-center newText-3xl font-bold mb-10">공간 등록</h2>

        {/* 폼 본문 */}
        <div className="grid grid-cols-1 gap-4">
          {/* 기본 정보 */}
          {[
            { label: "공간명", name: "facName", type: "text", required: true, placeholder: "공간명을 입력하세요" },
            { label: "수용인원", name: "capacity", type: "number", required: true, placeholder: "최대 수용 인원(숫자)" },
          ].map(({ label, name, type, required, placeholder }) => (
            <div className="flex items-start gap-4" key={name}>
              <label className="w-32 newText-base font-semibold pt-2">
                {label} {required && <span className="text-red-500">*</span>}
              </label>
              <div className="flex-1">
                <input
                  type={type}
                  name={name}
                  value={form[name]}
                  onChange={onChange}
                  min={name === "capacity" ? 1 : undefined}
                  placeholder={placeholder}
                  className={`input-focus newText-base w-full placeholder-gray-400 ${
                    errors[name] ? "border-red-500" : ""
                  }`}
                />
                {errors[name] && <p className="newText-sm text-red-600 mt-1">{errors[name]}</p>}
              </div>
            </div>
          ))}

          {/* 텍스트 영역 */}
          {[
            { label: "소개", name: "facInfo", rows: 4, required: true, placeholder: "공간 소개를 입력하세요" },
            { label: "구비품목", name: "facItem", rows: 3, placeholder: "예: 빔프로젝터, 화이트보드" },
            { label: "유의사항", name: "etc", rows: 2, placeholder: "예약 전 참고할 점" },
          ].map(({ label, name, rows, required, placeholder }) => (
            <div className="flex items-start gap-4" key={name}>
              <label className="w-32 newText-base font-semibold pt-2">
                {label} {required && <span className="text-red-500">*</span>}
              </label>
              <div className="flex-1">
                <textarea
                  name={name}
                  value={form[name]}
                  onChange={onChange}
                  rows={rows}
                  placeholder={placeholder}
                  className={`input-focus newText-base w-full resize-y placeholder-gray-400 ${
                    errors[name] ? "border-red-500" : ""
                  }`}
                />
                {errors[name] && <p className="newText-sm text-red-600 mt-1">{errors[name]}</p>}
              </div>
            </div>
          ))}

          {/* 예약 가능 시간 (시 단위) */}
          <div className="flex items-start gap-4">
            <label className="w-32 newText-base font-semibold pt-2">예약 가능 시간</label>
            <div className="flex-1 border rounded p-4 bg-gray-50">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <span className="newText-sm font-semibold block mb-1">시작 시간</span>
                  <select
                    className={`input-focus newText-base w-full ${errors.startTime || errors.timeRange ? "border-red-500" : ""}`}
                    value={startH}
                    onChange={(e) => onChangeTime("start", e.target.value)}
                  >
                    <option value="">시 선택</option>
                    {HOURS.map((h) => (
                      <option key={h} value={h}>{h}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <span className="newText-sm font-semibold block mb-1">종료 시간</span>
                  <select
                    className={`input-focus newText-base w-full ${errors.endTime || errors.timeRange ? "border-red-500" : ""}`}
                    value={endH}
                    onChange={(e) => onChangeTime("end", e.target.value)}
                  >
                    <option value="">시 선택</option>
                    {HOURS.map((h) => (
                      <option key={h} value={h}>{h}</option>
                    ))}
                  </select>
                </div>
              </div>

              {(errors.startTime || errors.endTime || errors.timeRange) && (
                <div className="newText-sm text-red-600 mt-3 space-y-1">
                  {errors.startTime && <div>{errors.startTime}</div>}
                  {errors.endTime && <div>{errors.endTime}</div>}
                  {errors.timeRange && <div>{errors.timeRange}</div>}
                </div>
              )}
            </div>
          </div>

          {/* 이미지 업로드 */}
          <div className="flex items-center gap-4">
            <label className="w-32 newText-base font-semibold">이미지</label>
            <div className="flex-1">
              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                multiple
                onChange={onFilePick}
                className={`input-focus newText-base w-full ${errors.images ? "border-red-500" : ""}`}
              />
              <p className="newText-sm text-gray-500 mt-1">첫 번째 선택한 이미지가 대표 이미지로 사용됩니다.</p>
              {errors.images && <p className="newText-sm text-red-600 mt-1">{errors.images}</p>}
            </div>
          </div>

          {/* 대표/서브 이미지 미리보기 */}
          {(mainImage || subImages.length > 0) && (
            <>
              {mainImage && (
                <div className="ml-32">
                  <PreviewImageCard title="대표 이미지" image={mainImage} onDelete={deleteMainImage} />
                </div>
              )}
              {subImages.length > 0 && (
                <div className="ml-32 grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
                  {subImages.map((img, idx) => (
                    <PreviewImageCard
                      key={idx}
                      title={`서브 이미지 ${idx + 1}`}
                      image={img}
                      onDelete={() => deleteSubImage(idx)}
                      compact
                    />
                  ))}
                </div>
              )}
            </>
          )}

          {/* 버튼 영역 */}
          <div className="flex justify-end gap-4 mt-6">
            <button
              onClick={handleRegister}
              disabled={!canSubmit}
              className={`positive-button ${!canSubmit ? "opacity-60 cursor-not-allowed" : ""}`}
              type="button"
            >
              공간 등록
            </button>
            <button onClick={moveToReturn} className="normal-button" type="button">
              뒤로가기
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

const PreviewImageCard = React.memo(({ title, image, onDelete, compact }) => (
  <div className="page-shadow border rounded p-2 bg-white w-fit">
    <div className="flex justify-between items-center mb-1">
      <p className="newText-sm font-semibold text-blue-600">{title}</p>
      <button className="nagative-button newText-sm px-2 py-0.5" onClick={onDelete} type="button">
        삭제
      </button>
    </div>
    <img
      src={image.url}
      alt={title}
      className={`${compact ? "w-28 h-28" : "w-32 h-32"} object-cover rounded`}
    />
    <p className="newText-sm break-words mt-1 max-w-[10rem]">{image.name}</p>
  </div>
));

export default FacilityAddComponent;
