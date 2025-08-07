import { Swiper, SwiperSlide } from 'swiper/react';
import 'swiper/css';

const ImageSliderModal = ({ open, onClose, imageList }) => {
    if (!open) return null;

    return (
        <div className="fixed inset-0 bg-white bg-opacity-30 backdrop-blur-sm flex items-center justify-center z-50">
            <div className="bg-white p-4 rounded-md relative w-[600px] max-w-full">
                <button
                    className="absolute top-2 right-2 text-gray-600 z-50 bg-white rounded-full p-1"
                    onClick={onClose}
                >
                    ✕
                </button>

                <Swiper
                    spaceBetween={10}
                    slidesPerView={1}
                    pagination={{ clickable: true }}
                      className="w-[500px] h-[400px] bg-white flex justify-center items-center"
                >
                    {imageList.map((url, idx) => (
                        <SwiperSlide key={idx}>
                            <img
                                src={url}
                                alt={`preview-${idx}`}
                                className="max-w-full max-h-[400px] object-contain rounded-md block mx-auto"
                            />
                        </SwiperSlide>
                    ))}
                </Swiper>
             
            </div>
        </div>
    );
};

export default ImageSliderModal;
