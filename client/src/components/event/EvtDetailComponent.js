import React, { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { getEventById, deleteEvent } from "../../api/eventApi";

const HOST = "http://localhost:8090/view";
const API_HOST = "http://localhost:8090/api";

const EvtDetailComponent = ({ eventNum }) => {
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const isAdmin = useSelector((state) => state.loginState?.role === "ADMIN");

  useEffect(() => {
    const fetchEvent = async () => {
      try {
        const data = await getEventById(eventNum);
        setEvent(data);
      } catch (err) {
        console.error("행사 정보 조회 실패:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchEvent();
  }, [eventNum]);

  const formatDate = (dateStr) =>
    dateStr ? new Date(dateStr).toISOString().split("T")[0] : "없음";

  const getFullUrl = (path) =>
    path?.startsWith("http") ? path : `${HOST}/${path}`;

  const categoryLabel = {
    TEACHER: "교사",
    STUDENT: "학생",
    USER: "일반인",
  }[event?.category] || "미지정";

  const handleDelete = async () => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;
    try {
      await deleteEvent(event.eventNum);
      alert("삭제가 완료되었습니다.");
      navigate("/event/list");
    } catch (err) {
      alert("삭제 실패: " + (err.response?.data?.message || err.message));
    }
  };

  const renderDownloadLink = (label, url, name) => (
    <a
      href={url}
      download
      target="_blank"
      rel="noopener noreferrer"
      className="block text-sm text-blue-600 hover:underline"
    >
      {name || label}
    </a>
  );

  if (loading) return <div className="text-center p-10">로딩 중...</div>;
  if (!event) return <div className="text-center p-10">행사 정보를 불러올 수 없습니다.</div>;

  return (
    <div className="max-w-6xl mx-auto p-6 bg-white rounded shadow mt-8 space-y-10">
      {/* 상단 - 대표 이미지 + 행사 정보 */}
      <div className="flex flex-col md:flex-row gap-8">
        {/* 대표 이미지 */}
        <div className="md:w-1/2 flex items-center justify-center">
          {event.mainImagePath ? (
            <img
              src={getFullUrl(event.mainImagePath)}
              alt="행사 이미지"
              className="rounded-xl w-full h-auto object-cover"
            />
          ) : (
            <div className="w-full h-[400px] bg-gray-100 flex items-center justify-center text-gray-500">
              이미지 없음
            </div>
          )}
        </div>

        {/* 행사 정보 */}
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

          {/* 버튼 */}
          <div className="pt-6 space-y-4">
            <button className="w-full bg-blue-500 text-white py-3 rounded hover:bg-blue-600 font-semibold">
              신청하기
            </button>

            {isAdmin && (
              <div className="flex gap-4">
                <button
                  className="flex-1 bg-yellow-500 text-white py-2 rounded hover:bg-yellow-600"
                  onClick={() => navigate(`/event/update/${event.eventNum}`)}
                >
                  수정
                </button>
                <button
                  className="flex-1 bg-red-500 text-white py-2 rounded hover:bg-red-600"
                  onClick={handleDelete}
                >
                  삭제
                </button>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* 첨부파일 및 이미지 다운로드 */}
      {(event.filePath || event.mainImagePath || event.attachList?.length > 0 || event.imageList?.length > 0) && (
        <div className="w-full">
          <h3 className="text-lg font-semibold text-gray-800 mb-4">📎 첨부파일 목록</h3>
          <div className="space-y-1">
            {event.filePath &&
              renderDownloadLink(
                "대표 첨부파일",
                `${API_HOST}/event/download/main-file/${event.eventNum}`,
                event.originalName
              )}
            {event.mainImagePath &&
              renderDownloadLink(
                "대표 이미지",
                `${API_HOST}/event/download/main-image/${event.eventNum}`,
                event.mainImageOriginalName
              )}
            {event.attachList?.map((file) =>
              renderDownloadLink(
                "첨부파일",
                `${API_HOST}/event/download/file/${file.id}`,
                file.originalName
              )
            )}
            {event.imageList?.map((img) =>
              renderDownloadLink(
                "이미지",
                `${API_HOST}/event/download/image/${img.id}`,
                img.originalName
              )
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default EvtDetailComponent;