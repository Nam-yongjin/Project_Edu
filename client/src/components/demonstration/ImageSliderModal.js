import { Swiper, SwiperSlide } from 'swiper/react';
import { Navigation, Pagination } from 'swiper/modules';
import 'swiper/css';
import 'swiper/css/navigation';
import 'swiper/css/pagination';

const ImageSliderModal = ({ open, onClose, imageList }) => {
    if (!open) return null;

    const handleBackgroundClick = (e) => {
        if (e.target === e.currentTarget) {
            onClose();
        }
    };

    return (
        <div 
            className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 p-8"
            onClick={handleBackgroundClick}
        >
            <div className="bg-white rounded-lg p-6 w-full max-w-4xl mx-auto max-h-[90vh] flex flex-col">
                <div className="flex-1 flex items-center justify-center min-h-0">
                    <div className="w-[600px] h-[600px]">
                        <Swiper
                            modules={[Navigation, Pagination]}
                            spaceBetween={10}
                            slidesPerView={1}
                            navigation={true}
                            pagination={{ 
                                clickable: true,
                                dynamicBullets: true 
                            }}
                            className="w-[600px] h-[600px] rounded-lg overflow-hidden"
                            style={{
                                '--swiper-navigation-color': '#3B82F6',
                                '--swiper-pagination-color': '#3B82F6',
                                '--swiper-navigation-size': '30px'
                            }}
                        >
                            {imageList.map((url, idx) => (
                                <SwiperSlide key={idx} className="flex justify-center items-center bg-gray-100">
                                    <img
                                        src={url}
                                        alt={`preview-${idx}`}
                                        className="w-[600px] h-[600px] object-cover rounded-lg"
                                    />
                                </SwiperSlide>
                            ))}
                        </Swiper>
                    </div>
                </div>
                
                <div className="mt-6 flex justify-end">
                    <button 
                        className="px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600 transition-colors" 
                        onClick={onClose}
                    >
                        닫기
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ImageSliderModal;