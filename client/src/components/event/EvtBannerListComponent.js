import React, { useEffect, useState } from 'react';
import { getBannerList, registerBanner, deleteBanner } from '../../api/eventApi';
import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';

const EvtBannerList = () => {
  const [events, setEvents] = useState([]);
  const [page, setPage] = useState(1);
  const navigate = useNavigate();
  const host = "http://localhost:8090/view";

  const isAdmin = useSelector((state) => state.loginState?.role === "ADMIN");

  // 권한 확인
  useEffect(() => {
    if (!isAdmin) {
      alert("권한이 없습니다.");
      navigate("/event/list");
    }
  }, [isAdmin, navigate]);

  // 배너용 행사 목록 조회
  useEffect(() => {
    if (isAdmin) {
      fetchBannerList();
    }
  }, [page, isAdmin]);

  const fetchBannerList = async () => {
    try {
      const data = await getBannerList(page);
      setEvents(data.content);
    } catch (err) {
      console.error("배너용 행사 목록 불러오기 실패", err);
    }
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

  // 배너 등록 요청
  const handleBannerRegister = async (eventNum) => {
    try {
      const formData = new FormData();
      formData.append("eventNum", eventNum); // 서버 DTO 필드명과 일치

      await registerBanner(formData);
      alert("배너 등록이 완료되었습니다.");
      fetchBannerList(); // 리스트 갱신
    } catch (err) {
      console.error("배너 등록 실패:", err);
      if (err.response && err.response.data) {
        alert(err.response.data);
      } else {
        alert("배너 등록 중 오류가 발생했습니다.");
      }
    }
  };

  // 배너 삭제 요청
  const handleBannerDelete = async (eventNum) => {
    if (!window.confirm("정말 이 행사에서 배너를 제거하시겠습니까?")) return;

    try {
      await deleteBanner(eventNum);
      alert("배너가 정상적으로 제거되었습니다.");
      fetchBannerList(); // 리스트 갱신
    } catch (err) {
      console.error("배너 삭제 실패:", err);
      if (err.response?.data) {
        alert(err.response.data);
      } else {
        alert("배너 제거 중 오류가 발생했습니다.");
      }
    }
  };

  return (
    <div className="w-full flex justify-center">
      <div className="w-full max-w-4xl px-4 py-6 space-y-4">
        {events.map((event) => (
          <div
            key={event.eventNum}
            className="flex items-center justify-between border rounded shadow p-4 bg-white"
          >
            {/* 대표 이미지 */}
            <div className="w-24 h-24 flex items-center justify-center bg-gray-100 overflow-hidden">
              {event.mainImagePath ? (
                <img
                  src={`${host}/${event.mainImagePath}`}
                  alt={event.mainImageOriginalName || "대표 이미지"}
                  className="w-full h-full object-cover"
                />
              ) : (
                <span className="text-sm text-gray-500">이미지 없음</span>
              )}
            </div>

            {/* 행사 정보 */}
            <div className="flex-1 ml-6 text-sm">
              <p><strong>행사이름:</strong> {event.eventName}</p>
              <p><strong>신청일자:</strong> {formatDateTime(event.applyAt)}</p>
              <p><strong>행사 일정:</strong> {formatDateTime(event.eventStartPeriod)} ~ {formatDateTime(event.eventEndPeriod)}</p>
              <p><strong>상태:</strong> <span className="text-blue-600">{event.state || '정보 없음'}</span></p>
            </div>

            {/* 버튼 영역 */}
            <div className="ml-4 flex flex-col items-end space-y-2">
              {event.bannerState === "YES" ? (
                <>
                  <button
                    disabled
                    className="px-4 py-2 bg-gray-400 text-white text-sm rounded cursor-not-allowed"
                  >
                    배너 등록 완료
                  </button>

                  <button
                    onClick={() => handleBannerDelete(event.bannerNum)}
                    className="px-4 py-2 bg-red-500 text-white text-sm rounded hover:bg-red-600"
                  >
                    배너 등록 제거
                  </button>
                </>
              ) : (
                <button
                  onClick={() => handleBannerRegister(event.eventNum)}
                  className="px-4 py-2 bg-blue-500 text-white text-sm rounded hover:bg-blue-600"
                >
                  배너등록
                </button>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default EvtBannerList;