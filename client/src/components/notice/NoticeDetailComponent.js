import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { NoticeDetail } from "../../api/noticeApi";
import useMove from "../../hooks/useMove";
import axios from "axios";

const NoticeDetailComponent = () => {
    const { noticeNum } = useParams();
    const { moveToPath } = useMove();
    const navigate = useNavigate();
    
    const [notice, setNotice] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

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
        moveToPath("/notice/list");
    };
    //수정
    const handleUpdate = (e) => {
        navigate(`/notice/update/${noticeNum}`);
    };
    //삭제
    const handleDelete = async () => {
        if (window.confirm("정말 삭제하시겠습니까?")) {
            try {
                await axios.delete(`/api/notice/${noticeNum}`);
                alert("삭제되었습니다.");
                navigate("/notice/list");
            } catch (err) {
                console.error("삭제 실패", err);
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
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    };
    //로딩중
    if (loading) {
        return (
            <div>
                <div>
                    <div></div>
                    <span>로딩 중...</span>
                </div>
            </div>
        );
    }
    //에러 발생
    if (error) {
        return (
            <div>
                <div>
                    <p>{error}</p>
                    <button onClick={handleList}>목록으로 돌아가기</button>
                </div>
            </div>
        );
    }
    //데이터 없을 때
    if (!notice) {
        return (
            <div>
                <div>
                    <p>공지사항을 찾을 수 없습니다.</p>
                    <button onClick={handleList}>목록으로 돌아가기</button>
                </div>
            </div>
        );
    }

    return (
        <div>
            {/* 공지사항 상세 페이지 */}
            <div>
                {/* 제목 */}
                <div>
                    <h2>{notice.title || '제목없음'}</h2>
                    {notice.isPinned && (
                        <span>공지</span>
                    )}
                </div>
                {/* 정보 */}
                <div>
                    <div>
                        <div>
                            <span>작성자:</span>
                            <span>{notice.name || '-'}</span>
                        </div>
                        <div>
                            <span>작성일:</span>
                            <span>{formatDate(notice.crestedAt)}</span>
                        </div>
                        <div>
                            <span>조회수:</span>
                            <span>{notice.viewCount || 0}</span>
                        </div>
                    </div>
                </div>
                {/* 첨부파일 */}
                {notice.files && notice.files.length > 0 && (
                    <div className="px-6 py-4 border-b border-gray-200 bg-gray-50">
                        <div className="flex items-start">
                            <span className="text-sm font-medium text-gray-700 w-20 flex-shrink-0">첨부파일:</span>
                            <div className="flex-1">
                                {notice.files.map((file, index) => (
                                    <div key={index} className="flex items-center mb-1">
                                        <svg className="w-4 h-4 text-gray-400 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13" />
                                        </svg>
                                       <a
                                            href={file.downloadUrl}
                                            download
                                            target="_blank"
                                            rel="noopener noreferrer"
                                            className="text-sm text-blue-600 hover:text-blue-800 hover:underline"
                                        >
                                            {file.originalName}
                                        </a>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>
                )}
                {/* 내용 */}
                <div>
                    <div>
                        <div>
                            {notice.content || '내용이 없습니다.'}
                        </div>
                    </div>
                </div>
                {/* 버튼 */}
                <div className="mt-6 flex justify-between">
                    <button 
                        onClick={handleList}
                        className="px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600 transition-colors"
                    >
                        목록
                    </button>
                
                    <div className="flex gap-2">
                        <button 
                            onClick={handleUpdate}
                            className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors"
                        >
                            수정하기
                        </button>
                        <button 
                            onClick={handleDelete}
                            className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600 transition-colors"
                        >
                            삭제하기
                        </button>
                    </div>
                </div>                
            </div>
            
        </div>
    )


}

export default NoticeDetailComponent;