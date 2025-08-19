import React, { useEffect, useState } from 'react';
import { Swiper, SwiperSlide } from 'swiper/react';
import { Navigation, Pagination, Autoplay } from 'swiper/modules';
import { getBannerEvent } from '../../api/eventApi';
import { useNavigate } from 'react-router-dom';

import 'swiper/css';
import 'swiper/css/navigation';
import 'swiper/css/pagination';

const HOST = "http://localhost:8090/view"; // 이미지 경로

const EvtBannerComponent = () => {
  const [banners, setBanners] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    getBannerEvent()
      .then(data => setBanners(data))
      .catch(err => console.error('배너 불러오기 실패:', err));
  }, []);

  if (banners.length === 0) {
    return (
      <div className="px-4 py-6 page-shadow">
        <h2 className="newText-xl font-semibold mb-4">프로그램</h2>
        <div className="text-center p-6 text-gray-500">
          신청가능한 프로그램이 없습니다.
        </div>
      </div>
    )
  }

  return (
    <div className="px-4 py-6 page-shadow">
      <h2 className="newText-xl font-semibold mb-4">프로그램</h2>

      <Swiper
        modules={[Navigation, Pagination, Autoplay]}
        navigation
        pagination={{ clickable: true }}
        autoplay={{ delay: 4000, disableOnInteraction: false }}
        loop
        spaceBetween={30}
        slidesPerView={1}
        className="w-full"
      >
        {banners.map((event, idx) => (
          <SwiperSlide key={idx}>
            <div
              className="cursor-pointer text-center"
              onClick={() => navigate(`/event/detail/${event.eventNum}`)}
            >
              <img
                src={event.mainImagePath ? `${HOST}/${event.mainImagePath}` : '/default/image.png'}
                alt={event.eventName}
                className="w-full h-[300px] object-cover rounded-xl mb-4"
                onError={(e) => {
                  e.currentTarget.onerror = null;
                  e.currentTarget.src = '/default/image.png';
                }}
              />
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
          </SwiperSlide>
        ))}
      </Swiper>
    </div>
  );
};

export default EvtBannerComponent;
