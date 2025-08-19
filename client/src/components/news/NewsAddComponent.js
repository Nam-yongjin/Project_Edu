import { useState } from "react";
import useMove from "../../hooks/useMove";
import { createNews } from "../../api/newsApi";

const NewsAddComponent = () => {
    const { moveToPath } = useMove();
    
    // 초기값 객체
    const initState = {
        title: '',
        content: '',
        imageUrl: '',
        linkUrl: ''
    };

    const [news, setNews] = useState(initState);
    
    // 에러 객체
    const [errors, setErrors] = useState({
        title: '',
        content: '',
        imageUrl: '',
        linkUrl: ''
    });

    // 이미지 로드 상태 관리
    const [imageLoadError, setImageLoadError] = useState(false);
    const [isImageLoading, setIsImageLoading] = useState(false);

    const getByteLength = (str) => new Blob([str]).size;
    
    // 내용 작성칸 자동 조절
    const autoTextareaHeight = (e) => {
        e.target.style.height = 'auto';
        e.target.style.height = e.target.scrollHeight + 'px';
    };

    // URL 유효성 검사
    const isValidUrl = (string) => {
        try {
            new URL(string);
            return true;
        } catch (_) {
            return false;
        }
    };

    // 이미지 URL 유효성 검사
    const isValidImageUrl = (url) => {
        if (!isValidUrl(url)) return false;
        const imageExtensions = ['.jpg', '.jpeg', '.png'];
        const urlLower = url.toLowerCase();
        return imageExtensions.some(ext => urlLower.includes(ext)) || 
               urlLower.includes('image') || 
               urlLower.includes('img');
    };

    const validateForm = () => {
        const newErrors = {};
    
        // 제목 검사
        if (!news.title.trim()) {
            newErrors.title = "제목을 입력하세요.";
        } else if (news.title.length > 100) {
            newErrors.title = "제목은 최대 100자까지 입력 가능합니다.";
        }
    
        // 내용 검사
        if (!news.content.trim()) {
            newErrors.content = "내용을 입력하세요.";
        } else if (getByteLength(news.content) > 65535) {
            newErrors.content = "내용이 너무 깁니다. 글자 수를 줄여주세요.";
        }

        // 이미지 URL 검사
        if (news.imageUrl && !isValidImageUrl(news.imageUrl)) {
            newErrors.imageUrl = "올바른 이미지 URL을 입력하세요.";
        }

        // 링크 URL 검사
        if (news.linkUrl && !isValidUrl(news.linkUrl)) {
            newErrors.linkUrl = "올바른 URL을 입력하세요.";
        }
    
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    // 이미지 URL 변경 처리
    const handleImageUrlChange = (e) => {
        const url = e.target.value;
        setNews({ ...news, imageUrl: url });
        setImageLoadError(false);
        
        if (url && isValidImageUrl(url)) {
            setIsImageLoading(true);
        }
    };

    // 이미지 로드 성공
    const handleImageLoad = () => {
        setIsImageLoading(false);
        setImageLoadError(false);
    };

    // 이미지 로드 실패
    const handleImageError = () => {
        setIsImageLoading(false);
        setImageLoadError(true);
    };

    // 등록 버튼
    const handleAdd = async () => {
        if (!validateForm()) {
            return;
    }

    try {
        const newsData = {
            title: news.title,
            content: news.content,
            imageUrl: news.imageUrl || null,
            linkUrl: news.linkUrl || null
        };

        await createNews(newsData); // 그냥 객체로 전송

        alert("뉴스가 등록되었습니다.");
        moveToPath("/news/NewsList");

    } catch (error) {
        console.error("뉴스 등록 실패:", error);
        // ... 에러 처리
    }
};

    // 취소 버튼
    const handleCancel = () => {
        if (window.confirm("작성을 취소하고 목록으로 돌아가시겠습니까?")) {
            moveToPath("/news/NewsList");
        }
    };

    return (
        <div className="max-w-screen-xl mx-auto my-10">
            <div className="min-blank mt-4 px-10 p-6 bg-white page-shadow space-y-6">
                <h2 className="newText-2xl my-4 font-bold">뉴스 등록</h2>
                <hr className="border-gray-200 my-4" />
                
                {/* 제목 */}
                <div>
                    <label className="block font-medium mb-1">제목</label>
                    <input
                        type="text"
                        value={news.title}
                        onChange={(e) => setNews({ ...news, title: e.target.value })}
                        placeholder="제목을 입력하세요"
                        className="w-full input-focus"
                    />
                    {errors.title && (
                        <p className="text-red-500 newText-sm mt-1">{errors.title}</p>
                    )}
                </div>

                {/* 이미지 URL */}
                <div>
                    <label className="block font-medium mb-1">썸네일 이미지 URL</label>
                    <input
                        type="url"
                        value={news.imageUrl}
                        onChange={handleImageUrlChange}
                        placeholder="https://example.com/image.jpg"
                        className="w-full input-focus"
                    />
                    {errors.imageUrl && (
                        <p className="text-red-500 newText-sm mt-1">{errors.imageUrl}</p>
                    )}
                    <p className="text-gray-500 text-xs mt-1">
                        이미지 파일의 직접 링크를 입력하세요 (.jpg, .jpeg, .png 등)
                    </p>
                </div>

                {/* 외부 링크 URL */}
                <div>
                    <label className="block font-medium mb-1">외부 기사 링크</label>
                    <input
                        type="url"
                        value={news.linkUrl}
                        onChange={(e) => setNews({ ...news, linkUrl: e.target.value })}
                        placeholder="https://example.com/news/article"
                        className="w-full input-focus"
                    />
                    {errors.linkUrl && (
                        <p className="text-red-500 newText-sm mt-1">{errors.linkUrl}</p>
                    )}
                </div>

                {/* 내용 + 이미지 미리보기 */}
                <div>
                    <label className="block font-medium mb-1">내용</label>
                    <div className="border border-gray-300 rounded focus-within:ring-2 focus-within:ring-blue-400">
                        {/* 이미지 미리보기 */}
                        {news.imageUrl && (
                            <div className="p-4 border-b border-gray-200 bg-gray-50">
                                {isImageLoading && (
                                    <div className="flex items-center justify-center h-48 bg-gray-100 rounded">
                                        <div className="text-gray-500">이미지 로딩 중...</div>
                                    </div>
                                )}
                                {!imageLoadError && !isImageLoading && (
                                    <div className="relative">
                                        <img
                                            src={news.imageUrl}
                                            alt="뉴스 썸네일"
                                            onLoad={handleImageLoad}
                                            onError={handleImageError}
                                            className="w-full max-w-md mx-auto h-auto object-contain rounded shadow-sm"
                                            style={{ maxHeight: '300px' }}
                                        />
                                    </div>
                                )}
                                {imageLoadError && (
                                    <div className="flex items-center justify-center h-48 bg-gray-100 rounded border-2 border-dashed border-gray-300">
                                        <div className="text-center text-gray-500">
                                            <div className="newText-4xl mb-2">🖼️</div>
                                            <div>이미지를 불러올 수 없습니다</div>
                                            <div className="newText-sm">URL을 확인해주세요</div>
                                        </div>
                                    </div>
                                )}
                            </div>
                        )}
                        
                        {/* 내용 입력 */}
                        <textarea
                            value={news.content}
                            onChange={(e) => {
                                setNews({ ...news, content: e.target.value });
                                autoTextareaHeight(e);
                            }}
                            placeholder="뉴스 내용을 입력하세요"
                            rows={8}
                            className="w-full px-3 py-2 border-none focus:outline-none resize-none"
                            style={{ minHeight: '200px' }}
                        />
                    </div>
                    {errors.content && (
                        <p className="text-red-500 newText-sm mt-1">{errors.content}</p>
                    )}
                </div>

                {/* 버튼 */}
                <div className="flex justify-end space-x-4">
                    <button
                        className="positive-button newText-sm"
                        style={{ minWidth: "60px" }}
                        onClick={handleAdd}
                    >
                        등록
                    </button>
                    <button
                        className="nagative-button newText-sm"
                        style={{ minWidth: "60px" }}
                        onClick={handleCancel}
                    >
                        취소
                    </button>
                </div>
            </div>
        </div>
    );
};

export default NewsAddComponent;