import { Link, useLocation } from "react-router-dom";

const SubAboutHeader = () => {
    const location = useLocation();

    return (
         <div className="bg-blue-100 w-full pt-16 pb-16"> 
            <div className="max-w-screen-xl mx-auto ">
                <div className="flex items-center justify-between">
                    <div className="text-4xl font-bold text-gray-800">
                        소프트랩 소개
                    </div>
                    {/* 이미지 영역 */}
                    <div className="w-1/3">
                        {/* 이미지 대체 영역 */}
                    </div>
                </div>
                
                {/* 서브 메뉴 영역 */}
                <div className="mt-8 border-b-2 border-blue-500">
                    <ul className="flex">
                        <li className="mr-6 pb-2">
                            <Link 
                                to="/about/greeting" 
                                className={`text-lg font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${
                                    location.pathname.includes("/greeting") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                }`}
                            >
                                인사말
                            </Link>
                        </li>
                        <li className="mr-6 pb-2">
                            <Link 
                                to="/about/business" 
                                className={`text-lg font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${
                                    location.pathname.includes("/business") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                }`}
                            >
                                사업소개
                            </Link>
                        </li>
                        <li className="pb-2">
                            <Link 
                                to="/about/direction"
                                className={`text-lg font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${
                                    location.pathname.includes("/direction") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                }`}
                            >
                                오시는 길
                            </Link>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    );
};

export default SubAboutHeader;