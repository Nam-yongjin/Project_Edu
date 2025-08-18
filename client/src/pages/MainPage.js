import BasicLayout from "../layouts/BasicLayout";
import { Swiper, SwiperSlide } from 'swiper/react';
import { Autoplay, Pagination } from 'swiper/modules';
import 'swiper/css';
import 'swiper/css/effect-fade';
import 'swiper/css/navigation';
import 'swiper/css/pagination';
import '../swiperCss/main.css'
import { useState, useEffect } from "react";
import { getAllBanners, getBannerImage, recordVisitor } from "../api/adminApi";

import NoticeMainComponent from "../components/notice/NoticeMainComponent";
import NewsMainComponent from "../components/news/NewsMainComponent";

const MainPage = () => {
    const [banners, setBanners] = useState([]);

    useEffect(() => {
        const fetchBanners = () => {
            getAllBanners()
                .then(data => {
                    setBanners(data);
                })
                .catch(error => {
                    console.error("배너를 불러올 수 없습니다.:", error);
                });
        };
        fetchBanners();
        recordVisitor();    // 방문자 카운트
    }, []);

    return (
        <div className="">
            <BasicLayout isFullWidth={true}>
                <div className="mx-auto relative">
                    {banners.length > 0 && (
                        <Swiper
                            centeredSlides={true}
                            autoplay={{
                                delay: 5000,
                                disableOnInteraction: false,
                            }}
                            pagination={{
                                clickable: true,
                            }}
                            loop={true}
                            loopedSlides={banners.length}
                            lazy={{
                                loadPrevNext: true,
                            }}
                            speed={1200}
                            modules={[Autoplay, Pagination]}
                            className="mySwiper max-h-[80vh] aspect-video"
                        >
                            {banners.map((banner) => (
                                <SwiperSlide key={banner.bannerNum}>
                                    <img
                                        src={getBannerImage(banner.imagePath)}
                                        className="w-full h-full object-cover"
                                        loading="lazy"
                                        alt={banner.originalName}
                                    />
                                </SwiperSlide>
                            ))}
                        </Swiper>
                    )}

                    <div className="min-blank absolute top-1/4 left-0 w-full z-20 pointer-events-none select-none">
                        <div className="max-w-screen-xl mx-auto">
                            <div className="text-white text-4xl sm:text-5xl md:text-6xl lg:text-7xl font-bold drop-shadow-[2px_2px_0px_rgba(0,0,0,0.8)]">
                                서울<br />
                                에듀테크<br />
                                소프트랩
                            </div>
                        </div>
                    </div>
                </div>
                <div className="my-10 max-w-screen-xl mx-auto">
                    <div className="min-blank my-10">소개 요약 칸</div>
                    <div className="min-blank my-10">프로그램소개 칸</div>
                    <div className="min-blank my-10">지원사업 소개 칸</div>
                    <div className="min-blank my-10">기업 소개 칸</div>
                    <div className="min-blank my-10">
                         <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <NoticeMainComponent />
                            <NewsMainComponent />
                        </div>
                    </div>
                </div>
            </BasicLayout>
        </div>
    );
};

export default MainPage;
