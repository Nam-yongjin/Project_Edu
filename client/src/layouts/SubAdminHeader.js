import { Link, useLocation } from "react-router-dom";
import admin from "../assets/admin.png";

const SubAdminHeader = () => {
    const location = useLocation();

    return (
        <div className="bg-red-100 w-full pt-8 pb-8">
            <div className="max-w-screen-xl mx-auto ">
                <div className="min-blank flex items-center justify-between">
                    <div className="newText-4xl font-bold text-gray-800">
                        관리자 페이지
                    </div>
                    <div className="w-1/4">
                        <img src={admin} className="w-[400px] p-4" />
                    </div>
                </div>

                <div className="newText-lg min-blank border-b-2 border-blue-500 pb-4">
                    <ul className="flex">
                        <li className="mr-4 pb-2">
                            <Link
                                to={`/admin/members`}
                                className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${location.pathname.includes("/members") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                    }`}
                            >
                                회원관리
                            </Link>
                        </li>
                        <li className="mr-4 pb-2">
                            <Link
                                to="/admin/banner"
                                className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${location.pathname.includes("/admin/banner") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                    }`}
                            >
                                배너관리
                            </Link>
                        </li>
                        <li className="mr-4 pb-2">
                            <Link
                                to="/admin/stats"
                                className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${location.pathname.includes("/admin/stats") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                    }`}
                            >
                                통계확인
                            </Link>
                        </li>
                        <li className="mr-4 pb-2">
                            <Link
                                to="/facility/holiday"
                                className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${location.pathname.includes("/facility/holiday") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                    }`}
                            >
                                휴무일관리
                            </Link>
                        </li>
                         <li className="mr-4 pb-2">
                            <Link
                                to="/admin/adminSelectEmail"
                                className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${location.pathname.includes("/admin/adminSelectEmail") || location.pathname.includes("/admin/adminEmail") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                    }`}
                            >
                                메일보내기
                            </Link>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    );
};
export default SubAdminHeader;