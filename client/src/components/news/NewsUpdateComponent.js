import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import useMove from "../../hooks/useMove";
import { updateNews, NewsDetail } from "../../api/newsApi";

const NewsUpdateComponent = () => {
    const { newsNum } = useParams(); // url에서 뉴스 번호 가져오기
    const { moveToPath } = useMove();
    const navigate = useNavigate(); // 페이지 이동

    // 초기값 객체
    const initState = {
        title: '',
        content: '',
        imageUrl: '', // 이미지url
        linkUrl: '' // 외부 기사 링크
    };

    const [news, setNews] = useState(initState);
    const [isLoading, setIsLoading] = useState(true);

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

    // 뉴스 데이터 불러오기
    useEffect(() => {
        console.log("뉴스 데이터 불러오기", {newsNum});

        const fetchNewsUpdate = async () => {
            if (!newsNum) {
                alert("뉴스 번호가 없습니다.");
                moveToPath("/news/NewsList");
                return;
            }

            try {
                setIsLoading(true);
                console.log("API 호출 시작 - newsNum:", newsNum);

                const response = await NewsDetail(Number(newsNum));
                console.log("API 응답:", response);

                const newState = {
                    ...initState,
                    title: response.title || '',
                    content: response.content || '',
                    imageUrl: response.imageUrl || '',
                    linkUrl: response.linkUrl || ''
                };

                console.log("뉴스 데이터 불러오기:", newState);
                setNews(newState);

            } catch (error) {
                console.log("뉴스 로드 실패:", error);
                alert("뉴스를 불러오는데 실패했습니다.");
                moveToPath("/news/NewsList");
            } finally {
                setIsLoading(false);
                console.log("로딩 완료!!!")
            }
        };

        fetchNewsUpdate();
    }, [newsNum]);

    const getByteLength = (str) => new Blob([str]).size;

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

    // 이미지 URL변경 처리
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

    // 수정 버튼
    const handleUpdate = async () => {
        // 유효성 검사
        if (!validateForm()) {
            return;
        }

        try {
            const formData = new FormData(); // @RequestPart("dto")로 받아야 함 --> FormData 사용

            const dto = { // dto객체를 JSON으로 변환 후 Blob로 감싸서 formData에 추가
                title: news.title || "",
                content: news.content || "",
                imageUrl: news.imageUrl || null,
                linkUrl: news.linkUrl || null
            };
            const dtoBlob = new Blob([JSON.stringify(dto)], { type: "application/json" });
            formData.append("dto", dtoBlob);

            // 전송
            await updateNews(newsNum, formData);

            alert("뉴스가 수정되었습니다.")
            navigate(`/news/NewsDetail/${Number(newsNum)}`);
        } catch (error) {
            console.error("뉴스 수정 실패:", error);

            // 백엔드 유효성 에러
            if (error.response?.status === 400 && error.response?.data?.errors) {
                const backendErrors = {};
                error.response.data.errors.forEach(err => {
                    backendErrors[err.field] = err.defaultMessage;
                });
                setErrors(backendErrors);
            } else {
                alert("수정 중 오류가 발생했습니다.");
            }
        }
    };

    // 취소 버튼
    const handleCancel = () => {
        if (window.confirm("수정을 취소하고 목록으로 돌아가시겠습니까?")) {
            moveToPath("/news/NewsList");
        }
    };

    if (isLoading) {
        return (
            <div className="w-full px-4 sm:px-6 md:px-8 lg:px-12 max-w-screen-xl mx-auto">
                <div className="max-w-4xl mx-auto mt-4 px-10 p-6 bg-white rounded-xl shadow-md">
                    <div className="text-center py-8 newText-base">로딩 중...</div>
                </div>
            </div>
        );
    }

    return (
        <div className="max-w-screen-xl mx-auto my-10">
            <div className="min-blank my-4 px-10 p-6 bg-white page-shadow space-y-6">
                <h2 className="newText-2xl my-4 font-bold">뉴스 수정</h2>
                <hr className="border-gray-200 my-4" />

                {/* 제목 */}
                <div>
                    <label className="block font-medium mb-1 newText-base">제목</label>
                    <input
                        type="text"
                        value={news.title}
                        onChange={(e) => setNews({ ...news, title: e.target.value })}
                        placeholder="제목을 입력하세요"
                        className="w-full input-focus newText-base"
                    />
                    {errors.title && (
                        <p className="text-red-500 newText-sm mt-1">{errors.title}</p>
                    )}
                </div>

                {/* 이미지 URL */}
                <div>
                    <label className="block font-medium mb-1 newText-base">썸네일 이미지 URL</label>
                    <input
                        type="url"
                        value={news.imageUrl}
                        onChange={handleImageUrlChange}
                        placeholder="https://example.com/image.jpg"
                        className="w-full input-focus newText-base"
                    />
                    {errors.imageUrl && (
                        <p className="text-red-500 newText-sm mt-1">{errors.imageUrl}</p>
                    )}
                    <p className="text-gray-500 newText-xs mt-1">
                        이미지 파일의 직접 링크를 입력하세요(.jpg, .jpeg, .png 등)
                    </p>
                </div>

                {/* 외부 링크 URL */}
                <div>
                    <label className="block font-medium mb-1 newText-base">외부 기사 링크</label>
                    <input
                        type="url"
                        value={news.linkUrl}
                        onChange={(e) => setNews({ ...news, linkUrl: e.target.value })}
                        placeholder="https://example.com/news/article"
                        className="w-full input-focus newText-base"
                    />
                    {errors.linkUrl && (
                        <p className="text-red-500 newText-sm mt-1">{errors.linkUrl}</p>
                    )}
                </div>

                {/* 내용 + 이미지 미리보기 */}
                <div>
                    <label className="block font-medium mb-1 newText-base">내용</label>
                        {/* 이미지 미리보기 */}
                        {news.imageUrl && (
                            <div className="p-4 border border-gray-300 rounded bg-gray-50 mb-2">
                                {isImageLoading && (
                                    <div className="flex items-center justify-center h-48 bg-gray-100 rounded">
                                        <div className="text-gray-500 newText-base">이미지 로딩 중...</div>
                                    </div>
                                )}
                                {!imageLoadError && !isImageLoading && (
                                    <div className="relative">
                                        <img
                                            src={news.imageUrl}
                                            alt="뉴스 썸네일"
                                            onLoad={handleImageLoad}
                                            onError={handleImageError}
                                            className="page-shadow newText-base"
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

                        {/* 내용 */}
                        <textarea
                            value={news.content}
                            onChange={(e) => setNews({ ...news, content: e.target.value })}
                            placeholder="뉴스 내용을 입력하세요"
                            className="w-full input-focus newText-base"
                            style={{ minHeight: "300px", resize: "none", overflowY: "auto" }} 
                        />
                    {errors.content && (
                        <p className="text-red-500 newText-sm mt-1">{errors.content}</p>
                    )}
                </div>

                <div className="flex justify-end space-x-4">
                    <button
                        className="green-button newText-sm"
                        style={{ minWidth: "60px" }}
                        onClick={handleUpdate}>수정</button>
                    <button
                        className="nagative-button newText-sm"
                        style={{ minWidth: "60px" }}
                        onClick={handleCancel}>취소</button>
                </div>
            </div>
        </div>
    );
};

export default NewsUpdateComponent;