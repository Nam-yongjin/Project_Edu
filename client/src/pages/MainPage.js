import BasicLayout from "../layouts/BasicLayout";
import { Swiper, SwiperSlide } from 'swiper/react';
import { Autoplay, Navigation, Pagination } from 'swiper/modules';
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
                <div className="mx-auto">
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
                        navigation={true}
                        modules={[Autoplay, Pagination, Navigation]}
                        className="mySwiper max-h-screen aspect-[4/3]"
                    >
                        <SwiperSlide>
                            <img src={main1}/>
                        </SwiperSlide>
                        <SwiperSlide>
                            <img src={main2}/>
                        </SwiperSlide>
                        <SwiperSlide>
                            <img src={main3}/>
                        </SwiperSlide>
                        <SwiperSlide>
                            <img src={main4}/>
                        </SwiperSlide>
                    </Swiper>
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
