import { Link, useLocation } from "react-router-dom";
import admin from "../assets/admin.png";

const SubAdminHeader = () => {
    const location = useLocation();

    return (
        <div className="bg-blue-100 w-full pt-8 pb-8">
            <div className="max-w-screen-xl mx-auto ">
                <div className="flex items-center justify-between">
                    <div className="text-4xl font-bold text-gray-800">
                        관리자 페이지
                    </div>
                    <div className="w-1/4">
                        <img src={admin} className="w-[400px] p-2" />
                    </div>
                </div>

                <div className="border-b-2 border-blue-500 pb-4">
                    <ul className="flex">
                        <li className="mr-6 pb-2">
                            <Link
                                to={`/admin/members`}
                                className={`text-lg font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${location.pathname.includes("/members") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                    }`}
                            >
                                회원관리
                            </Link>
                        </li>
                        <li className="mr-6 pb-2">
                            <Link
                                to="/admin/"
                                className={`text-lg font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${location.pathname.includes("/1") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                    }`}
                            >
                                분석1
                            </Link>
                        </li>
                        <li className="mr-6 pb-2">
                            <Link
                                to="/event/banner"
                                className={`text-lg font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${location.pathname.includes("/1") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                    }`}
                            >
                                프로그램 배너 관리
                            </Link>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    );
};
export default SubAdminHeader;