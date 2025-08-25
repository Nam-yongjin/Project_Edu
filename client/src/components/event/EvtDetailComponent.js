import React, { useEffect, useState, useCallback } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { getEventById, deleteEvent, applyEvent } from "../../api/eventApi";

const HOST = "http://localhost:8090/view";
const API_HOST = "http://localhost:8090/api";

function EvtDetailComponent({ eventNum }) {
  // 상태
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(true);
  const [alreadyApplied, setAlreadyApplied] = useState(false);

  // 전역
  const navigate = useNavigate();
  const isAdmin = useSelector((state) => state.loginState?.role === "ADMIN");
  const memId = useSelector((state) => state.loginState?.memId);

  // 시각 기준
  const now = new Date();

  // 상세 조회
  const fetchEvent = useCallback(async () => {
    try {
      const data = await getEventById(eventNum);
      setEvent(data);
    } catch (err) {
      console.error("프로그램 정보 조회 실패:", err);
    } finally {
      setLoading(false);
    }
  }, [eventNum]);

  // 신청 여부 조회
  const checkIfApplied = useCallback(async () => {
    if (!memId) return;
    try {
      const res = await fetch(
        `${API_HOST}/event/applied?eventNum=${eventNum}&memId=${encodeURIComponent(memId)}`
      );
      if (!res.ok) throw new Error(await res.text());
      const isApplied = await res.json();
      setAlreadyApplied(isApplied);
    } catch (err) {
      console.error("신청 여부 확인 실패:", err);
    }
  }, [eventNum, memId]);

  useEffect(() => {
    fetchEvent();
    checkIfApplied();
  }, [fetchEvent, checkIfApplied]);

  // 날짜 포맷
  const formatDate = (dateStr) => {
    if (!dateStr) return "없음";
    const d = new Date(dateStr);
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, "0");
    const day = String(d.getDate()).padStart(2, "0");
    const hh = String(d.getHours()).padStart(2, "0");
    const mm = String(d.getMinutes()).padStart(2, "0");
    return `${y}.${m}.${day} ${hh}:${mm}`;
  };

  // 리소스 경로 보정
  const getFullUrl = (path) => (path?.startsWith("http") ? path : `${HOST}/${path}`);

  // 상태 판별
  const isCanceled = event?.state === "CANCEL";
  const isEventStarted =
    event?.eventStartPeriod && now >= new Date(event.eventStartPeriod);
  const isEventEnded =
    event?.eventEndPeriod && now > new Date(event.eventEndPeriod);

  const isApplyPeriod = () =>
    event?.applyStartPeriod &&
    event?.applyEndPeriod &&
    now >= new Date(event.applyStartPeriod) &&
    now <= new Date(event.applyEndPeriod);

  const isFull = () =>
    event?.currCapacity != null &&
    event?.maxCapacity != null &&
    event.currCapacity >= event.maxCapacity;

  // 버튼 비활성 조건
  const isDisabled =
    isCanceled || isEventStarted || isEventEnded || alreadyApplied || !isApplyPeriod() || isFull();

  // 신청 버튼 라벨
  const getApplyButtonText = () => {
    if (isCanceled) return "취소된 프로그램";
    if (isEventEnded) return "프로그램 완료";
    if (isEventStarted) return "프로그램 진행 중";
    if (alreadyApplied) return "신청 완료";
    if (!isApplyPeriod()) return "신청 기간 아님";
    if (isFull()) return "모집 마감";
    return "신청하기";
  };

  // 신청 버튼 스타일 (지정된 네이밍 우선)
  const applyBtnClass = isDisabled
    ? "normal-button !bg-gray-300 !text-gray-700 !border !border-gray-400 cursor-not-allowed w-full"
    : "positive-button w-full";

  // 신청 처리
  const handleApply = async () => {
    if (!memId) {
      alert("로그인한 사용자만 신청할 수 있습니다.");
      return;
    }
    try {
      await applyEvent({ eventNum: event.eventNum, memId });
      setAlreadyApplied(true);
      alert("신청이 완료되었습니다.");
      navigate("/event/list");
    } catch (err) {
      const message = err.response?.data?.message || err.message;
      alert(`신청 실패: ${message}`);
      window.location.reload();
    }
  };

  // 프로그램 취소(관리자)
  const handleCancel = async () => {
    if (!window.confirm("정말 이 프로그램을 취소하시겠습니까?")) return;

    if ((event.currCapacity ?? 0) > 0) {
      const confirmCancel = window.confirm(
        `이미 ${event.currCapacity}명이 신청했습니다.\n그래도 취소하시겠습니까?`
      );
      if (!confirmCancel) return;
    }

    try {
      await deleteEvent(event.eventNum);
      alert("프로그램이 취소되었습니다.");
      navigate("/event/list");
    } catch (err) {
      alert("프로그램 취소 실패: " + (err.response?.data?.message || err.message));
    }
  };

  // 다운로드 링크 렌더
  const renderDownloadLink = (label, url, name, key) => (
    <a
      key={key}
      href={url}
      download
      target="_blank"
      rel="noopener noreferrer"
      className="block newText-sm text-blue-600 hover:underline"
      title={name || label}
    >
      {name || label}
    </a>
  );

  // 카테고리 라벨
  const categoryLabel =
    {
      TEACHER: "교사",
      STUDENT: "학생",
      USER: "일반인",
    }[event?.category] || "미지정";

  // 로딩/에러 상태 뷰
  if (loading) {
    return (
      <div className="max-w-screen-xl mx-auto my-10">
        <div className="min-blank page-shadow bg-white rounded-lg p-10 text-center newText-base">
          로딩 중...
        </div>
      </div>
    );
  }
  if (!event) {
    return (
      <div className="max-w-screen-xl mx-auto my-10">
        <div className="min-blank page-shadow bg-white rounded-lg p-10 text-center newText-base">
          프로그램 정보를 불러올 수 없습니다.
        </div>
      </div>
    );
  }

  // 본문
  return (
    <div className="max-w-screen-xl mx-auto my-10">
      {/* 좌우 여백 고정 */}
      <div className="min-blank page-shadow bg-white rounded-lg p-10">
        {/* 헤더 */}
        <div className="flex items-center justify-between mb-6">
          <h1 className="newText-3xl font-bold">프로그램 상세</h1>
          <button className="normal-button newText-base" onClick={() => navigate(-1)}>
            목록으로
          </button>
        </div>

        {/* 상단 콘텐츠 */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          {/* 이미지 */}
          <div className="flex items-center justify-center">
            {event.mainImagePath ? (
              <img
                src={getFullUrl(event.mainImagePath)}
                alt="프로그램 이미지"
                className="rounded-xl w-full h-auto object-cover"
              />
            ) : (
              <div className="w-full h-[360px] bg-gray-100 flex items-center justify-center newText-base text-gray-500 rounded-xl">
                이미지 없음
              </div>
            )}
          </div>

          {/* 정보 */}
          <div>
            <div className="inline-flex items-center gap-2 newText-sm px-3 py-1 rounded-full border border-blue-400 text-blue-600 mb-3">
              {categoryLabel}
            </div>
            <h2 className="newText-2xl font-bold text-gray-800 mb-4">{event.eventName}</h2>

            {/* 정보 표기: 정의 리스트 */}
            <dl className="grid grid-cols-[120px_1fr] gap-y-2 newText-base text-gray-700">
              <dt className="font-semibold text-gray-600">장소</dt>
              <dd>{event.place || "미정"}</dd>

              <dt className="font-semibold text-gray-600">소개</dt>
              <dd>{event.eventInfo || "내용 없음"}</dd>

              <dt className="font-semibold text-gray-600">신청기간</dt>
              <dd>
                {formatDate(event.applyStartPeriod)} ~ {formatDate(event.applyEndPeriod)}
              </dd>

              <dt className="font-semibold text-gray-600">진행기간</dt>
              <dd>
                {formatDate(event.eventStartPeriod)} ~ {formatDate(event.eventEndPeriod)}
              </dd>

              <dt className="font-semibold text-gray-600">모집인원</dt>
              <dd>{event.maxCapacity || 0}명</dd>

              <dt className="font-semibold text-gray-600">현재인원</dt>
              <dd>{event.currCapacity ?? 0}명</dd>

              <dt className="font-semibold text-gray-600">기타 유의사항</dt>
              <dd>{event.etc || "없음"}</dd>
            </dl>

            {/* 버튼 영역 */}
            <div className="mt-6 space-y-4">
              <button
                className={`${applyBtnClass} newText-base py-3 rounded font-semibold`}
                disabled={isDisabled}
                onClick={handleApply}
              >
                {getApplyButtonText()}
              </button>

              {isAdmin && (
                <div className="flex gap-3">
                  <button
                    className="normal-button newText-base flex-1"
                    onClick={() => navigate(`/event/update/${event.eventNum}`)}
                  >
                    수정
                  </button>

                  {isCanceled ? (
                    <button
                      className="normal-button newText-base flex-1 cursor-not-allowed"
                      disabled
                    >
                      취소 완료
                    </button>
                  ) : (
                    <button
                      className="negative-button newText-base flex-1"
                      onClick={handleCancel}
                    >
                      프로그램 취소
                    </button>
                  )}
                </div>
              )}
            </div>
          </div>
        </div>

        {/* 첨부파일 섹션 */}
        {(event.filePath ||
          event.mainImagePath ||
          (event.attachList && event.attachList.length > 0) ||
          (event.imageList && event.imageList.length > 0)) && (
          <div className="mt-10">
            <h3 className="newText-xl font-semibold mb-3">첨부파일 목록</h3>
            <div className="space-y-1">
              {/* 메인 이미지 */}
              {event.mainImagePath &&
                renderDownloadLink(
                  "대표 이미지",
                  `${API_HOST}/event/download/main-image/${event.eventNum}`,
                  event.mainImageOriginalName
                )}

              {/* 이미지 리스트 */}
              {event.imageList?.map((img) =>
                renderDownloadLink(
                  "이미지",
                  `${API_HOST}/event/download/image/${img.id}`,
                  img.originalName,
                  img.id
                )
              )}

              {/* 메인 첨부파일 */}
              {event.filePath &&
                renderDownloadLink(
                  "대표 첨부파일",
                  `${API_HOST}/event/download/main-file/${event.eventNum}`,
                  event.originalName
                )}

              {/* 첨부파일 리스트 */}
              {event.attachList?.map((file) =>
                renderDownloadLink(
                  "첨부파일",
                  `${API_HOST}/event/download/file/${file.id}`,
                  file.originalName,
                  file.id
                )
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default EvtDetailComponent;