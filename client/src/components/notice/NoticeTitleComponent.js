import React from "react";
import { Link, useLocation } from "react-router-dom";
import noticeTitle from "../../assets/noticeTitle.png";

const NoticeTitleComponent = ({ title }) => {
  const location = useLocation();

  // 현재 경로
  const path = location.pathname;

  // 탭 경로 정의
  const tabItems = [
    { name: "공지사항", path: "/notice" },
    { name: "언론보도", path: "/news/list" },
    { name: "문의하기", path: "/qna" },
  ];

  return (
    <div className="bg-blue-100 w-full py-4">
      <div className="max-w-screen-xl mx-auto px-12">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-y-1">
          <div className="text-[30px] font-bold text-gray-700 md:mb-0">
            알림마당
          </div>
          <div className="w-full md:w-auto flex justify-center md:justify-end">
            <img
              src={noticeTitle}
              alt="게시판 이미지"
              className="w-[160px] h-auto mt-8 mr-16 object-contain"
            />
          </div>
        </div>
        <div className="flex space-x-6 mb-4">
          {tabItems.map((item) => {
            const isActive = path.startsWith(item.path);
            return (
              <Link
                key={item.name}
                to={item.path}
                className={`text-[18px] font-bold pb-1 border-b-2 ${
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
    </div>
  );
};

export default NoticeTitleComponent;