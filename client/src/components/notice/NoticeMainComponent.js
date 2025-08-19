import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { NoticeList } from "../../api/noticeApi";

const NoticeMainComponent = () => {
  const [notices, setNotices] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchNotices = async () => {
      setLoading(true);
      try {
        const data = await NoticeList({
          page: 0,
          size: 4,
          keyword: "",
          searchType: "ALL",
          sortBy: "createdDate",
          sortDirection: "DESC",
        });
        setNotices(data.content || []);
      } catch (err) {
        console.error("공지사항 불러오기 실패:", err);
        setNotices([]);
      } finally {
        setLoading(false);
      }
    };

    fetchNotices();
  }, []);

  // 날짜 포맷팅(현재 날짜 기준)
  const formatDate = (dateString) => {
    if (!dateString) return "-";
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    });
  };

  // 제목 길이 제한 함수
  const truncateTitle = (title, maxLength = 30) => {
    return title.length > maxLength ? title.substring(0, maxLength) + '...' : title;
  };

  if (loading) {
    return (
      <div className="w-full bg-white rounded-lg shadow-lg border border-gray-200 overflow-hidden">
        <div className="flex justify-center items-center py-12">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
          <span className="ml-2 text-gray-600">로딩중...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="w-full bg-white rounded-lg shadow-lg border border-gray-200 overflow-hidden">
      {/* 헤더 */}
      <div className="px-6 py-4 border-b border-gray-200">
        <div className="flex items-center justify-between">
          <h2 className="newText-xl font-bold text-gray-900">공지사항</h2>
          <Link 
            to="/notice/NoticeList"
            className="text-gray-600 hover:text-gray-800 text-sm transition-colors duration-200 flex items-center font-medium"
          >
            더보기
            <svg className="w-4 h-4 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
            </svg>
          </Link>
        </div>
      </div>

      {/* 내용 */}
      <div className="p-6">
        {notices.length === 0 ? (
          <div className="text-center py-8 text-gray-500">
            <svg className="w-12 h-12 mx-auto text-gray-300 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9.5a2 2 0 00-2-2h-2m-2-1V6a2 2 0 00-2-2H5a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-5" />
            </svg>
            등록된 공지사항이 없습니다.
          </div>
        ) : (
          <ul className="space-y-3">
            {notices.map((notice, index) => (
              <li 
                key={notice.noticeNum}
                className="group hover:bg-gray-50 rounded-lg p-3 transition-all duration-200 border-l-4 border-transparent hover:border-blue-500"
              >
                <div className="flex items-start justify-between">
                  <div className="flex items-start flex-1 min-w-0">
                    {/* 번호 또는 고정글 표시 */}
                    <div className="flex-shrink-0 mr-3 mt-1">
                      {notice.isPinned ? (
                        <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-red-100 text-red-800 border border-red-200">
                          <svg className="w-3 h-3 mr-1" fill="currentColor" viewBox="0 0 20 20">
                            <path fillRule="evenodd" d="M3 6a3 3 0 013-3h10a1 1 0 01.8 1.6L14.25 8l2.55 3.4A1 1 0 0116 13H6a1 1 0 00-1 1v3a1 1 0 11-2 0V6z" clipRule="evenodd" />
                          </svg>
                          공지
                        </span>
                      ) : (
                        <></>
                      )}
                    </div>
                    
                    {/* 제목과 메타 정보 */}
                    <div className="flex-1 min-w-0">
                      <Link 
                        to={`/notice/NoticeDetail/${notice.noticeNum}`}
                        className="block group-hover:text-blue-600 transition-colors duration-200"
                      >
                        <div className="flex items-center">
                          <h3 className="newText-sm font-medium text-gray-900 truncate group-hover:text-blue-600">
                            {truncateTitle(notice.title)}
                          </h3>
                          
                          {/* New 표시 (최근 3일 이내) */}
                          {(() => {
                            const createdDate = new Date(notice.createdAt);
                            const daysDiff = Math.floor((new Date() - createdDate) / (1000 * 60 * 60 * 24));
                            return daysDiff <= 3 && (
                              <span className="ml-2 text-xs font-bold text-red-500">
                                New !
                              </span>
                            );
                          })()}
                          
                          {/* 첨부파일 아이콘 */}
                          {notice.fileCount > 0 && (
                            <div className="flex-shrink-0 ml-2" title="첨부파일">
                              <svg className="w-4 h-4 text-gray-400 group-hover:text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13" />
                              </svg>
                            </div>
                          )}
                        </div>
                        
                        {/* 작성자 및 조회수 */}
                        <div className="flex items-center mt-1 text-xs text-gray-500">
                          <span>{notice.name}</span>
                          {notice.viewCount !== undefined && (
                            <>
                              <span className="mx-1">·</span>
                              <span>조회 {notice.viewCount.toLocaleString()}</span>
                            </>
                          )}
                        </div>
                      </Link>
                    </div>
                  </div>

                  {/* 날짜 */}
                  <div className="flex-shrink-0 ml-4">
                    <time className="text-xs text-gray-500 font-medium">
                      {formatDate(notice.createdAt)}
                    </time>
                  </div>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
};

export default NoticeMainComponent;