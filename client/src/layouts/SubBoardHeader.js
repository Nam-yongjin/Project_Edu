import { Link, useLocation } from "react-router-dom";
import board from "../assets/noticeTitle.png";

const SubAboutHeader = () => {
    const location = useLocation();

    return (
         <div className="bg-yellow-100 w-full h-[300px] pt-8 pb-8"> 
            <div className="max-w-screen-xl mx-auto ">
                <div className="min-blank flex items-center justify-between">
                    <div className="newText-4xl font-bold text-gray-800">
                        알림마당
                    </div>
                    <div className="w-1/4">
                        <img src={board} className="w-[185px]"/>
                    </div>
                </div>
                
                <div className="newText-lg min-blank border-b-2 border-blue-500 pb-4">
                    <ul className="flex">
                        <li className="mr-6 pb-2">
                            <Link 
                                to="/notice/NoticeList" 
                                className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600 border-b-2 ${
                                    location.pathname.includes("/notice/NoticeList")
                                    ? "text-blue-600 border-blue-600"
                                    : "text-gray-500 border-transparent"
                                }`}
                            >
                                공지사항
                            </Link>
                        </li>
                        <li className="mr-6 pb-2">
                            <Link 
                                to="/qna" 
                                className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600 border-b-2 ${
                                    location.pathname.includes("/qna")
                                    ? "text-blue-600 border-blue-600"
                                    : "text-gray-500 border-transparent"
                                }`}
                            >
                                문의사항
                            </Link>
                        </li>
                        <li className="mr-6 pb-2">
                            <Link 
                                to="/news/NewsList" 
                                className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600 border-b-2 ${
                                    location.pathname.includes("/news/NewsList")
                                    ? "text-blue-600 border-blue-600"
                                    : "text-gray-500 border-transparent"
                                }`}
                            >
                                언론보도
                            </Link>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    );
};

export default SubAboutHeader;