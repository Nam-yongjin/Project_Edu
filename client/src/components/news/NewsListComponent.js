import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { useSelector } from "react-redux";
import { NewsList } from "../../api/newsApi";
import NewsSearchComponent from "./NewsSearchComponent";
import NewsButtonComponent from "./NewsButtonsComponent";
import PageComponent from "../common/PageComponent";

const NewsListComponent = () => {
  const loginState = useSelector((state) => state.loginState); //로그인 상태 확인

  //상태변수들
  //const [상태 변수, 상태 변경 함수] = useState(초기값);
  const [articles, setArticles] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [selectedArticles, setSelectedArticles] = useState([]);

  const [searchParams, setSearchParams] = useState({
    page: 0,
    size: 10,
    keyword: "",
    searchType: "ALL",
    startDate: "",
    endDate: "",
    sortBy: "createdAt",
    sortDirection: "DESC"
  });

  //언론보도 목록 조회
  const fetchArticles = async (params = searchParams) => {
    setLoading(true);
    try{
      const response = await NewsList(params); //컨트롤러에 전달되는 매개변수
      setArticles(response.content || []);
      setTotalPages(response.totalPages || 0);
      setTotalElements(response.totalElements || 0);
      setCurrentPage(response.number || 0);
    } catch (error) {
      console.error("언론보도 목록 조회 실패:", error);
      setArticles([]);
    } finally {
      setLoading(false);
    }
  };

  //처음 페이지 들어오면 초기 데이터를 나타냄
  useEffect(() => {
    fetchArticles();
  }, []);

  //검색 처리
  const handleSearch = (newSearchParams) => {
    const updatedParams = {
      ...newSearchParams,
      page: 0, //검색 시 첫 페이지로
    };
    setSearchParams(updatedParams);
    fetchArticles(updatedParams);
    setSelectedArticles([]); //선택 초기화
  };

  //페이지 변경
  const handlePageChange = (page) => {
    const updatedParams = {
      ...searchParams,
      page: page,
    };
    setSearchParams(updatedParams);
    fetchArticles(updatedParams);
    setSelectedArticles([]); //페이지 변경 시 선택 초기화
  };

  // 체크박스 변경 처리
  const handleCheckboxChange = (newsNum, isChecked) => { //뉴스 선택
    if (isChecked) {
      setSelectedArticles(prev => [...prev, newsNum]);
    } else {
      setSelectedArticles(prev => prev.filter(num => num !== newsNum));
    }
  };

  // 전체 선택/해제
  const handleSelectAll = (isChecked) => {
    if (isChecked) {
      const allNewsNums = articles.map(news => news.newsNum);
      setSelectedArticles(allNewsNums);
    } else {
      setSelectedArticles([]);
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

  // 뉴스 정렬
    const allArticles = articles;

  return (
    <div className="max-w-screen-xl mx-auto my-10">
      <div className="min-blank">
        <div className="mb-6">
          <h1 className="newText-2xl font-bold text-gray-900">언론보도</h1>
          <p className="text-gray-600 mt-1">
            전체 {totalElements}건의 뉴스가 있습니다.
          </p>
        </div>

        {/* 검색 컴포넌트 */}
        <NewsSearchComponent
          onSearch={handleSearch}
          initialValues={searchParams}
        />

        {/* 언론보도 테이블 */}
        <div className="bg-white page-shadow border border-gray-200 overflow-hidden">
          {loading ? (
            <div className="flex justify-center items-center py-12">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
              <span className="ml-2 text-gray-600">로딩중...</span>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full table-auto">
                <thead className="bg-gray-50 border-b border-gray-200">
                  <tr>
                    {loginState.role === 'ADMIN' && ( //관리자만 선택 가능
                      <th className="w-12 px-2 py-3 text-center hidden sm:table-cell">
                        <input
                          type="checkbox"
                          checked={selectedArticles.length === articles.length && articles.length > 0}
                          onChange={(e) => handleSelectAll(e.target.checked)}
                          className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                        />
                      </th>
                    )}
                    <th className="w-16 px-2 py-3 text-center newText-sm font-medium text-gray-900">번호</th>
                    <th className="px-3 py-3 text-left newText-sm font-medium text-gray-900">제목</th>
                    <th className="min-w-[70px] px-3 py-3 text-center newText-sm font-medium text-gray-900">작성자</th>
                    <th className="min-w-[90px] px-3 py-3 text-center newText-sm font-medium text-gray-900">작성일</th>
                    <th className="min-w-[70px] px-3 py-3 text-center newText-sm font-medium text-gray-900 hidden sm:table-cell">조회수</th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {allArticles.length === 0 ? ( //뉴스가 없을 때
                    <tr>
                      <td 
                        colSpan={loginState.role === 'ADMIN' ? 6 : 5}
                        className="px-4 py-12 text-center text-gray-500"
                      >
                        등록된 뉴스가 없습니다.
                      </td>
                    </tr>
                  ) : (
                    allArticles.map((news, index) => (
                      <tr 
                        key={news.newsNum}
                        className={`hover:bg-gray-50`}
                      >
                        {loginState.role === 'ADMIN' && (
                          <td className="px-4 py-3 hidden sm:table-cell">
                            <input
                              type="checkbox"
                              checked={selectedArticles.includes(news.newsNum)}
                              onChange={(e) => handleCheckboxChange(news.newsNum, e.target.checked)}
                              className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                            />
                          </td>
                        )}
                        {/* 역순 번호 */}
                        <td className="px-2 py-3 text-center newText-sm text-gray-900 whitespace-nowrap min-w-[50px]">
                          {totalElements - (currentPage * searchParams.size) - index}
                        </td>
                        {/* 제목 */}
                        <td className="px-2 py-3">
                          <div className="flex items-center space-x-2">
                            <Link //제목 누르면 상세페이지로 넘어감
                              to={`/news/NewsDetail/${news.newsNum}`}
                              className="newText-sm font-medium text-gray-900 hover:text-blue-600 transition-colors truncate max-w-[150px] lg:max-w-[700px]"
                              title={news.title}
                            >
                              {news.title}
                            </Link>
                          </div>
                        </td>
                        <td className="px-4 py-3 text-center newText-sm text-gray-500 whitespace-nowrap min-w-[70px]">
                          {news.name}
                        </td>
                        <td className="px-4 py-3 text-center newText-sm text-gray-500 whitespace-nowrap min-w-[90px]">
                          {formatDate(news.createdAt)}
                        </td>
                        <td className="px-4 py-3 text-center newText-sm text-gray-500 whitespace-nowrap min-w-[70px] hidden sm:table-cell">
                          {news.viewCount?.toLocaleString() || 0}
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
        <div className="flex justify-center mt-[50px]">
          <PageComponent
            totalPages={totalPages}
            current={currentPage}
            setCurrent={handlePageChange}
          />
        </div>

        {/* 하단 버튼 */}
        <div className="mb-8">
          <NewsButtonComponent
            isAdmin={loginState.role === 'ADMIN'}
            selectedArticles={selectedArticles}
            onDelete={() => {
              // 삭제 후 목록 새로고침
              fetchArticles();
              setSelectedArticles([]);
            }}
          />
        </div>
      </div>
    </div>
  );
};

export default NewsListComponent;