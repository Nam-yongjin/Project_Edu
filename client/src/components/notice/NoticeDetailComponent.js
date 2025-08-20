import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { NoticeDetail } from "../../api/noticeApi";
import { deleteNotice } from "../../api/noticeApi";
import useMove from "../../hooks/useMove";
import { useSelector } from "react-redux"

const NoticeDetailComponent = () => {
    const { noticeNum } = useParams();
    const { moveToPath } = useMove();
    const navigate = useNavigate();

    const [notice, setNotice] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const loginState = useSelector((state) => state.loginState);

    //공지사항 상세 조회
    const fetchNoticeDetail = async () => {
        if (!noticeNum) {
            setError("공지사항 번호가 없습니다.");
            setLoading(false);
            return;
        }

        try {
            setLoading(true);
            console.log("API 호출 시작 - noticeNum:", noticeNum);

            const response = await NoticeDetail(Number(noticeNum));
            console.log("API 응답:", response);

            setNotice(response);
            setError(null);
        } catch (error) {
            console.error("공지사항 상세 조회 실패:", error);
            setError("공지사항을 불러오는데 실패했습니다.");
            setNotice(null);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        console.log("noticeNum:", noticeNum);
        fetchNoticeDetail();
    }, [noticeNum]);

    //목록
    const handleList = (e) => {
        e.preventDefault();
        moveToPath("/notice/NoticeList");
    };
    //수정
    const handleUpdate = (e) => {
        navigate(`/notice/UpdateNotice/${noticeNum}`);
    };
    //삭제
    const handleDelete = async () => {
        console.log("삭제 요청 보냄");

        if (window.confirm("정말 삭제하시겠습니까?")) {
            try {
                await deleteNotice(Number(noticeNum));
                console.log("삭제할 공지사항:", noticeNum);
                alert("공지사항이 삭제되었습니다.");

                moveToPath("/notice/NoticeList")
            } catch (error) {
                console.error("삭제 실패:", error);
                alert("삭제 중 오류가 발생했습니다.");
            }
        }
    };

    //현재 날짜 기준 
    const formatDate = (dateString) => {
        if (!dateString) return "-";
        const date = new Date(dateString);
        return date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit'
        });
    };
    //로딩중
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
    //에러 발생
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
    //데이터 없을 때
    if (!notice) {
        return (
            <div className="w-full px-4 sm:px-6 md:px-8 lg:px-12 max-w-screen-xl mx-auto">
                <div className="text-center py-12">
                    <p className="text-gray-600 mb-4 newText-base">공지사항을 찾을 수 없습니다.</p>
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
    //이미지 파일과 일반 파일 분리
    const imageFiles = notice.files?.filter(file =>
        file.originalName?.toLowerCase().match(/\.(jpg|jpeg|png|gif|webp)$/i)
    ) || [];

    const attachmentFiles = notice.files?.filter(file =>
        !file.originalName?.toLowerCase().match(/\.(jpg|jpeg|png|gif|webp)$/i)
    ) || [];

    return (
        <div className="max-w-screen-xl mx-auto my-10">
            {/* 공지사항 상세 페이지 */}
            <div className="min-blank bg-white page-shadow border border-gray-200 overflow-hidden">
                {/* 제목 */}
                <div className="px-6 py-8 border-b border-gray-200">
                    <div className="flex items-center gap-3 mb-2">
                        {notice.isPinned && (
                            <span className="inline-flex items-center px-3 py-1 rounded-full newText-sm font-medium bg-red-100 text-red-800 whitespace-nowrap">
                                공지
                            </span>
                        )}
                        <h1 className="newText-2xl font-bold text-gray-900">{notice.title || '제목없음'}</h1>
                    </div>

                    {/* 정보 */}
                    <div className="flex flex-wrap items-center gap-6 newText-sm text-gray-600 mt-4">
                        <div className="flex items-center gap-1">
                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                            </svg>
                            <span>작성자: {notice.name || '-'}</span>
                        </div>
                        <div className="flex items-center gap-1">
                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3a1 1 0 011-1h6a1 1 0 011 1v4M3 7h18l-1 10a2 2 0 01-2 2H6a2 2 0 01-2-2L3 7z" />
                            </svg>
                            <span>작성일: {formatDate(notice.createdAt)}</span>
                        </div>
                        {/* formatDate로 감싸야 날짜를 작성일이랑 수정일이 다를 때를 제대로 인식함 */}
                        {notice.updatedAt && formatDate(notice.updatedAt) !== formatDate(notice.createdAt) && (
                            <div className="flex items-center gap-1">
                                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                                </svg>
                                <span>수정일: {formatDate(notice.updatedAt)}</span>
                            </div>
                        )}
                        <div className="flex items-center gap-1">
                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                            </svg>
                            <span>조회수: {notice.viewCount || 0}</span>
                        </div>
                    </div>
                </div>

                {/* 내용 + 이미지 */}
                <div className="px-6">
                    {/* 내용 */}
                    <div className="py-6">
                        <div className="prose max-w-none text-gray-800 leanding-relaxed whitespace-pre-wrap newText-base">
                            {notice.content || '내용이 없습니다.'}
                        </div>
                    </div>

                    {/* 이미지 표시 */}
                    {imageFiles.length > 0 && (
                        <div className="pt-2 pb-8">
                            <div className="space-y-3">
                                {imageFiles.map((file, index) => (
                                    <div key={index}>
                                        <img
                                            src={`http://localhost:8090/api/notice/view/${file.savedName}`}
                                            alt={file.originalName}
                                            className="max-w-full h-auto mx-auto block"
                                            style={{
                                                width: 'auto',
                                                height: 'auto',
                                                maxWidth: '50%',
                                                maxHeight: '50%',
                                                display: 'block',
                                                margin: '0 auto',
                                                imageRendering: 'high-quality',
                                                imageRendering: '-webkit-optimize-contrast'
                                            }}
                                            loading="lazy"
                                            onError={(e) => {
                                                e.target.style.display = 'none';
                                                e.target.nextSibling.style.display = 'block';
                                            }}
                                        />
                                        <div
                                            className="hidden p-4 text-center text-gray-500 bg-gray-50 newText-base"
                                        >
                                            이미지를 불러올 수 없습니다: {file.originalName}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}
                </div>

                {/* 첨부파일 */}
                {attachmentFiles.length > 0 && (
                    <div className="px-6 py-4 border-t border-gray-200 bg-gray-50">
                        <div className="flex items-start">
                            <span className="newText-sm font-medium text-gray-700 w-20 flex-shrink-0">첨부파일:</span>
                            <div className="flex-1">
                                {attachmentFiles.map((file, index) => (
                                    <div key={index} className="flex items-center mb-2 last:mb-0">
                                        <svg className="w-4 h-4 text-gray-400 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13" />
                                        </svg>
                                        <a
                                            href={file.downloadUrl}
                                            download
                                            target="_blank"
                                            rel="noopener noreferrer"
                                            className="newText-sm text-blue-600 hover:text-blue-800 hover:underline"
                                        >
                                            {file.originalName}
                                        </a>
                                    </div>
                                ))}
                            </div>
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

                        {loginState.role === 'ADMIN' ? (
                            <div className="flex gap-2">
                                <button
                                    onClick={handleUpdate}
                                    className="green-button newText-sm"
                                >
                                    수정하기
                                </button>
                                <button
                                    onClick={handleDelete}
                                    className="nagative-button newText-sm"
                                >
                                    삭제하기
                                </button>
                            </div>
                        ) : (<></>)}
                    </div>
                </div>
            </div>
        </div>
    )
}

export default NoticeDetailComponent;