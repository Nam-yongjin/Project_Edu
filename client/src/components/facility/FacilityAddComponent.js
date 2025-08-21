import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import useMove from "../../hooks/useMove";
import { registerFacility } from "../../api/facilityApi";

// 폼 초기값
const initialForm = { facName: "", facInfo: "", capacity: "", facItem: "", etc: "" };

// 00~23 시 옵션
const HOURS = Array.from({ length: 24 }, (_, i) => String(i).padStart(2, "0"));

// "HH" → "HH:00"
const toTimeString = (h) => (h ? `${h}:00` : "");

// 오른쪽 카드 내부에서 쓰는 썸네일 타일
const ImageTile = React.memo(function ImageTile({ src, title, isMain, onSetMain, onDelete }) {
  return (
    <div className={`rounded-2xl border bg-white p-2 w-[160px] ${isMain ? "ring-2 ring-blue-400" : ""}`}>
      <div className="flex justify-between items-center mb-1">
        <p className="newText-sm font-semibold text-blue-600">{isMain ? "대표" : title}</p>
        <button type="button" onClick={onDelete} className="nagative-button newText-xs px-2 py-0.5 rounded">
          삭제
        </button>
      </div>
      <img src={src} alt={title} className="w-[140px] h-[140px] object-cover rounded" />
      {!isMain && (
        <button type="button" onClick={onSetMain} className="mt-2 positive-button w-full newText-xs px-2 py-1 rounded">
          대표지정
        </button>
      )}
    </div>
  );
});

export default function FacilityAddComponent() {
  const navigate = useNavigate();
  const { moveToReturn } = useMove();
  const isAdmin = useSelector((s) => s.loginState?.role === "ADMIN");

  // 폼 상태
  const [form, setForm] = useState(initialForm);
  const [startH, setStartH] = useState("");
  const [endH, setEndH] = useState("");

  // 이미지 통합 상태: images = [{ file, url, name }]
  const [images, setImages] = useState([]);
  const [mainIndex, setMainIndex] = useState(0);

  // 에러 상태
  const [errors, setErrors] = useState({});

  // 파일 인풋 ref
  const fileInputRef = useRef(null);

  // 권한 체크
  useEffect(() => {
    if (!isAdmin) {
      alert("권한이 없습니다.");
      navigate("/facility/list");
    }
  }, [isAdmin, navigate]);

  // 언마운트 시 미리보기 URL 해제
  useEffect(
    () => () => {
      images.forEach((i) => i?.url && URL.revokeObjectURL(i.url));
    },
    [images]
  );

  // 입력 변경
  const onChange = useCallback((e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }, []);

  // 시간 변경
  const onChangeTime = useCallback((which, value) => {
    if (which === "start") setStartH(value);
    else setEndH(value);
  }, []);

  // 파일 선택(누적) → 이미지 배열에 추가, 첫 추가 시 대표 인덱스 0
  const onFilePick = useCallback(
    (e) => {
      const files = Array.from(e.target.files || []);
      if (!files.length) return;

      const previews = files.map((file) => ({
        file,
        url: URL.createObjectURL(file),
        name: file.name,
      }));

      setImages((prev) => [...prev, ...previews]);
      if (images.length === 0) setMainIndex(0);

      if (fileInputRef.current) fileInputRef.current.value = "";
    },
    [images.length]
  );

  // 대표 지정
  const setAsMain = useCallback((idx) => setMainIndex(idx), []);

  // 이미지 삭제(대표 인덱스 보정)
  const deleteImage = useCallback(
    (idx) => {
      setImages((prev) => {
        const next = [...prev];
        const removed = next.splice(idx, 1)[0];
        if (removed?.url) URL.revokeObjectURL(removed.url);

        if (idx === mainIndex) {
          setMainIndex(0);
        } else if (idx < mainIndex) {
          setMainIndex((i) => Math.max(0, i - 1));
        }
        return next;
      });
    },
    [mainIndex]
  );

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
    if (images.length === 0) next.images = "이미지를 1장 이상 추가하세요.";
    if (!startH) next.startTime = "시작 시간을 선택하세요.";
    if (!endH) next.endTime = "종료 시간을 선택하세요.";
    if (startH && endH && Number(startH) >= Number(endH)) {
      next.timeRange = "시작 시간은 종료 시간보다 앞서야 합니다.";
    }
    setErrors(next);
    return Object.keys(next).length === 0;
  }, [form, images.length, startH, endH]);

  // FormData 구성: 대표 이미지를 먼저 append, 나머지 뒤에 append
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

    if (images.length > 0) {
      const main = images[mainIndex];
      fd.append("images", main.file, main.name || main.file.name);
      images.forEach((img, i) => {
        if (i !== mainIndex) {
          fd.append("images", img.file, img.name || img.file.name);
        }
      });
    }

    return fd;
  }, [form, startH, endH, images, mainIndex]);

  // 등록
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

  // 제출 가능 여부
  const canSubmit = useMemo(
    () =>
      !!form.facName.trim() &&
      !!form.facInfo.trim() &&
      !!String(form.capacity).trim() &&
      images.length > 0 &&
      !!startH &&
      !!endH,
    [form, images.length, startH, endH]
  );

  return (
    // 최상단 레이아웃: 고정 클래스 사용
    <div className="max-w-screen-xl mx-auto my-10">
      {/* 좌우 여백: 고정 클래스 사용 */}
      <div className="min-blank">
        <h2 className="newText-3xl font-bold mb-8 text-center">공간 등록</h2>

        {/* 좌(폼) / 우(이미지 미리보기 카드) */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* 좌측 폼 */}
          <div className="lg:col-span-2 page-shadow rounded-2xl border bg-white p-6">
            <div className="space-y-5">
              {/* 기본 정보 */}
              {[
                { label: "공간명", name: "facName", type: "text", required: true, placeholder: "공간명을 입력하세요" },
                { label: "수용인원", name: "capacity", type: "number", required: true, placeholder: "최대 수용 인원(숫자)" },
              ].map(({ label, name, type, required, placeholder }) => (
                <div className="flex items-start gap-4" key={name}>
                  <label className="w-32 newText-base font-semibold pt-2">
                    {label} {required && <span className="newText-base text-red-500">*</span>}
                  </label>
                  <div className="flex-1">
                    <input
                      type={type}
                      name={name}
                      value={form[name]}
                      onChange={onChange}
                      min={name === "capacity" ? 1 : undefined}
                      placeholder={placeholder}
                      className={`input-focus newText-base w-full placeholder-gray-400 ${errors[name] ? "border-red-500" : ""}`}
                    />
                    {errors[name] && <p className="newText-sm text-red-600 mt-1">{errors[name]}</p>}
                  </div>
                </div>
              ))}

              {/* 텍스트 영역 */}
              {[
                { label: "소개", name: "facInfo,textarea", rows: 4, required: true, placeholder: "공간 소개를 입력하세요" },
                { label: "구비품목", name: "facItem,textarea", rows: 3, placeholder: "예: 빔프로젝터, 화이트보드" },
                { label: "유의사항", name: "etc,textarea", rows: 2, placeholder: "예약 전 참고할 점" },
              ].map(({ label, name, rows, required, placeholder }) => {
                const [field, kind] = name.split(",");
                return (
                  <div className="flex items-start gap-4" key={field}>
                    <label className="w-32 newText-base font-semibold pt-2">
                      {label} {required && <span className="newText-base text-red-500">*</span>}
                    </label>
                    <div className="flex-1">
                      {kind === "textarea" ? (
                        <textarea
                          name={field}
                          value={form[field]}
                          onChange={onChange}
                          rows={rows}
                          placeholder={placeholder}
                          className={`input-focus newText-base w-full resize-y placeholder-gray-400 ${errors[field] ? "border-red-500" : ""}`}
                        />
                      ) : (
                        <input
                          name={field}
                          value={form[field]}
                          onChange={onChange}
                          placeholder={placeholder}
                          className={`input-focus newText-base w-full placeholder-gray-400 ${errors[field] ? "border-red-500" : ""}`}
                        />
                      )}
                      {errors[field] && <p className="newText-sm text-red-600 mt-1">{errors[field]}</p>}
                    </div>
                  </div>
                );
              })}

              {/* 예약 가능 시간 */}
              <div className="flex items-start gap-4">
                <label className="w-32 newText-base font-semibold pt-2">예약 가능 시간</label>
                <div className="flex-1 rounded-2xl border bg-white p-4">
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
                          <option key={h} value={h} className="newText-base">
                            {h}
                          </option>
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
                          <option key={h} value={h} className="newText-base">
                            {h}
                          </option>
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

              {/* 이미지 업로드(통합) */}
              <div className="flex items-center gap-4">
                <label className="w-32 newText-base font-semibold">이미지</label>
                <div className="flex-1 rounded-2xl border bg-white p-4">
                  <input
                    ref={fileInputRef}
                    type="file"
                    accept="image/*"
                    multiple
                    onChange={onFilePick}
                    className={`input-focus newText-base w-full ${errors.images ? "border-red-500" : ""}`}
                  />
                  <p className="newText-sm text-gray-500 mt-1">
                    대표로 사용할 이미지는 우측 미리보기에서 '대표지정'을 눌러 설정하세요.
                  </p>
                  {errors.images && <p className="newText-sm text-red-600 mt-1">{errors.images}</p>}
                </div>
              </div>

              {/* 버튼 */}
              <div className="flex justify-end gap-3">
                <button
                  onClick={handleRegister}
                  disabled={!canSubmit}
                  className={`positive-button newText-base px-4 py-2 rounded ${!canSubmit ? "opacity-60 cursor-not-allowed" : ""}`}
                  type="button"
                >
                  공간 등록
                </button>
                <button onClick={moveToReturn} className="normal-button newText-base px-4 py-2 rounded" type="button">
                  뒤로가기
                </button>
              </div>
            </div>
          </div>

          {/* 우측: 이미지 미리보기(통합 + 대표지정 가능) */}
          <aside className="lg:col-span-1 lg:sticky lg:top-6">
            <section className="page-shadow rounded-2xl border bg-white p-4">
              <h4 className="newText-lg font-semibold mb-2">이미지 미리보기</h4>
              {images.length > 0 ? (
                <div className="grid grid-cols-2 gap-3">
                  {images.map((img, idx) => (
                    <ImageTile
                      key={`${img.name}-${idx}`}
                      src={img.url}
                      title={`이미지 ${idx + 1}`}
                      isMain={idx === mainIndex}
                      onSetMain={() => setAsMain(idx)}
                      onDelete={() => deleteImage(idx)}
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
}