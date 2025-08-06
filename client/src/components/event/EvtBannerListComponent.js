import React, { useEffect, useState } from 'react';
import { getBannerList } from '../../api/eventApi';

const EvtBannerList = () => {
  const [events, setEvents] = useState([]);
  const [page, setPage] = useState(1);

  useEffect(() => {
    getBannerList(page)
      .then((data) => {
        setEvents(data.content);
      })
      .catch((err) => console.error("배너용 행사 목록 불러오기 실패", err));
  }, [page]);

  const handleBannerRegister = (eventNum) => {
    console.log("배너 등록 요청:", eventNum);
    // TODO: 배너 등록 API 연동
  };

  return (
    <div className="w-full flex justify-center">
      <div className="w-full max-w-4xl px-4 py-6 space-y-4">
        {events.map((event) => (
          <div
            key={event.eventNum}
            className="flex items-center justify-between border rounded shadow p-4 bg-white"
          >
            {/* 이미지 */}
            <div className="w-24 h-24 flex items-center justify-center bg-gray-100 text-gray-500 flex-shrink-0 overflow-hidden">
              {event.mainImagePath ? (
                <img
                  src={
                    event.mainImagePath.startsWith("http")
                      ? event.mainImagePath
                      : `/upload/${event.mainImagePath}`
                  }
                  alt={event.originalName}
                  className="w-full h-full object-cover"
                />
              ) : (
                <span className="text-sm">이미지 없음</span>
              )}
            </div>

            {/* 행사 정보 */}
            <div className="flex-1 ml-6 text-sm">
              <p><strong>행사이름:</strong> {event.eventName}</p>
              <p><strong>신청일자:</strong> {formatDateTime(event.applyAt)}</p>
              <p><strong>행사 일정:</strong> {formatDateTime(event.eventStartPeriod)} ~ {formatDateTime(event.eventEndPeriod)}</p>
              <p><strong>상태:</strong> <span className="text-blue-600">{event.state || '정보 없음'}</span></p>
            </div>

            {/* 배너 등록 버튼 */}
            <div className="ml-4 flex-shrink-0">
              <button
                onClick={() => handleBannerRegister(event.eventNum)}
                className="px-4 py-2 bg-blue-500 text-white text-sm rounded hover:bg-blue-600"
              >
                배너등록
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

// 날짜 포맷 함수
const formatDateTime = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, '0');
  const dd = String(date.getDate()).padStart(2, '0');
  const hh = String(date.getHours()).padStart(2, '0');
  const mi = String(date.getMinutes()).padStart(2, '0');
  return `${yyyy}.${mm}.${dd} ${hh}:${mi}`;
};

export default EvtBannerList;
