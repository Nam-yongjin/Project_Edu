import { Swiper, SwiperSlide } from 'swiper/react';
import 'swiper/css';

const ImageSliderModal = ({ open, onClose, imageList }) => {
    if (!open) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
            <div className="bg-white p-4 rounded-md min-w-[600px] max-w-full min-blank">
                <Swiper
                    spaceBetween={10}
                    slidesPerView={1}
                    pagination={{ clickable: true }}
                    className="w-[500px] h-[400px] bg-white flex justify-center items-center"
                >
                    {imageList.map((url, idx) => (
                        <SwiperSlide key={idx} className="flex justify-center items-center">
                            <img
                                src={url}
                                alt={`preview-${idx}`}
                                className="w-full h-full object-cover rounded-md"
                            />
                        </SwiperSlide>

                    ))}
                </Swiper>
                <div className="mt-4 flex justify-end">
                    <button className="normal-button rounded" onClick={onClose}>닫기</button>
                </div>

            </div>
        </div>
    );
};

export default ImageSliderModal;
