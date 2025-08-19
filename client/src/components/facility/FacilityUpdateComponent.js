// FacilityUpdateComponent.jsx
// - 장소 수정 화면
// - 기존 정보 로드, 값 편집, 운영시간(시 단위) 변경
// - 기존 이미지 삭제 체크, 새 이미지 추가 업로드
// - 서버에는 multipart/form-data 로 전달 (dto JSON + addImages 파일들)
// - 주석은 모두 한글

import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useSelector } from "react-redux";
import useMove from "../../hooks/useMove";
import {
  getFacilityDetail, // 상세 조회 API
  updateFacility,    // 수정 요청 API(FormData 인자)
} from "../../api/facilityApi";

// 폼 초기값
const initialForm = {
  facName: "",
  facInfo: "",
  capacity: "",
  facItem: "",
  etc: "",
};

// 시만 선택 가능한 셀렉트 옵션
const HOURS = Array.from({ length: 24 }, (_, i) => String(i).padStart(2, "0"));

// "HH" → "HH:00" 문자열 변환
const toTimeString = (h) => (h ? `${h}:00` : "");

// "HH:mm[:ss]" → "HH" (시만 추출)
const hhFrom = (v) => {
  if (!v) return "";
  const m = String(v).match(/(\d{1,2}):(\d{2})/);
  return m ? m[1].padStart(2, "0") : "";
};

// 서버 이미지 URL 정규화
const VIEW_HOST = "http://localhost:8090/view";
function normalizeImageUrl(url) {
  if (!url) return "/placeholder.svg";
  if (/^https?:\/\//i.test(url)) return url;
  let path = String(url).replace(/^https?:\/\/[^/]+/i, "");
  path = path.replace(/^\/?view\/?/, "/");
  if (!path.startsWith("/")) path = "/" + path;
  return (VIEW_HOST + path).replace(/([^:]\/)\/+/g, "$1");
}

// 신규 업로드 이미지 미리보기 카드
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

// 기존(서버 보관) 이미지 카드
const ExistingImage = React.memo(({ img, marked, onToggle }) => (
  <div className={`border rounded p-2 ${marked ? "ring-2 ring-red-400" : ""}`}>
    <div className="flex justify-between items-center">
      <p className="text-sm font-semibold">기존 이미지 #{img.facImageNum}</p>
      <button
        type="button"
        onClick={() => onToggle(img.facImageNum)}
        className={`text-sm ${marked ? "text-gray-600" : "text-red-600"}`}
      >
        {marked ? "삭제 취소" : "삭제"}
      </button>
    </div>
    <img
      src={normalizeImageUrl(img.imageUrl)}
      alt={img.imageName || `image-${img.facImageNum}`}
      className="w-32 h-32 object-cover rounded mt-1"
    />
    <p className="text-xs text-gray-600 break-words mt-1">{img.imageName}</p>
    {marked && <p className="text-xs text-red-600 mt-1">저장 시 삭제됩니다</p>}
  </div>
));

export default function FacilityUpdateComponent({ facRevNum: facRevNumProp }) {
  // URL 파라미터 또는 상위에서 전달된 prop 사용
  const { facRevNum: facRevNumParam } = useParams();
  const facRevNum = useMemo(
    () => (facRevNumProp != null ? Number(facRevNumProp) : facRevNumParam ? Number(facRevNumParam) : undefined),
    [facRevNumProp, facRevNumParam]
  );

  const navigate = useNavigate();
  const { moveToReturn } = useMove();

  // 관리자 권한 체크
  const isAdmin = useSelector((state) => state.loginState?.role === "ADMIN");

  // 폼 상태
  const [form, setForm] = useState(initialForm);

  // 운영시간(시 단위)
  const [startH, setStartH] = useState("");
  const [endH, setEndH] = useState("");

  // 기존 이미지 목록(서버), 삭제 체크된 ID 목록
  const [existingImages, setExistingImages] = useState([]); // [{facImageNum,imageUrl,imageName}, ...]
  const [removeImageIds, setRemoveImageIds] = useState([]); // [number, ...]

  // 새로 추가할 이미지들
  const [newImages, setNewImages] = useState([]); // [{file,url,name}, ...]

  // 에러 메시지 모음
  const [errors, setErrors] = useState({});

  // 파일 인풋 ref (다중 업로드)
  const fileInputRef = useRef(null);

  // 페이지 진입 시 권한 체크
  useEffect(() => {
    if (!isAdmin) {
      alert("권한이 없습니다.");
      navigate("/facility/list");
    }
  }, [isAdmin, navigate]);

  // 언마운트 시 미리보기 URL 해제
  useEffect(
    () => () => {
      newImages.forEach((s) => s?.url && URL.revokeObjectURL(s.url));
    },
    [newImages]
  );

  // 상세 조회 → 폼 초기화
  useEffect(() => {
    if (!facRevNum) return;
    getFacilityDetail(facRevNum)
      .then((d) => {
        // 기본 필드 주입
        setForm({
          facName: d?.facName || "",
          facInfo: d?.facInfo || "",
          capacity: d?.capacity ?? "",
          facItem: d?.facItem || "",
          etc: d?.etc || "",
        });
        // 시간 초기화(서버는 HH:mm 형태라 가정)
        setStartH(hhFrom(d?.reserveStart));
        setEndH(hhFrom(d?.reserveEnd));
        // 이미지 초기화
        const imgs = Array.isArray(d?.images) ? d.images : [];
        setExistingImages(imgs);
        setRemoveImageIds([]);
        setNewImages([]);
        setErrors({});
      })
      .catch((err) => {
        console.error(err);
        alert("시설 정보를 불러오지 못했습니다.");
        navigate("/facility/list");
      });
  }, [facRevNum, navigate]);

  // 입력 변경
  const onChange = useCallback((e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }, []);

  // 시간 변경(시만)
  const onChangeTime = useCallback((which, value) => {
    if (which === "start") setStartH(value);
    else setEndH(value);
  }, []);

  // 새 이미지 파일 선택
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

  // 새 이미지 삭제(미리보기 제거)
  const deleteNewImage = useCallback((idx) => {
    setNewImages((prev) => {
      const next = [...prev];
      const [removed] = next.splice(idx, 1);
      if (removed?.url) URL.revokeObjectURL(removed.url);
      return next;
    });
  }, []);

  // 기존 이미지 삭제 토글
  const toggleRemoveExisting = useCallback((facImageNum) => {
    setRemoveImageIds((prev) =>
      prev.includes(facImageNum)
        ? prev.filter((id) => id !== facImageNum)
        : [...prev, facImageNum]
    );
  }, []);

  // 유효성 검사
  const validate = useCallback(() => {
    const next = {};
    if (!form.facName.trim()) next.facName = "공간명을 입력하세요.";
    if (!form.facInfo.trim()) next.facInfo = "소개를 입력하세요.";
    if (!String(form.capacity).trim()) next.capacity = "수용인원을 입력하세요.";
    const capNum = Number(form.capacity);
    if (String(form.capacity).trim() && (Number.isNaN(capNum) || capNum <= 0)) {
      next.capacity = "수용인원은 1 이상 숫자입니다.";
    }
    if (!startH) next.startTime = "시작 시간을 선택하세요.";
    if (!endH) next.endTime = "종료 시간을 선택하세요.";
    if (startH && endH && Number(startH) >= Number(endH)) {
      next.timeRange = "시작 시간은 종료 시간보다 앞서야 합니다.";
    }
    setErrors(next);
    return Object.keys(next).length === 0;
  }, [form, startH, endH]);

  // FormData 구성: dto(JSON) + addImages(파일들)
  const buildFormData = useCallback(() => {
    const payload = {
      // 서버 DTO(FacilityUpdateRequestDTO)와 필드명 일치 필요
      facRevNum, // 수정 대상 PK (필수)
      facName: form.facName.trim(),
      facInfo: form.facInfo.trim(),
      capacity: Number(form.capacity),
      facItem: form.facItem.trim(),
      etc: form.etc.trim(),
      reserveStart: toTimeString(startH),
      reserveEnd: toTimeString(endH),
      // 기존 이미지 삭제 목록
      removeImageIds, // List<Long>
    };

    const fd = new FormData();
    fd.append("dto", new Blob([JSON.stringify(payload)], { type: "application/json" }));

    // 신규 추가 이미지
    newImages.forEach((img) => {
      fd.append("addImages", img.file, img.name || img.file.name);
    });

    return fd;
  }, [facRevNum, form, startH, endH, newImages, removeImageIds]);

  // 저장(수정) 핸들러
  const handleUpdate = useCallback(async () => {
    if (!validate()) return;
    try {
      if (!facRevNum) {
        alert("잘못된 접근입니다. 식별자가 없습니다.");
        return;
      }
      const formData = buildFormData();
      await updateFacility(formData); // API 모듈의 FormData 버전 호출
      alert("공간 정보가 수정되었습니다.");
      navigate(`/facility/detail/${facRevNum}`);
    } catch (err) {
      console.error(err);
      const msg = err?.response?.data?.message || err?.message || "서버 오류";
      alert(`수정 실패: ${msg}`);
    }
  }, [buildFormData, facRevNum, navigate, validate]);

  // 제출 가능 여부
  const canSubmit = useMemo(() => {
    return (
      !!form.facName.trim() &&
      !!form.facInfo.trim() &&
      !!String(form.capacity).trim() &&
      !!startH &&
      !!endH
    );
  }, [form, startH, endH]);

  return (
    <div className="flex mt-10 max-w-6xl mx-auto">
      {/* 좌측: 입력 폼 */}
      <div className="space-y-6 w-2/3">
        <h2 className="text-2xl font-bold mb-4">공간 수정</h2>

        {/* 기본 정보 입력 */}
        {[
          { label: "공간명", name: "facName", type: "text" },
          { label: "수용인원", name: "capacity", type: "number" },
        ].map(({ label, name, type }) => (
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
        ))}

        {[
          { label: "소개", name: "facInfo", rows: 4 },
          { label: "구비품목", name: "facItem", rows: 3 },
          { label: "유의사항", name: "etc", rows: 2 },
        ].map(({ label, name, rows }) => (
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
        ))}

        {/* 예약 가능 시간(시 단위) */}
        <div className="border rounded p-3">
          <h3 className="font-semibold mb-3">예약 가능 시간</h3>
          <div className="grid grid-cols-12 gap-3 items-end p-2 rounded bg-gray-50">
            {/* 시작 */}
            <div className="col-span-6">
              <label className="text-sm font-semibold block mb-1">시작 시간</label>
              <select
                className={`border p-2 rounded w-full ${errors.startTime || errors.timeRange ? "border-red-500" : ""}`}
                value={startH}
                onChange={(e) => onChangeTime("start", e.target.value)}
              >
                <option value="">시</option>
                {HOURS.map((h) => (
                  <option key={h} value={h}>{h}</option>
                ))}
              </select>
            </div>
            {/* 종료 */}
            <div className="col-span-6">
              <label className="text-sm font-semibold block mb-1">종료 시간</label>
              <select
                className={`border p-2 rounded w-full ${errors.endTime || errors.timeRange ? "border-red-500" : ""}`}
                value={endH}
                onChange={(e) => onChangeTime("end", e.target.value)}
              >
                <option value="">시</option>
                {HOURS.map((h) => (
                  <option key={h} value={h}>{h}</option>
                ))}
              </select>
            </div>

            {(errors.startTime || errors.endTime || errors.timeRange) && (
              <div className="col-span-12 text-xs text-red-600 mt-2">
                {errors.startTime && <div>{errors.startTime}</div>}
                {errors.endTime && <div>{errors.endTime}</div>}
                {errors.timeRange && <div>{errors.timeRange}</div>}
              </div>
            )}
          </div>
        </div>

        {/* 새 이미지 업로드 */}
        <div className="flex items-center">
          <label className="w-[120px] font-semibold">이미지 추가</label>
          <input
            ref={fileInputRef}
            type="file"
            accept="image/*"
            multiple
            onChange={onFilePick}
            className="border p-2 flex-1"
          />
        </div>

        {/* 버튼 */}
        <div className="mt-4 flex justify-end gap-4">
          <button
            onClick={handleUpdate}
            disabled={!canSubmit}
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-60"
            type="button"
          >
            저장
          </button>
          <button
            onClick={moveToReturn}
            className="bg-gray-400 text-white px-4 py-2 rounded hover:bg-gray-500"
            type="button"
          >
            뒤로가기
          </button>
        </div>
      </div>

      {/* 우측: 이미지 영역(기존/신규) */}
      <div className="w-1/3 pl-10 flex flex-col gap-6">
        {/* 기존 이미지 목록 + 삭제 토글 */}
        <section>
          <h4 className="font-semibold mb-2">기존 이미지</h4>
          {existingImages?.length ? (
            <div className="grid grid-cols-2 gap-3">
              {existingImages.map((img) => (
                <ExistingImage
                  key={img.facImageNum}
                  img={img}
                  marked={removeImageIds.includes(img.facImageNum)}
                  onToggle={toggleRemoveExisting}
                />
              ))}
            </div>
          ) : (
            <p className="text-sm text-gray-500">등록된 이미지가 없습니다.</p>
          )}
          {!!removeImageIds.length && (
            <p className="text-xs text-red-600 mt-2">삭제 예정: {removeImageIds.join(", ")}</p>
          )}
        </section>

        {/* 새 이미지 미리보기 */}
        <section>
          <h4 className="font-semibold mb-2">추가할 이미지</h4>
          {newImages?.length ? (
            <div className="flex flex-col gap-3">
              {newImages.map((img, idx) => (
                <ImagePreview
                  key={idx}
                  title={`새 이미지 ${idx + 1}`}
                  image={img}
                  onDelete={() => deleteNewImage(idx)}
                />
              ))}
            </div>
          ) : (
            <p className="text-sm text-gray-500">추가할 이미지가 없습니다.</p>
          )}
        </section>
      </div>
    </div>
  );
}
