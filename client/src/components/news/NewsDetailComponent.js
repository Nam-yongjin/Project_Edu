import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { NewsDetail } from "../../api/newsApi";
import { deleteNews } from "../../api/newsApi";
import useMove from "../../hooks/useMove";
import { useSelector } from "react-redux"

const NewsDetailComponent = () => {
    const { newsNum } = useParams();
    const { moveToPath } = useMove();
    const navigate = useNavigate();

    const [news, setNews] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const loginState = useSelector((state) => state.loginState);

    // 뉴스 상세 조회
    const fetchNewsDetail = async () => {
        if (!newsNum) {
            setError("뉴스 번호가 없습니다.");
            setLoading(false);
            return;
        }

        try {
            setLoading(true);
            console.log("API 호출 시작 - newsNum:", newsNum);

            const response = await NewsDetail(Number(newsNum));
            console.log("API 응답:", response);

            setNews(response);
            setError(null);
        } catch (error) {
            console.error("뉴스 상세 조회 실패:", error);
            setError("뉴스를 불러오는데 실패했습니다.");
            setNews(null);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        console.log("newsNum:", newsNum);
        fetchNewsDetail();
    }, [newsNum]);

    // 목록
    const handleList = (e) => {
        e.preventDefault();
        moveToPath("/news/NewsList");
    };

    // 수정
    const handleUpdate = (e) => {
        navigate(`/news/UpdateNews/${newsNum}`);
    };

    // 삭제
    const handleDelete = async () => {
        console.log("삭제 요청 보냄");

        if (window.confirm("정말 삭제하시겠습니까?")) {
            try {
                await deleteNews(Number(newsNum));
                console.log("삭제할 뉴스:", newsNum);
                alert("뉴스가 삭제되었습니다.");

                moveToPath("/news/NewsList")
            } catch (error) {
                console.error("삭제 실패:", error);
                alert("삭제 중 오류가 발생했습니다.");
            }
        }
    };

    // 현재 날짜 기준 
    const formatDate = (dateString) => {
        if (!dateString) return "-";
        const date = new Date(dateString);
        return date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
        });
    };

    // 외부 링크로 이동
    const handleLinkClick = () => {
        if (news.linkUrl) {
            window.open(news.linkUrl, '_blank', 'noopener noreferrer');
        }
    };

    // 로딩중
    if (loading) {
        return (
            <div className="w-full px-4 sm:px-6 md:px-8 lg:px-12 max-w-screen-xl mx-auto">
                <div className="flex justify-center items-center py-12">
                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
                    <span className="ml-2 text-gray-600 newText-base">로딩 중...</span>
                </div>
            </div>
        );
    }

    // 에러 발생
    if (error) {
        return (
            <div className="w-full px-4 sm:px-6 md:px-8 lg:px-12 max-w-screen-xl mx-auto">
                <div className="text-center py-12">
                    <p className="text-red-600 mb-4">{error}</p>
                    <button
                        onClick={handleList}
                        className="dark-button newText-sm"
                    >
                        목록으로 돌아가기
                    </button>
                </div>
            </div>
        );
    }

    // 데이터 없을 때
    if (!news) {
        return (
            <div className="w-full px-4 sm:px-6 md:px-8 lg:px-12 max-w-screen-xl mx-auto">
                <div className="text-center py-12">
                    <p className="text-gray-600 mb-4 newText-base">뉴스를 찾을 수 없습니다.</p>
                    <button
                        onClick={handleList}
                        className="dark-button newText-sm"
                    >
                        목록으로 돌아가기
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="max-w-screen-xl mx-auto my-10">
            {/* 뉴스 상세 페이지 */}
            <div className="min-blank bg-white page-shadow border border-gray-200 overflow-hidden">
                {/* 제목 */}
                <div className="px-6 py-8 border-b border-gray-200">
                    <div className="flex items-center gap-3 mb-2">
                        <h1 className="newText-2xl font-bold text-gray-900">{news.title || '제목없음'}</h1>
                    </div>

                    {/* 정보 */}
                    <div className="flex flex-wrap items-center gap-6 newText-sm text-gray-600 mt-4">
                        <div className="flex items-center gap-1">
                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                            </svg>
                            <span>작성자: {news.name || '-'}</span>
                        </div>
                        <div className="flex items-center gap-1">
                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3a1 1 0 011-1h6a1 1 0 011 1v4M3 7h18l-1 10a2 2 0 01-2 2H6a2 2 0 01-2-2L3 7z" />
                            </svg>
                            <span>작성일: {formatDate(news.createdAt)}</span>
                        </div>
                        {news.updatedAt && formatDate(news.updatedAt) !== formatDate(news.createdAt) && (
                            <div className="flex items-center gap-1">
                                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                                </svg>
                                <span>수정일: {formatDate(news.updatedAt)}</span>
                            </div>
                        )}
                        <div className="flex items-center gap-1">
                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                            </svg>
                            <span>조회수: {news.viewCount || 0}</span>
                        </div>
                    </div>
                </div>

                {/* 썸네일 이미지 */}
                {news.imageUrl && (
                    <div className="px-6 pt-6">
                        <div className="mb-6">
                            <img
                                src={news.imageUrl}
                                alt={news.title || '뉴스 썸네일'}
                                className="w-full h-auto max-w-full"
                                style={{
                                    maxHeight: '400px',
                                    objectFit: 'contain',
                                    display: 'block',
                                    margin: '0 auto'
                                }}
                                loading="lazy"
                                onError={(e) => {
                                    e.target.style.display = 'none';
                                    e.target.nextSibling.style.display = 'block';
                                }}
                            />
                            <div
                                className="hidden p-4 text-center text-gray-500 bg-gray-50 rounded-lg newText-base"
                            >
                                이미지를 불러올 수 없습니다.
                            </div>
                        </div>
                    </div>
                )}

                {/* 내용 */}
                <div className="px-6 py-6">
                    <div className="prose max-w-none newText-base text-gray-800 leading-relaxed whitespace-pre-wrap">
                        {news.content || '내용이 없습니다.'}
                    </div>
                </div>

                {/* 외부 링크 */}
                {news.linkUrl && (
                    <div className="px-6 py-4 border-t border-gray-200 bg-blue-50">
                        <div className="flex items-center justify-between">
                            <div className="flex items-center">
                                <svg className="w-5 h-5 text-blue-500 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                                </svg>
                                <span className="newText-sm font-medium text-blue-700">원본 기사 보기</span>
                            </div>
                            <button
                                onClick={handleLinkClick}
                                className="px-4 py-2 bg-blue-500 text-white newText-sm rounded hover:bg-blue-600 transition-colors"
                            >
                                외부 링크로 이동
                            </button>
                        </div>
                    </div>
                )}

                {/* 버튼 */}
                <div className="px-6 py-4 border-t border-gray-200 bg-gray-50">
                    <div className="flex justify-between">
                        <button
                            onClick={handleList}
                            className="dark-button newText-sm"
                        >
                            목록
                        </button>

                        {loginState.role === 'ADMIN' && (
                            <div className="flex gap-2">
                                <button
                                    onClick={handleUpdate}
                                    className="green-button newText-sm"
                                >
                                    수정하기
                                </button>
                                <button
                                    onClick={handleDelete}
                                    className="negative-button newText-sm"
                                >
                                    삭제하기
                                </button>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    )
}

export default NewsDetailComponent;