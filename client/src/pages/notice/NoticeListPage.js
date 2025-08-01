import React, { useState, useEffect } from "react";
import { useSelector } from "react-redux";
import { NoticeList } from "../../api/noticeApi";
import NoticeSearchComponent from "../../components/notice/NoticeSearchComponent";
import NoticeListComponent from "../../components/notice/NoticeListComponent";
import NoticeButtonsComponent from "../../components/notice/NoticeButtonsComponent";
import NoticeTitleComponent from "../../components/notice/NoticeTitleComponent";

//공지 데이터를 서버에서 받아와서 화면에 보여주고 검색, 삭제 기능 담당
const NoticeListPage = () => {
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
      const response = await NoticeList(params);//컨트롤러에 전달되는 매개변수
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
  }

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

  return (
      <div>
        <NoticeTitleComponent title="공지사항" />
        <div className="max-w-7xl mx-auto px-4 py-6">
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

          {/* 공지사항 리스트 컴포넌트 */}
          <div className="bg-white shadow-sm border border-gray-200 rounded-lg overflow-hidden">
            {loading ? (
              <div className="flex justify-center items-center py-12">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
                <span className="ml-2 text-gray-600">로딩중...</span>
              </div>
            ) : (
              <NoticeListComponent
                notices={notices}
                selectedNotices={selectedNotices}
                setSelectedNotices={setSelectedNotices}
                isAdmin={loginState.role === 'ADMIN'}
              />
            )}
          </div>

          {/* 페이지네이션 */}
          {renderPagination()}

          {/* 하단 버튼 */}
          <NoticeButtonsComponent
            isAdmin={loginState.role === 'ADMIN'}
            selectedNotices={selectedNotices}
            onDelete={() => {
              //삭제 후 목록 새로고침
              fetchNotices();
              selectedNotices([]);
            }}
          />
        </div>
      </div>

    );
};

export default NoticeListPage;