import React, { useEffect, useState, useCallback } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { getEventById, deleteEvent, applyEvent } from "../../api/eventApi";

const HOST = "http://localhost:8090/view";
const API_HOST = "http://localhost:8090/api";

function EvtDetailComponent({ eventNum }) {
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(true);
  const [alreadyApplied, setAlreadyApplied] = useState(false);

  const navigate = useNavigate();
  const isAdmin = useSelector((state) => state.loginState?.role === "ADMIN");
  const memId = useSelector((state) => state.loginState?.memId);

  const now = new Date();

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

  const checkIfApplied = useCallback(async () => {
    if (!memId) return;
    try {
      const res = await fetch(`${API_HOST}/event/applied?eventNum=${eventNum}&memId=${encodeURIComponent(memId)}`);
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

  const formatDate = (dateStr) => {
    if (!dateStr) return "없음";
    const date = new Date(dateStr);
    return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, "0")}.${String(date.getDate()).padStart(2, "0")} ${String(date.getHours()).padStart(2, "0")}:${String(date.getMinutes()).padStart(2, "0")}`;
  };

  const getFullUrl = (path) => path?.startsWith("http") ? path : `${HOST}/${path}`;

  const isCanceled = event?.state === "CANCEL";
  const isEventStarted = event?.eventStartPeriod && now >= new Date(event.eventStartPeriod);
  const isEventEnded = event?.eventEndPeriod && now > new Date(event.eventEndPeriod);

  const isApplyPeriod = () =>
    event?.applyStartPeriod &&
    event?.applyEndPeriod &&
    now >= new Date(event.applyStartPeriod) &&
    now <= new Date(event.applyEndPeriod);

  const isFull = () =>
    event?.currCapacity != null &&
    event?.maxCapacity != null &&
    event.currCapacity >= event.maxCapacity;

  const isDisabled = isCanceled || isEventStarted || isEventEnded || alreadyApplied || !isApplyPeriod() || isFull();

  const getApplyButtonText = () => {
    if (isCanceled) return "취소된 프로그램";
    if (isEventEnded) return "프로그램 완료";
    if (isEventStarted) return "프로그램 진행 중";
    if (alreadyApplied) return "신청 완료";
    if (!isApplyPeriod()) return "신청 기간 아님";
    if (isFull()) return "모집 마감";
    return "신청하기";
  };

  const getApplyButtonStyle = () =>
    isDisabled ? "bg-gray-300 text-gray-500 cursor-not-allowed" : "bg-blue-500 text-white hover:bg-blue-600";

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

  const handleCancel = async () => {
    if (!window.confirm("정말 이 프로그램를 취소하시겠습니까?")) return;

    if ((event.currCapacity ?? 0) > 0) {
      const confirmCancel = window.confirm(
        `이미 ${event.currCapacity}명이 신청했습니다.\n그래도 취소하시겠습니까?`
      );
      if (!confirmCancel) return;
    }

    try {
      await deleteEvent(event.eventNum);
      alert("프로그램가 취소되었습니다.");
      navigate("/event/list");
    } catch (err) {
      alert("프로그램 취소 실패: " + (err.response?.data?.message || err.message));
    }
  };

  const renderDownloadLink = (label, url, name, key) => (
    <a
      key={key}
      href={url}
      download
      target="_blank"
      rel="noopener noreferrer"
      className="block text-sm text-blue-600 hover:underline"
    >
      {name || label}
    </a>
  );

  const categoryLabel = {
    TEACHER: "교사",
    STUDENT: "학생",
    USER: "일반인",
  }[event?.category] || "미지정";

  if (loading) return <div className="text-center p-10">로딩 중...</div>;
  if (!event) return <div className="text-center p-10">프로그램 정보를 불러올 수 없습니다.</div>;

  return (
    <div className="max-w-6xl mx-auto p-6 bg-white rounded shadow mt-8 space-y-10">
      <div className="flex flex-col md:flex-row gap-8">
        <div className="md:w-1/2 flex items-center justify-center">
          {event.mainImagePath ? (
            <img
              src={getFullUrl(event.mainImagePath)}
              alt="프로그램 이미지"
              className="rounded-xl w-full h-auto object-cover"
            />
          ) : (
            <div className="w-full h-[400px] bg-gray-100 flex items-center justify-center text-gray-500">
              이미지 없음
            </div>
          )}
        </div>

        <div className="md:w-1/2 space-y-4">
          <div className="text-sm inline-block border border-blue-400 text-blue-600 px-3 py-1 rounded-full">
            {categoryLabel}
          </div>
          <h2 className="text-2xl font-bold text-gray-800">{event.eventName}</h2>

          <div className="space-y-2 text-gray-700 text-sm">
            <p><strong>장소:</strong> {event.place || "미정"}</p>
            <p><strong>소개:</strong> {event.description || "내용 없음"}</p>
            <p><strong>신청기간:</strong> {formatDate(event.applyStartPeriod)} ~ {formatDate(event.applyEndPeriod)}</p>
            <p><strong>진행기간:</strong> {formatDate(event.eventStartPeriod)} ~ {formatDate(event.eventEndPeriod)}</p>
            <p><strong>모집인원:</strong> {event.maxCapacity || 0}명</p>
            <p><strong>현재인원:</strong> {event.currCapacity ?? 0}명</p>
            <p><strong>기타 유의사항:</strong> {event.etc || "없음"}</p>
          </div>

          <div className="pt-6 space-y-4">
            <button
              className={`w-full py-3 rounded font-semibold transition ${getApplyButtonStyle()}`}
              disabled={isDisabled}
              onClick={handleApply}
            >
              {getApplyButtonText()}
            </button>

            {isAdmin && (
              <div className="flex gap-4">
                <button
                  className="flex-1 bg-yellow-500 text-white py-2 rounded hover:bg-yellow-600"
                  onClick={() => navigate(`/event/update/${event.eventNum}`)}
                >
                  수정
                </button>
                {isCanceled ? (
                  <button
                    className="flex-1 bg-gray-400 text-white py-2 rounded cursor-not-allowed"
                    disabled
                  >
                    취소 완료
                  </button>
                ) : (
                  <button
                    className="flex-1 bg-red-500 text-white py-2 rounded hover:bg-red-600"
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

      {(event.filePath || event.mainImagePath || event.attachList?.length > 0 || event.imageList?.length > 0) && (
        <div className="w-full">
          <h3 className="text-lg font-semibold text-gray-800 mb-4">📎 첨부파일 목록</h3>
          <div className="space-y-1">
            {event.filePath &&
              renderDownloadLink("대표 첨부파일", `${API_HOST}/event/download/main-file/${event.eventNum}`, event.originalName)}
            {event.mainImagePath &&
              renderDownloadLink("대표 이미지", `${API_HOST}/event/download/main-image/${event.eventNum}`, event.mainImageOriginalName)}
            {event.attachList?.map((file) =>
              renderDownloadLink("첨부파일", `${API_HOST}/event/download/file/${file.id}`, file.originalName, file.id)
            )}
            {event.imageList?.map((img) =>
              renderDownloadLink("이미지", `${API_HOST}/event/download/image/${img.id}`, img.originalName, img.id)
            )}
          </div>
        </div>
      )}
    </div>
  );
}

export default EvtDetailComponent;