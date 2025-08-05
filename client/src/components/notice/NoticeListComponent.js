import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { useSelector } from "react-redux";
import { NoticeList } from "../../api/noticeApi";
import NoticeSearchComponent from "./NoticeSearchComponent";
import NoticeButtonComponent from "./NoticeButtonsComponent";
//공지 데이터를 서버에서 받아와서 화면에 보여주고 검색, 삭제 기능 담당
const NoticeListComponent = () => {
  const loginState = useSelector((state) => state.loginState); //로그인 상태 확인

  //상태변수들
  //const [상태 변수, 상태 변경 함수] = useState(초기값);
  const [notices, setNotices] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [selectedNotices, setSelectedNotices] = useState([]);

  const [searchParams, setSearchParams] = useState({
    page: 0,
    size: 10,
    keyword: "",
    searchType: "ALL",
    startDate: "",
    endDate: "",
    isPinned: null,
    sortBy: "createdAt",
    sortDirection: "DESC"
  });

  //공지사항 목록 조회
  const fetchNotices = async (params = searchParams) => {
    setLoading(true);
    try{
      const response = await NoticeList(params); //컨트롤러에 전달되는 매개변수
      setNotices(response.content || []);
      setTotalPages(response.totalPages || 0);
      setTotalElements(response.totalElements || 0);
      setCurrentPage(response.number || 0);
    } catch (error) {
      console.error("공지사항 목록 조회 실패:", error);
      setNotices([]);
    } finally {
      setLoading(false);
    }
  };

  //처음 페이지 들어오면 초기 데이터를 나타냄
  useEffect(() => {
    fetchNotices();
  }, []);

  //검색 처리
  const handleSearch = (newSearchParams) => {
    const updatedParams = {
      ...newSearchParams,
      page: 0 //검색 시 첫 페이지로
    };
    setSearchParams(updatedParams);
    fetchNotices(updatedParams);
    setSelectedNotices([]); //선택 초기화
  };

  //페이지 변경
  const handlePageChange = (page) => {
    const updatedParams = {
      ...searchParams,
      page: page
    };
    setSearchParams(updatedParams);
    fetchNotices(updatedParams);
    setSelectedNotices([]); //페이지 변경 시 선택 초기화
  };

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

  //페이지네이션
  const renderPagination = () => {
    if (totalPages <= 1) return null;

    const pages = [];
    const startPage = Math.max(0, currentPage - 2); //첫 페이지
    const endPage = Math.min(totalPages - 1, currentPage + 2); //마지막 페이지

    //이전 버튼
    if (currentPage > 0) {
      pages.push(
        <button
          key="prev"
          onClick={() => handlePageChange(currentPage - 1)}
          className="px-3 py-1 mx-1 bg-gray-200 text-gray-700 rounded hover:bg-gray-300"
        > 이전 </button>
      );
    }

    //페이지 번호
    for (let i = startPage; i  <= endPage; i++)  {
      pages.push(
        <button
          key={i}
          onClick={() => handlePageChange(i)}
          className={`px-3 py-1 mx-1 rounded ${
            currentPage === i
              ? "bg-blue-500 text-white"
              : "bg-gray-200 text-gray-700 hover:bg-gray-300"
          }`}
          >
            {i + 1}
          </button>
      );
    }

    //다음 버튼
    if (currentPage < totalPages - 1) {
      pages.push(
        <button
          key="next"
          onClick={() => handlePageChange(currentPage + 1)}
          className="px-3 py-1 mx-1 bg-gray-200 text-gray-700 rounded hover:bg-gray-300"
        > 다음 </button>
      );
    }

    return (
      <div className="flex justify-center items-center mt-6">
        {pages}
      </div>
    );
  };

  // 고정글과 일반글 분리 및 정렬
  const pinnedNotices = notices.filter(notice => notice.isPinned);
  const normalNotices = notices.filter(notice => !notice.isPinned);
  const allNotices = [...pinnedNotices, ...normalNotices];

  return (
    <div className="w-full px-4 mt-10 sm:px-6 md:px-8 lg:px-12 max-w-screen-xl mx-auto">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-gray-900">공지사항</h1>
        <p className="text-gray-600 mt-1">
          전체 {totalElements}건의 공지사항이 있습니다.
        </p>
      </div>

      {/* 검색 컴포넌트 */}
      <NoticeSearchComponent
        onSearch={handleSearch}
        initialValues={searchParams}
      />

      {/* 공지사항 테이블 */}
      <div className="bg-white shadow-sm border border-gray-200 rounded-lg overflow-hidden">
        {loading ? (
          <div className="flex justify-center items-center py-12">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
            <span className="ml-2 text-gray-600">로딩중...</span>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full table-fixed">
              <thead className="bg-gray-50 border-b border-gray-200">
                <tr>
                  {loginState.role === 'ADMIN' && ( //관리자만 선택 가능
                    <th className="w-12 px-2 py-3 text-center align-middle">
                      <input
                        type="checkbox"
                        checked={selectedNotices.length === notices.length && notices.length > 0}
                        onChange={(e) => handleSelectAll(e.target.checked)}
                        className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                      />
                    </th>
                  )}
                  <th className="w-16 px-2 py-3 text-center text-sm font-medium text-gray-900">번호</th>
                  <th className="px-3 py-3 text-left text-sm font-medium text-gray-900">제목</th>
                  <th className="w-28 px-3 py-3 text-center text-sm font-medium text-gray-900">작성자</th>
                  <th className="w-36 px-3 py-3 text-center text-sm font-medium text-gray-900">작성일</th>
                  <th className="w-24 px-3 py-3 text-center text-sm font-medium text-gray-900">조회수</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {allNotices.length === 0 ? ( //공지사항이 없을 때
                  <tr>
                    <td 
                      colSpan={loginState.role === 'ADMIN' ? 6 : 5} 
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
                      {loginState.role === 'ADMIN' && (
                        <td className="px-4 py-3">
                          <input
                            type="checkbox"
                            checked={selectedNotices.includes(notice.noticeNum)}
                            onChange={(e) => handleCheckboxChange(notice.noticeNum, e.target.checked)}
                            className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                          />
                        </td>
                      )}
                      <td className="px-4 py-3 text-center text-sm text-gray-900">
                        {notice.isPinned ? (
                          <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-red-100 text-red-800">
                            공지
                          </span>
                        ) : (
                          //일반글 역순 번호 계산
                          //전체 일반글 개수 - (현재 페이지 × 페이지 크기) - 현재 일반글 인덱스
                          totalElements - pinnedNotices.length - (currentPage * searchParams.size) - (index - pinnedNotices.length)
                        )}
                      </td>
                      <td className="px-4 py-3">
                        <div className="flex items-center">
                          <Link //제목 누르면 상세페이지로 넘어감
                            to={`/notice/detail/${notice.noticeNum}`}
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
        )}
      </div>

      {/* 페이지네이션 */}
      {renderPagination()}

      {/* 하단 버튼 */}
      <div className="mb-8">
        <NoticeButtonComponent
          isAdmin={loginState.role === 'ADMIN'}
          selectedNotices={selectedNotices}
          onDelete={() => {
            // 삭제 후 목록 새로고침
            fetchNotices();
            setSelectedNotices([]);
          }}
        />
      </div>
  </div>
  );
};

export default NoticeListComponent;