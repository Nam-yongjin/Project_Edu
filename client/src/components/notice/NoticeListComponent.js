import React from "react";
import { Link } from "react-router-dom";
import { useSelector } from "react-redux";

const NoticeListComponent = ({ notices, selectedNotices, setSelectedNotices, isAdmin }) => {
  const loginState = useSelector((state) => state.loginState); //로그인 상태 확인
  // 체크박스 변경 처리
  const handleCheckboxChange = (noticeNum, isChecked) => { //공지글 선택
    if (isChecked) {
      setSelectedNotices(prev => [...prev, noticeNum]);
    } else {
      setSelectedNotices(prev => prev.filter(num => num !== noticeNum));
    }
  };

  // 전체 선택/해제
  const handleSelectAll = (isChecked) => {
    if (isChecked) {
      const allNoticeNums = notices.map(notice => notice.noticeNum);
      setSelectedNotices(allNoticeNums);
    } else {
      setSelectedNotices([]);
    }
  };

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

  // 고정글과 일반글 분리 및 정렬
  const pinnedNotices = notices.filter(notice => notice.isPinned);
  const normalNotices = notices.filter(notice => !notice.isPinned);

  const allNotices = [...pinnedNotices, ...normalNotices];

  return (
    <div className="overflow-x-auto">
      <table className="w-full">
        <thead className="bg-gray-50 border-b border-gray-200">
          <tr>
            {loginState.role === 'ADMIN' ? ( //관리자만 선택 가능
              <th className="px-4 py-3 text-left">
                <input
                  type="checkbox"
                  checked={selectedNotices.length === notices.length && notices.length > 0}
                  onChange={(e) => handleSelectAll(e.target.checked)}
                  className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                />
              </th>
            ) : (<></>)}
            <th className="px-4 py-3 text-center text-sm font-medium text-gray-900 w-20">번호</th>
            <th className="px-4 py-3 text-left text-sm font-medium text-gray-900">제목</th>
            <th className="px-4 py-3 text-center text-sm font-medium text-gray-900 w-24">작성자</th>
            <th className="px-4 py-3 text-center text-sm font-medium text-gray-900 w-28">작성일</th>
            <th className="px-4 py-3 text-center text-sm font-medium text-gray-900 w-20">조회수</th>
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-gray-200">
          {allNotices.length === 0 ? ( //공지글이 없을 때
            <tr>
              <td 
                colSpan={isAdmin ? 6 : 5} 
                className="px-4 py-12 text-center text-gray-500"
              >
                등록된 공지사항이 없습니다.
              </td>
            </tr>
          ) : (
            allNotices.map((notice, index) => (
              <tr 
                key={notice.noticeNum}
                className={`hover:bg-gray-50 ${notice.isPinned ? 'bg-yellow-50' : ''}`}
              >
                {loginState.role === 'ADMIN' ? (
                  <td className="px-4 py-3">
                    <input
                      type="checkbox"
                      checked={selectedNotices.includes(notice.noticeNum)}
                      onChange={(e) => handleCheckboxChange(notice.noticeNum, e.target.checked)}
                      className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                    />
                  </td>
                 ) : (<></>)}
                <td className="px-4 py-3 text-center text-sm text-gray-900">
                  {notice.isPinned ? (
                    <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-red-100 text-red-800">
                      공지
                    </span>
                  ) : (
                    notice.noticeNum
                  )}
                </td>
                <td className="px-4 py-3">
                  <div className="flex items-center">
                    <Link //제목 누르면 상세페이지로 넘어감
                      to={`/notice/${notice.noticeNum}`}
                      className="text-sm font-medium text-gray-900 hover:text-blue-600 transition-colors"
                    >
                      {notice.title}
                    </Link>
                    {notice.fileCount > 0 && (
                      <span className="ml-2 inline-flex items-center">
                        <svg 
                          className="w-4 h-4 text-gray-400" 
                          fill="none" 
                          stroke="currentColor" 
                          viewBox="0 0 24 24"
                        >
                          <path 
                            strokeLinecap="round" 
                            strokeLinejoin="round" 
                            strokeWidth={2} 
                            d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13" 
                          />
                        </svg>
                      </span>
                    )}
                    {notice.isPinned && (
                      <span className="ml-2 inline-flex items-center">
                        <svg 
                          className="w-4 h-4 text-red-500" 
                          fill="currentColor" 
                          viewBox="0 0 20 20"
                        >
                          <path 
                            fillRule="evenodd" 
                            d="M3 6a3 3 0 013-3h10a1 1 0 01.8 1.6L14.25 8l2.55 3.4A1 1 0 0116 13H6a1 1 0 00-1 1v3a1 1 0 11-2 0V6z" 
                            clipRule="evenodd" 
                          />
                        </svg>
                      </span>
                    )}
                  </div>
                </td>
                <td className="px-4 py-3 text-center text-sm text-gray-500">
                  {notice.name}
                </td>
                <td className="px-4 py-3 text-center text-sm text-gray-500">
                  {formatDate(notice.createdAt)}
                </td>
                <td className="px-4 py-3 text-center text-sm text-gray-500">
                  {notice.viewCount?.toLocaleString() || 0}
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
};

export default NoticeListComponent;