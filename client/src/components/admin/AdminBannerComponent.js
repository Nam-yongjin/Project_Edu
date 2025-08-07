import { useState, useEffect, useRef } from 'react';
import { getAllBanners, uploadBanners, deleteBanner, updateBannerSequence, getBannerImage } from '../../api/adminApi';
import cancel from "../../assets/cancel.png";

const AdminBannerComponent = () => {
    const [banners, setBanners] = useState([]);
    const [selectedFiles, setSelectedFiles] = useState([]);
    const dragItem = useRef(null);
    const dragOverItem = useRef(null);

    useEffect(() => {
        fetchBanners();
    }, []);

    const fetchBanners = () => {
        getAllBanners()
            .then(data => {
                setBanners(data);
            })
            .catch(error => {
                alert('배너 목록을 불러오는 데 실패했습니다.', error);
            });
    };

    const handleFileChange = (e) => {
        setSelectedFiles(Array.from(e.target.files));
    };

    const handleUpload = () => {
        if (selectedFiles.length === 0) {
            alert('파일을 선택해 주세요.');
            return;
        }

        const formData = new FormData();
        selectedFiles.forEach(file => formData.append('files', file));

        uploadBanners(formData)
            .then(() => {
                alert('배너가 성공적으로 업로드되었습니다.');
                fetchBanners();
                setSelectedFiles([]);
            })
            .catch(error => {
                alert('업로드 실패', error);
            });
    };

    const handleDelete = (bannerNum) => {
        if (window.confirm('정말로 이 배너를 삭제하시겠습니까?')) {
            deleteBanner(bannerNum)
                .then(() => {
                    alert('배너가 삭제되었습니다.');
                    fetchBanners();
                })
                .catch(error => {
                    alert('삭제 실패', error);
                });
        }
    };

    const handleDragStart = (e, index) => {
        dragItem.current = index;
    };

    const handleDragEnter = (e, index) => {
        dragOverItem.current = index;
    };

    // 드래그로 순서 변경
    const handleDrop = () => {
        const newBanners = [...banners];
        const dragItemContent = newBanners[dragItem.current];
        newBanners.splice(dragItem.current, 1);
        newBanners.splice(dragOverItem.current, 0, dragItemContent);

        dragItem.current = null;
        dragOverItem.current = null;
        setBanners(newBanners);

        const bannerNums = newBanners.map(banner => banner.bannerNum);

        // 순서 변경
        updateBannerSequence(bannerNums)
            .then(() => {
                alert('배너 순서가 업데이트되었습니다.');
                fetchBanners();
            })
            .catch(error => {
                alert('순서 업데이트에 실패했습니다.', error);
            });
    };

    return (
        <div className='max-w-screen-xl mx-auto my-10 px-4'>
            <div className="mb-4 flex flex-col sm:flex-row sm:items-center sm:justify-between">
                <div className="text-2xl font-bold">
                    배너 관리
                    <span className='ml-2 font-medium text-base text-gray-500'>(드래그시 순서 변경)</span>
                </div>

                <div className="flex gap-2 sm:flex-row-reverse mt-4 sm:m-0">

                    <label className="cursor-pointer normal-button">
                        <span className="truncate">파일 선택</span>
                        <input type="file" multiple onChange={handleFileChange} className='hidden' />
                    </label>
                    <button
                        onClick={handleUpload}
                        className='positive-button w-[80px]'
                    >
                        업로드
                    </button>
                    {selectedFiles.length > 0 && (
                        <div className="text-sm text-gray-600 py-3">
                            {selectedFiles.length}개의 파일 선택됨
                        </div>
                    )}
                </div>
            </div>

            <div className='bg-white p-6 page-shadow'>
                <ul className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                    {banners.map((banner, index) => (
                        <li
                            key={banner.bannerNum}
                            draggable
                            onDragStart={(e) => handleDragStart(e, index)}
                            onDragEnter={(e) => handleDragEnter(e, index)}
                            onDragOver={(e) => e.preventDefault()}
                            onDrop={handleDrop}
                            className='relative group flex flex-col items-center p-3 bg-gray-100 rounded-lg border border-gray-300 hover:shadow-lg transition-shadow duration-300 
                            ease-in-out cursor-grab active:cursor-grabbing'
                        >
                            <div className="w-full aspect-[4/3] flex items-center justify-center overflow-hidden rounded-md bg-white">
                                <img
                                    src={getBannerImage(banner.imagePath)}
                                    alt={banner.originalName}
                                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300 ease-in-out"
                                />
                            </div>

                            <div className="w-full mt-3 text-center">
                                <p className="text-base font-medium truncate">{banner.originalName}</p>
                                <span className="text-sm text-gray-500">순서: {banner.sequence}</span>
                            </div>

                            <button
                                onClick={() => handleDelete(banner.bannerNum)}
                                className='absolute top-2 right-2 p-1 w-[28px] h-[28px] rounded-full bg-white border hover:bg-red-100 hover:border-red-300 active:bg-red-200 opacity-0 group-hover:opacity-100 transition-opacity duration-200 ease-in-out'
                                aria-label="배너 삭제"
                            >
                                <img src={cancel} className='w-full h-full object-contain' />
                            </button>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
};

export default AdminBannerComponent;