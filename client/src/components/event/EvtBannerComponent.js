import React, { useEffect, useState } from 'react';
import { Swiper, SwiperSlide } from 'swiper/react';
import { Autoplay } from 'swiper/modules';
import { getBannerEvent } from '../../api/eventApi';
import { useNavigate } from 'react-router-dom';
import useMove from "../../hooks/useMove";
import { useSelector } from "react-redux";

import 'swiper/css';
import 'swiper/css/navigation';

const HOST = "http://localhost:8090/view"; // 이미지 경로

const EvtBannerComponent = () => {
  const [banners, setBanners] = useState([]);
  const navigate = useNavigate();

  const { moveToLogin } = useMove();
  const loginState = useSelector((state) => state.loginState);

  useEffect(() => {
    getBannerEvent()
      .then(data => setBanners(data))
      .catch(err => console.error('배너 불러오기 실패:', err));
  }, []);

  if (banners.length === 0) {
    return (
      <div className="px-6 py-4 rounded-lg shadow-lg border border-gray-200 overflow-hidden">
        <h2 className="newText-xl font-semibold mb-4">프로그램</h2>
        <div className="text-center p-6 text-gray-500">
          이달의 신청가능한 프로그램이 없습니다.
        </div>
      </div>
    )
  }

  return (
    <div className="px-6 py-4 rounded-lg shadow-lg border border-gray-200 overflow-hidden">
      <h2 className="newText-xl font-semibold mb-4 ">프로그램</h2>

      <Swiper
        modules={[Autoplay]}
        autoplay={{ delay: 4000, disableOnInteraction: false }}
        loop
        speed={800}
        spaceBetween={30}
        breakpoints={{
          0: {
            slidesPerView: 1,
          },
          768: {
            slidesPerView: 2,
          },
          1024: {
            slidesPerView: 3,
          },
        }}
        className="w-full"
      >
        {banners.map((event, idx) => (
          <SwiperSlide key={idx}>
            <div
              className="cursor-pointer text-center w-full max-w-[400px] mx-auto"
              onClick={() => {
                if (loginState && loginState.memId) {
                  navigate(`/event/detail/${event.eventNum}`);
                } else {  // 로그인창 이동
                  alert("로그인이 필요합니다.");
                  moveToLogin();
                }
              }}
            >
              <div className="w-full aspect-[4/3] rounded-xl overflow-hidden mb-4">
                <img
                  src={event.mainImagePath ? `${HOST}/${event.mainImagePath}` : '/default/image.png'}
                  alt={event.eventName}
                  className="w-full h-full object-cover object-center"
                  onError={(e) => {
                    e.currentTarget.onerror = null;
                    e.currentTarget.src = '/default/image.png';
                  }}
                />
              </div>
              <div className='pb-2'>
                <p className="newText-lg font-bold text-green-700 mb-1">[{event.eventName}]</p>
                <p className="newText-sm text-gray-600">
                  {event.eventStartPeriod?.slice(0, 10)} ~ {event.eventEndPeriod?.slice(0, 10)}
                </p>
                <p className="newText-sm text-gray-500">
                  {event.eventStartPeriod?.slice(11, 16)} ~ {event.eventEndPeriod?.slice(11, 16)}
                </p>
                <p className="newText-sm text-gray-500">
                  {event.category === 'STUDENT' ? '학생 대상'
                    : event.category === 'TEACHER' ? '교직원 대상'
                      : '일반 대상'}
                </p>
              </div>
            </div>
          </SwiperSlide>
        ))}
      </Swiper>
    </div>
  );
};

export default EvtBannerComponent;
