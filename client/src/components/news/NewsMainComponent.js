import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { NewsList } from "../../api/newsApi";

const NewsMainComponent = () => {
  const [news, setNews] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchNews = async () => {
      setLoading(true);
      try {
        const data = await NewsList({
          page: 0,
          size: 4,
          keyword: "",
          searchType: "ALL",
          sortBy: "createdDate",
          sortDirection: "DESC",
        });
        setNews(data.content || []);
      } catch (err) {
        console.error("언론보도 불러오기 실패:", err);
        setNews([]);
      } finally {
        setLoading(false);
      }
    };

    fetchNews();
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
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-green-500"></div>
          <span className="ml-2 text-gray-600 newText-base">로딩중...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="w-full bg-white rounded-lg shadow-lg border border-gray-200 overflow-hidden">
      {/* 헤더 */}
      <div className="px-6 py-4 border-b border-gray-200">
        <div className="flex items-center justify-between">
          <h2 className="newText-2xl font-bold text-gray-900">언론보도</h2>
          <Link
            to="/news/NewsList"
            className="text-gray-600 hover:text-gray-800 newText-base transition-colors duration-200 flex items-center font-medium"
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
        {news.length === 0 ? (
          <div className="text-center py-8 text-gray-500 newText-base">
            <svg className="w-12 h-12 mx-auto text-gray-300 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9.5a2 2 0 00-2-2h-2m-2-1V6a2 2 0 00-2-2H5a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-5" />
            </svg>
            등록된 언론보도가 없습니다.
          </div>
        ) : (
          <ul className="space-y-3">
            {news.map((news, index) => (
              <li
                key={news.newsNum}
                className="group hover:bg-gray-50 rounded-lg p-3 transition-all duration-200 border-l-4 border-transparent hover:border-green-500"
              >
                <div className="flex items-start justify-between">
                  <div className="flex items-start flex-1 min-w-0">

                    {/* 제목과 메타 정보 */}
                    <div className="flex-1 min-w-0">
                      <Link
                        to={`/news/NewsDetail/${news.newsNum}`}
                        className="block group-hover:text-green-600 transition-colors duration-200"
                      >
                        <div className="flex items-center">
                          <h3 className="newText-base font-medium text-gray-900 group-hover:text-green-600 truncate max-w-[200px] lg:max-w-[400px]">
                            {truncateTitle(news.title)}
                          </h3>

                            {/* New 표시 (최근 3일 이내) */}
                            {(() => {
                              const createdDate = new Date(news.createdAt);
                              const daysDiff = Math.floor((new Date() - createdDate) / (1000 * 60 * 60 * 24));
                              return daysDiff <= 3 && (
                                <span className="ml-2 newText-sm font-bold text-red-500 whitespace-nowrap">
                                  New !
                                </span>
                              );
                            })()}
                        </div>

                        {/* 작성자 및 조회수 */}
                        <div className="flex items-center mt-1 newText-sm text-gray-500">
                          <span>{news.name}</span>
                          {news.viewCount !== undefined && (
                            <>
                              <span className="mx-1">·</span>
                              <span>조회 {news.viewCount.toLocaleString()}</span>
                            </>
                          )}
                        </div>
                      </Link>
                    </div>
                  </div>

                  {/* 날짜 */}
                  <div className="flex-shrink-0 ml-4">
                    <time className="newText-sm text-gray-500 font-medium">
                      {formatDate(news.createdAt)}
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

export default NewsMainComponent;