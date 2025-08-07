import { useState, useEffect, useRef } from 'react';
import { getAllBanners, uploadBanners, deleteBanner, updateBannerSequence, getBannerImage } from '../../api/adminApi';

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
                console.error('배너 목록을 불러오는 데 실패했습니다.', error);
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
                alert('업로드 실패');
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
                    alert('삭제 실패');
                });
        }
    };

    const handleDragStart = (e, index) => {
        dragItem.current = index;
    };

    const handleDragEnter = (e, index) => {
        dragOverItem.current = index;
    };

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
            })
            .catch(error => {
                alert('순서 업데이트에 실패했습니다.');
            });
    };

    return (
        <div>
            <h2>배너 관리</h2>
            <div className="upload-section">
                <input type="file" multiple onChange={handleFileChange} />
                <button onClick={handleUpload}>업로드</button>
            </div>

            <hr />

            <ul className="banner-list">
                {banners.map((banner, index) => (
                    <li
                        key={banner.bannerNum}
                        draggable
                        onDragStart={(e) => handleDragStart(e, index)}
                        onDragEnter={(e) => handleDragEnter(e, index)}
                        onDragOver={(e) => e.preventDefault()}
                        onDrop={handleDrop}
                    >
                        <img src={getBannerImage(banner.imagePath)} alt={banner.originalName} style={{ width: '100px' }} />
                        <span>{banner.originalName} (순서: {banner.sequence})</span>
                        <button onClick={() => handleDelete(banner.bannerNum)}>삭제</button>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default AdminBannerComponent;