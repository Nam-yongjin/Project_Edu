import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useSelector } from "react-redux";
import useMove from "../../hooks/useMove";
import { getFacilityDetail, updateFacility } from "../../api/facilityApi";

const initialForm = { facName: "", facInfo: "", capacity: "", facItem: "", etc: "" };
const HOURS = Array.from({ length: 24 }, (_, i) => String(i).padStart(2, "0"));
const toTimeString = (h) => (h ? `${h}:00` : "");
const hhFrom = (v) => {
  if (!v) return "";
  const m = String(v).match(/(\d{1,2}):(\d{2})/);
  return m ? m[1].padStart(2, "0") : "";
};

const VIEW_HOST = "http://localhost:8090/view";
function normalizeImageUrl(url) {
  if (!url) return "/placeholder.svg";
  if (/^https?:\/\//i.test(url)) return url;
  let path = String(url).replace(/^https?:\/\/[^/]+/i, "");
  path = path.replace(/^\/?view\/?/, "/");
  if (!path.startsWith("/")) path = "/" + path;
  return (VIEW_HOST + path).replace(/([^:]\/)\/+/g, "$1");
}

/** 공통 썸네일 카드 (우측 패널 내부) */
const ThumbTile = React.memo(function ThumbTile({
  src,
  title,
  isMain,
  isExisting,
  marked,
  onToggleDelete,
  onSetMain,
}) {
  return (
    <div className={`rounded-2xl border bg-white p-2 w-[160px] ${isMain ? "ring-2 ring-blue-400" : ""}`}>
      <div className="flex justify-between items-center mb-1">
        <p className="newText-sm font-semibold text-blue-600">
          {isMain ? "대표" : title}
        </p>
        <button
          type="button"
          onClick={onToggleDelete}
          className={`${isExisting ? (marked ? "normal-button" : "negative-button") : "negative-button"} newText-xs px-2 py-0.5 rounded`}
        >
          {isExisting ? (marked ? "취소" : "삭제") : "삭제"}
        </button>
      </div>
      <img src={src} alt={title} className="w-[140px] h-[140px] object-cover rounded" />
      {!isMain && (
        <button type="button" onClick={onSetMain} className="mt-2 positive-button w-full newText-xs px-2 py-1 rounded">
          대표지정
        </button>
      )}
      {isExisting && (
        <p className="newText-xs text-gray-500 mt-1">기존</p>
      )}
    </div>
  );
});

// 서버에서 내려온 배열에서 대표 인덱스 탐색(여러 필드명 케이스 방어)
function getInitialMainIndex(imgs = []) {
  const idx = imgs.findIndex(
    (it) => it?.mainImage === true || it?.isMain === true || it?.main === true
  );
  return idx >= 0 ? idx : 0; // 대표 플래그 없으면 첫 번째
}

export default function FacilityUpdateComponent({ facRevNum: facRevNumProp }) {
  const { facRevNum: facRevNumParam } = useParams();
  const facRevNum = useMemo(
    () => (facRevNumProp != null ? Number(facRevNumProp) : facRevNumParam ? Number(facRevNumParam) : undefined),
    [facRevNumProp, facRevNumParam]
  );

  const navigate = useNavigate();
  const { moveToReturn } = useMove();
  const isAdmin = useSelector((state) => state.loginState?.role === "ADMIN");

  const [form, setForm] = useState(initialForm);
  const [startH, setStartH] = useState("");
  const [endH, setEndH] = useState("");

  // 이미지 상태
  const [existingImages, setExistingImages] = useState([]); // [{facImageNum,imageUrl,imageName,mainImage?}]
  const [removeImageIds, setRemoveImageIds] = useState([]); // 삭제 토글된 기존 이미지 id 목록
  const [newImages, setNewImages] = useState([]); // [{file,url,name}]
  const [mainIndex, setMainIndex] = useState(0); // 우측 통합 리스트에서의 대표 인덱스

  const [errors, setErrors] = useState({});
  const fileInputRef = useRef(null);

  useEffect(() => {
    if (!isAdmin) {
      alert("권한이 없습니다.");
      navigate("/facility/list");
    }
  }, [isAdmin, navigate]);

  // 미리보기 URL 해제
  useEffect(
    () => () => {
      newImages.forEach((s) => s?.url && URL.revokeObjectURL(s.url));
    },
    [newImages]
  );

  // 상세 조회
  useEffect(() => {
    if (!facRevNum) return;
    getFacilityDetail(facRevNum)
      .then((d) => {
        setForm({
          facName: d?.facName || "",
          facInfo: d?.facInfo || "",
          capacity: d?.capacity ?? "",
          facItem: d?.facItem || "",
          etc: d?.etc || "",
        });
        setStartH(hhFrom(d?.reserveStart));
        setEndH(hhFrom(d?.reserveEnd));

        const imgs = Array.isArray(d?.images) ? d.images : [];
        setExistingImages(imgs);
        setRemoveImageIds([]);
        setNewImages([]);
        setErrors({});

        // 서버가 내려준 대표 플래그 기준으로 대표 인덱스 설정
        const initMainIdx = imgs.length ? getInitialMainIndex(imgs) : 0;
        setMainIndex(initMainIdx);
      })
      .catch((err) => {
        console.error(err);
        alert("시설 정보를 불러오지 못했습니다.");
        navigate("/facility/list");
      });
  }, [facRevNum, navigate]);

  // allImages 길이 변화에 따른 mainIndex 보정(범위 밖 방지)
  const allLenRef = useRef(0);
  useEffect(() => {
    if (allLenRef.current === 0) return;
    setMainIndex((idx) => Math.min(idx, allLenRef.current - 1));
  }, [existingImages.length, newImages.length]);

  const onChange = useCallback((e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }, []);

  const onChangeTime = useCallback((which, value) => {
    if (which === "start") setStartH(value);
    else setEndH(value);
  }, []);

  // 신규 이미지 추가
  const onFilePick = useCallback((e) => {
    const files = Array.from(e.target.files || []);
    if (!files.length) return;
    const previews = files.map((file) => ({
      file,
      url: URL.createObjectURL(file),
      name: file.name,
    }));
    setNewImages((prev) => [...prev, ...previews]);
    if (fileInputRef.current) fileInputRef.current.value = "";
  }, []);

  // 신규 이미지 제거
  const deleteNewImage = useCallback((idxNew) => {
    setNewImages((prev) => {
      const next = [...prev];
      const [removed] = next.splice(idxNew, 1);
      if (removed?.url) URL.revokeObjectURL(removed.url);
      return next;
    });
  }, []);

  // 기존 이미지 삭제 토글
  const toggleRemoveExisting = useCallback((id) => {
    setRemoveImageIds((prev) => (prev.includes(id) ? prev.filter((v) => v !== id) : [...prev, id]));
  }, []);

  // 우측 통합 리스트 구성 (기존 + 신규)
  const allImages = useMemo(() => {
    const existing = existingImages.map((img) => ({
      kind: "existing",
      id: img.facImageNum,
      url: normalizeImageUrl(img.imageUrl),
      name: img.imageName || `image-${img.facImageNum}`,
      marked: removeImageIds.includes(img.facImageNum),
    }));
    const newly = newImages.map((img, idx) => ({
      kind: "new",
      tempIdx: idx,
      url: img.url,
      name: img.name,
    }));
    const merged = [...existing, ...newly];
    allLenRef.current = merged.length;
    return merged;
  }, [existingImages, newImages, removeImageIds]);

  // 대표 지정
  const setAsMain = useCallback((idx) => setMainIndex(idx), []);

  // 통합 리스트에서의 삭제/토글 핸들러
  const onTileDelete = useCallback(
    (idx) => {
      const target = allImages[idx];
      if (!target) return;
      const adjustMainAfter = () => {
        if (idx === mainIndex) setMainIndex(0);
        else if (idx < mainIndex) setMainIndex((i) => Math.max(0, i - 1));
      };

      if (target.kind === "existing") {
        toggleRemoveExisting(target.id);
        if (idx === mainIndex && !target.marked) setMainIndex(0);
      } else {
        deleteNewImage(target.tempIdx);
        adjustMainAfter();
      }
    },
    [allImages, deleteNewImage, mainIndex, toggleRemoveExisting]
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
    if (allImages.length === 0) next.images = "이미지를 1장 이상 추가하세요.";
    if (!startH) next.startTime = "시작 시간을 선택하세요.";
    if (!endH) next.endTime = "종료 시간을 선택하세요.";
    if (startH && endH && Number(startH) >= Number(endH)) {
      next.timeRange = "시작 시간은 종료 시간보다 앞서야 합니다.";
    }
    setErrors(next);
    return Object.keys(next).length === 0;
  }, [form, allImages.length, startH, endH]);

  // FormData 구성
  const buildFormData = useCallback(() => {
    const payload = {
      facRevNum,
      facName: form.facName.trim(),
      facInfo: form.facInfo.trim(),
      capacity: Number(form.capacity),
      facItem: form.facItem.trim(),
      etc: form.etc.trim(),
      reserveStart: toTimeString(startH),
      reserveEnd: toTimeString(endH),
      removeImageIds,
    };

    // 대표가 기존 이미지라면 서버가 지원할 경우 dto에 함께 전달
    const main = allImages[mainIndex];
    if (main && main.kind === "existing" && !removeImageIds.includes(main.id)) {
      payload.mainImageId = main.id;
    }

    const fd = new FormData();
    fd.append("dto", new Blob([JSON.stringify(payload)], { type: "application/json" }));

    // addImages: 대표가 신규 이미지라면 그것을 먼저 append
    if (newImages.length) {
      if (main && main.kind === "new") {
        const first = newImages[main.tempIdx];
        if (first) fd.append("addImages", first.file, first.name || first.file.name);
      }
      newImages.forEach((img, i) => {
        if (!(main && main.kind === "new" && i === main.tempIdx)) {
          fd.append("addImages", img.file, img.name || img.file.name);
        }
      });
    }

    return fd;
  }, [facRevNum, form, startH, endH, newImages, allImages, mainIndex, removeImageIds]);

  // 저장
  const handleUpdate = useCallback(async () => {
    if (!validate()) return;
    try {
      if (!facRevNum) {
        alert("잘못된 접근입니다. 식별자가 없습니다.");
        return;
      }
      const formData = buildFormData();
      await updateFacility(formData);
      alert("공간 정보가 수정되었습니다.");
      navigate(`/facility/detail/${facRevNum}`);
    } catch (err) {
      console.error(err);
      const msg = err?.response?.data?.message || err?.message || "서버 오류";
      alert(`수정 실패: ${msg}`);
    }
  }, [buildFormData, facRevNum, navigate, validate]);

  const canSubmit = useMemo(
    () =>
      !!form.facName.trim() &&
      !!form.facInfo.trim() &&
      !!String(form.capacity).trim() &&
      !!startH &&
      !!endH,
    [form, startH, endH]
  );

  return (
    <div className="max-w-screen-xl mx-auto my-10">
      <div className="min-blank">
        <h2 className="newText-3xl font-bold mb-6">공간 수정</h2>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* 좌측: 입력 폼 */}
          <div className="lg:col-span-2 page-shadow rounded-2xl border bg-white p-5">
            <div className="space-y-4">
              {/* 기본 정보 */}
              {[
                { label: "공간명", name: "facName", type: "text" },
                { label: "수용인원", name: "capacity", type: "number" },
              ].map(({ label, name, type }) => (
                <div className="flex items-center gap-3" key={name}>
                  <label className="w-32 newText-base font-semibold">{label}</label>
                  <input
                    type={type}
                    name={name}
                    value={form[name]}
                    onChange={onChange}
                    className={`input-focus newText-base rounded px-3 py-2 border flex-1 ${errors[name] ? "border-red-500" : ""}`}
                    min={name === "capacity" ? 1 : undefined}
                  />
                </div>
              ))}

              {/* 텍스트 영역 */}
              {[
                { label: "소개", name: "facInfo", rows: 4 },
                { label: "구비품목", name: "facItem", rows: 3 },
                { label: "유의사항", name: "etc", rows: 2 },
              ].map(({ label, name, rows }) => (
                <div className="flex items-start gap-3" key={name}>
                  <label className="w-32 newText-base font-semibold pt-2">{label}</label>
                  <textarea
                    name={name}
                    value={form[name]}
                    onChange={onChange}
                    rows={rows}
                    className={`input-focus newText-base rounded px-3 py-2 border flex-1 resize-y ${errors[name] ? "border-red-500" : ""}`}
                  />
                </div>
              ))}

              {/* 예약 가능 시간 */}
              <div className="rounded-2xl border bg-white p-4 mt-4">
                <h3 className="newText-lg font-semibold mb-3">예약 가능 시간</h3>
                <div className="grid grid-cols-12 gap-4 items-end">
                  <div className="col-span-12 md:col-span-6">
                    <label className="newText-sm font-semibold block mb-1">시작 시간</label>
                    <select
                      className={`input-focus newText-base rounded px-3 py-2 border w-full ${errors.startTime || errors.timeRange ? "border-red-500" : ""}`}
                      value={startH}
                      onChange={(e) => onChangeTime("start", e.target.value)}
                    >
                      <option value="">시</option>
                      {HOURS.map((h) => (
                        <option key={h} value={h}>
                          {h}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="col-span-12 md:col-span-6">
                    <label className="newText-sm font-semibold block mb-1">종료 시간</label>
                    <select
                      className={`input-focus newText-base rounded px-3 py-2 border w-full ${errors.endTime || errors.timeRange ? "border-red-500" : ""}`}
                      value={endH}
                      onChange={(e) => onChangeTime("end", e.target.value)}
                    >
                      <option value="">시</option>
                      {HOURS.map((h) => (
                        <option key={h} value={h}>
                          {h}
                        </option>
                      ))}
                    </select>
                  </div>

                  {(errors.startTime || errors.endTime || errors.timeRange) && (
                    <div className="col-span-12 newText-xs text-red-600 mt-1">
                      {errors.startTime && <div>{errors.startTime}</div>}
                      {errors.endTime && <div>{errors.endTime}</div>}
                      {errors.timeRange && <div>{errors.timeRange}</div>}
                    </div>
                  )}
                </div>
              </div>

              {/* 이미지 추가 */}
              <div className="rounded-2xl border bg-white p-4 mt-4">
                <div className="flex items-center gap-3">
                  <label className="w-32 newText-base font-semibold">이미지 추가</label>
                  <input
                    ref={fileInputRef}
                    type="file"
                    accept="image/*"
                    multiple
                    onChange={onFilePick}
                    className="input-focus newText-base rounded px-3 py-2 border flex-1"
                  />
                </div>
                {errors.images && <p className="newText-sm text-red-600 mt-2">{errors.images}</p>}
              </div>

              {/* 버튼 */}
              <div className="mt-6 flex justify-end gap-3">
                <button
                  onClick={handleUpdate}
                  disabled={!canSubmit}
                  className="positive-button newText-base px-4 py-2 rounded disabled:opacity-60"
                  type="button"
                >
                  저장
                </button>
                <button onClick={moveToReturn} className="normal-button newText-base px-4 py-2 rounded" type="button">
                  뒤로가기
                </button>
              </div>
            </div>
          </div>

          {/* 우측: 통합 이미지 미리보기 (대표지정 가능) */}
          <aside className="lg:col-span-1 lg:sticky lg:top-6">
            <section className="page-shadow rounded-2xl border bg-white p-4">
              <h4 className="newText-lg font-semibold mb-3">이미지 미리보기</h4>

              {allImages.length ? (
                <div className="grid grid-cols-2 gap-3">
                  {allImages.map((it, idx) => (
                    <ThumbTile
                      key={`${it.kind}-${it.kind === "existing" ? it.id : it.tempIdx}`}
                      src={it.url}
                      title={`${it.kind === "existing" ? "기존" : "신규"} ${idx + 1}`}
                      isMain={idx === mainIndex}
                      isExisting={it.kind === "existing"}
                      marked={it.kind === "existing" ? it.marked : false}
                      onToggleDelete={() => onTileDelete(idx)}
                      onSetMain={() => setAsMain(idx)}
                    />
                  ))}
                </div>
              ) : (
                <p className="newText-sm text-gray-500">표시할 이미지가 없습니다.</p>
              )}

              {!!removeImageIds.length && (
                <p className="newText-xs text-red-600 mt-3">
                  삭제 예정(기존): {removeImageIds.join(", ")}
                </p>
              )}
            </section>
          </aside>
        </div>
      </div>
    </div>
  );
}