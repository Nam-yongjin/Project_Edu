import React, { useEffect, useState } from "react";
import { getEventById } from "../../api/eventApi";

const HOST = "http://localhost:8090"; // 백엔드 API 서버 주소

const EvtDetailComponent = ({ eventNum }) => {
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(true);

  // 🔹 날짜 포맷 헬퍼
  const formatDate = (dateStr) => {
    if (!dateStr) return "없음";
    const date = new Date(dateStr);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
  };

  // 🔹 경로를 전체 URL로 변환
  const getFullUrl = (path) => {
    if (!path) return "";
    return path.startsWith("http") ? path : `${HOST}/${path}`;
  };

  // 🔹 모집 대상 한글 변환
  const getCategoryLabel = (category) => {
    const labels = { TEACHER: "교사", STUDENT: "학생", USER: "일반인" };
    return labels[category] || category || "미지정";
  };

  // 🔹 행사 정보 로딩
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

  // 🔹 로딩/오류 처리
  if (loading) return <div className="text-center p-10">로딩 중...</div>;
  if (!event) return <div className="text-center p-10">행사 정보를 불러올 수 없습니다.</div>;

  const categoryLabel = getCategoryLabel(event.category);

  return (
    <div className="max-w-6xl mx-auto p-6 bg-white rounded shadow flex flex-col md:flex-row gap-8 mt-8">
      
      {/* ✅ 좌측: 이미지 */}
      <div className="md:w-1/2 flex items-center justify-center">
        {event.filePath ? (
          <img
            src={getFullUrl(event.filePath)}
            alt="행사 이미지"
            className="rounded-xl w-full h-auto object-cover"
          />
        ) : (
          <div className="w-full h-[400px] bg-gray-100 flex items-center justify-center text-gray-500">
            이미지 없음
          </div>
        )}
      </div>

      {/* ✅ 우측: 행사 정보 */}
      <div className="md:w-1/2 space-y-4">

        {/* 모집 대상 뱃지 */}
        <div className="text-sm inline-block border border-blue-400 text-blue-600 px-3 py-1 rounded-full">
          {categoryLabel}
        </div>

        {/* 제목 */}
        <h2 className="text-2xl font-bold text-gray-800">{event.eventName}</h2>

        {/* 상세 정보 */}
        <div className="space-y-2 text-gray-700 text-sm">
          <p><strong>장소</strong>: {event.place || "미정"}</p>
          <p><strong>소개</strong>: {event.description || "내용 없음"}</p>
          <p><strong>신청기간</strong>: {formatDate(event.applyStartPeriod)} ~ {formatDate(event.applyEndPeriod)}</p>
          <p><strong>진행기간</strong>: {formatDate(event.eventStartPeriod)} ~ {formatDate(event.eventEndPeriod)}</p>
          <p><strong>모집대상</strong>: {categoryLabel}</p>
          <p><strong>모집인원</strong>: {event.maxCapacity ? `${event.maxCapacity}명` : "미정"}</p>
          <p><strong>현재인원</strong>: {event.currCapacity ?? 0}명</p>
          <p><strong>기타 유의사항</strong>: {event.etc || "없음"}</p>
        </div>

        {/* 첨부파일 */}
        {event.attachList?.length > 0 && (
          <div>
            <h4 className="font-semibold mt-4">첨부파일</h4>
            <ul className="list-disc ml-5 text-blue-600 text-sm">
              {event.attachList.map((file, idx) => (
                <li key={idx}>
                  <a href={getFullUrl(file.fileUrl)} target="_blank" rel="noreferrer" download>
                    {file.originalName}
                  </a>
                </li>
              ))}
            </ul>
          </div>
        )}

        {/* 신청 버튼 */}
        <div className="pt-6">
          <button className="w-full bg-blue-500 text-white py-3 rounded hover:bg-blue-600 font-semibold">
            신청하기
          </button>
        </div>

      </div>
    </div>
  );
};

export default EvtDetailComponent;
