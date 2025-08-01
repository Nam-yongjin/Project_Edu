import React from "react";
import { Link, useLocation } from "react-router-dom";

const NoticeTitleComponent = ({ title }) => {
  const location = useLocation();

  // 현재 경로
  const path = location.pathname;

  // 탭 경로 정의
  const tabItems = [
    { name: "공지사항", path: "/notice/list" },
    { name: "언론보도", path: "/news/list" },
    { name: "문의하기", path: "/qna" },
  ];

  return (
    <div className="w-full px-6 py-4 bg-gray-50 border-b">
      <div className="text-lg font-semibold text-gray-700 mb-2">알림마당</div>
      <div className="flex space-x-6">
        {tabItems.map((item) => {
          const isActive = path.startsWith(item.path);
          return (
            <Link
              key={item.name}
              to={item.path}
              className={`text-base font-medium pb-1 border-b-2 ${
                isActive
                  ? "text-blue-600 border-blue-600"
                  : "text-gray-500 border-transparent hover:text-blue-400"
              }`}
            >
              {item.name}
            </Link>
          );
        })}
      </div>
    </div>
    
  );
};

export default NoticeTitleComponent;