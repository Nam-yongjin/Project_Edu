import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { useSelector } from "react-redux";
import { NoticeList } from "../../api/noticeApi";
import NoticeSearchComponent from "./NoticeSearchComponent";
import NoticeButtonComponent from "./NoticeButtonsComponent";
import PageComponent from "../common/PageComponent";

//공지 데이터를 서버에서 받아와서 화면에 보여주고 검색, 삭제 기능 담당
const NoticeListComponent = () => {
  const loginState = useSelector((state) => state.loginState); //로그인 상태 확인

  //상태변수들
  //const [상태 변수, 상태 변경 함수] = useState(초기값);
  const [notices, setNotices] = useState([]);
  const [pinnedNotices, setPinnedNotices] = useState([]); // 고정 공지 별도 관리
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

  // 고정 공지 별도 조회
  const fetchPinnedNotices = async () => {
    try {
      const pinnedParams = {
        ...searchParams,
        isPinned: true,
        page: 0,
        size: 100 // 충분히 큰 값으로 모든 고정 공지 조회
      };
      const response = await NoticeList(pinnedParams);
      setPinnedNotices(response.content || []);
    } catch (error) {
      console.error("고정 공지사항 조회 실패:", error);
      setPinnedNotices([]);
    }
  };

  //공지사항 목록 조회 (일반 공지만)
  const fetchNotices = async (params = searchParams) => {
    setLoading(true);
    try {
      // 일반 공지만 조회 (고정 공지 제외)
      const normalParams = {
        ...params,
        isPinned: false
      };
      const response = await NoticeList(normalParams);
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
    fetchPinnedNotices(); // 고정 공지 조회
    fetchNotices();
  }, []);

  //검색 처리
  const handleSearch = (newSearchParams) => {
    const updatedParams = {
      ...newSearchParams,
      page: 0 //검색 시 첫 페이지로
    };
    setSearchParams(updatedParams);
    
    // 검색 시에도 고정 공지는 별도로 조회
    if (updatedParams.keyword || updatedParams.searchType !== "ALL" || 
        updatedParams.startDate || updatedParams.endDate) {
      // 검색 조건이 있을 때는 고정 공지도 같은 조건으로 필터링
      const pinnedSearchParams = {
        ...updatedParams,
        isPinned: true,
        page: 0,
        size: 100
      };
      NoticeList(pinnedSearchParams).then(response => {
        setPinnedNotices(response.content || []);
      }).catch(error => {
        console.error("고정 공지사항 검색 실패:", error);
        setPinnedNotices([]);
      });
    } else {
      // 검색 조건이 없을 때는 모든 고정 공지 표시
      fetchPinnedNotices();
    }
    
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
      const allNoticeNums = [...pinnedNotices, ...notices].map(notice => notice.noticeNum);
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

  // 표시할 공지사항 목록 (고정 공지 + 일반 공지)
  const allNotices = [...pinnedNotices, ...notices];

  // 전체 공지사항 수 계산 (서버에서 받은 일반 공지 수 + 고정 공지 수)
  const totalNoticeCount = totalElements + pinnedNotices.length;

  return (
    <div className="max-w-screen-xl mx-auto my-10">
      <div className="min-blank">
        <div className="mb-6">
          <h1 className="newText-2xl font-bold text-gray-900">공지사항</h1>
          <p className="text-gray-600 mt-1 newText-base">
            전체 {totalNoticeCount}건의 공지사항이 있습니다.
          </p>
        </div>

        {/* 검색 컴포넌트 */}
        <NoticeSearchComponent
          onSearch={handleSearch}
          initialValues={searchParams}
        />

        {/* 공지사항 테이블 */}
        <div className="bg-white page-shadow border border-gray-200 overflow-hidden">
          {loading ? (
            <div className="flex justify-center items-center py-12">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
              <span className="ml-2 text-gray-600 newText-base">로딩중...</span>
            </div>
          ) : (
            <table className="w-full table-auto">
              <thead className="bg-gray-50 border-b border-gray-200">
                <tr>
                  {loginState.role === 'ADMIN' && ( //관리자만 선택 가능
                    <th className="w-12 px-2 py-3 text-center hidden sm:table-cell">
                      <input
                        type="checkbox"
                        checked={selectedNotices.length === allNotices.length && allNotices.length > 0}
                        onChange={(e) => handleSelectAll(e.target.checked)}
                        className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                      />
                    </th>
                  )}
                  <th className="min-w-[50px] px-2 py-3 text-center newText-sm font-medium text-gray-900">번호</th>
                  <th className="w-full px-3 py-3 text-left newText-sm font-medium text-gray-900">제목</th>
                  <th className="min-w-[100px] px-3 py-3 text-center newText-sm font-medium text-gray-900">작성자</th>
                  <th className="min-w-[100px] px-3 py-3 text-center newText-sm font-medium text-gray-900">작성일</th>
                  <th className="min-w-[100px] px-3 py-3 text-center newText-sm font-medium text-gray-900 hidden sm:table-cell">조회수</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {allNotices.length === 0 ? ( //공지사항이 없을 때
                  <tr>
                    <td
                      colSpan={loginState.role === 'ADMIN' ? 6 : 5}
                      className="px-4 py-12 text-center text-gray-500 newText-base"
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
                        <td className="px-4 py-3 hidden sm:table-cell">
                          <input
                            type="checkbox"
                            checked={selectedNotices.includes(notice.noticeNum)}
                            onChange={(e) => handleCheckboxChange(notice.noticeNum, e.target.checked)}
                            className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                          />
                        </td>
                      )}
                      <td className="px-2 py-3 text-center newText-sm text-gray-900">
                        {notice.isPinned ? (
                          <span className="inline-block min-w-[40px] px-3 py-1 rounded-full newText-xs font-medium bg-red-100 text-red-800 whitespace-nowrap">
                            공지
                          </span>
                        ) : (
                          //일반글 역순 번호 계산
                          //전체 일반글 개수 - (현재 페이지 × 페이지 크기) - 현재 일반글 인덱스 (고정공지 제외)
                          totalElements - (currentPage * searchParams.size) - (index - pinnedNotices.length)
                        )}
                      </td>
                      {/* 제목 */}
                      <td className="px-2 py-3">
                        <div className="flex items-center space-x-2 w-full">
                          <Link //제목 누르면 상세페이지로 넘어감
                            to={`/notice/NoticeDetail/${notice.noticeNum}`}
                            className="newText-sm font-medium text-gray-900 hover:text-blue-600 transition-colors truncate max-w-[100px] lg:max-w-[400px]"
                            title={notice.title} // 마우스 오버 시 전체 제목 툴팁으로 표시
                          >
                            {notice.title}
                          </Link>
                          {/* 첨부파일 아이콘 */}
                          {notice.fileCount > 0 && (
                            <span className="inline-flex items-center flex-shrink-0">
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
                          {/* 고정글 아이콘 */}
                          {notice.isPinned && (
                            <span className="inline-flex items-center flex-shrink-0">
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
                      {/* overflow 방지 whitespace-nowrap*/}
                      <td className="px-4 py-3 text-center newText-sm text-gray-500 whitespace-nowrap min-w-[100px]">
                        {notice.name}
                      </td>
                      <td className="px-4 py-3 text-center newText-sm text-gray-500 whitespace-nowrap min-w-[100px]">
                        {formatDate(notice.createdAt)}
                      </td>
                      <td className="px-4 py-3 text-center newText-sm text-gray-500 whitespace-nowrap min-w-[100px] hidden sm:table-cell">
                        {notice.viewCount?.toLocaleString() || 0}
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          )}
        </div>

        {/* 페이지네이션 */}
        <div className="flex justify-center mt-[50px]">
          <PageComponent
            totalPages={totalPages}
            current={currentPage}
            setCurrent={handlePageChange}
          />
        </div>

        {/* 하단 버튼 */}
        <div className="mb-8">
          <NoticeButtonComponent
            isAdmin={loginState.role === 'ADMIN'}
            selectedNotices={selectedNotices}
            onDelete={() => {
              // 삭제 후 목록 새로고침
              fetchPinnedNotices(); // 고정 공지도 새로고침
              fetchNotices();
              setSelectedNotices([]);
            }}
          />
        </div>
      </div>
    </div>
  );
};

export default NoticeListComponent;