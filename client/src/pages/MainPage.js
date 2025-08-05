import BasicLayout from "../layouts/BasicLayout";
import { Swiper, SwiperSlide } from 'swiper/react';
import { Autoplay, Pagination } from 'swiper/modules';
import 'swiper/css';
import 'swiper/css/effect-fade';
import 'swiper/css/navigation';
import 'swiper/css/pagination';
import '../swiperCss/loop.css'
import main1 from '../assets/main1.jpg';
import main2 from '../assets/main2.jpg';
import main3 from '../assets/main3.jpg';
import main4 from '../assets/main4.jpg';

const MainPage = () => {
    return (
        <div className="">
            <BasicLayout isFullWidth={true}>
                <div className="mx-auto relative">
                    <Swiper
                        spaceBetween={30}
                        centeredSlides={true}
                        autoplay={{
                            delay: 5000,
                            disableOnInteraction: false,
                        }}
                        pagination={{
                            clickable: true,
                        }}
                        modules={[Autoplay, Pagination]}
                        className="mySwiper max-h-screen aspect-[4/3]"
                    >
                        <SwiperSlide>
                            <img src={main1} className="w-full h-full object-cover" />
                        </SwiperSlide>
                        <SwiperSlide>
                            <img src={main2} className="w-full h-full object-cover" />
                        </SwiperSlide>
                        <SwiperSlide>
                            <img src={main3} className="w-full h-full object-cover" />
                        </SwiperSlide>
                        <SwiperSlide>
                            <img src={main4} className="w-full h-full object-cover" />
                        </SwiperSlide>
                    </Swiper>

                    <div className="absolute top-1/4 left-0 w-full z-20 pointer-events-none select-none ">
                        <div className="max-w-screen-xl mx-auto px-4">
                            <div className="text-white text-4xl sm:text-5xl md:text-6xl lg:text-7xl font-bold drop-shadow-[2px_2px_0px_rgba(0,0,0,0.8)]">
                                서울<br />
                                에듀테크<br />
                                소프트랩
                            </div>
                        </div>
                    </div>
                </div>
                <div className="my-10 max-w-screen-xl mx-auto">
                    <div className="my-10">소개 요약 칸</div>
                    <div className="my-10">프로그램소개 칸</div>
                    <div className="my-10">지원사업 소개 칸</div>
                    <div className="my-10">기업 소개 칸</div>
                    <div className="my-10">공지사항 칸</div>
                </div>
            </BasicLayout>
        </div>
    );
};

export default MainPage;
