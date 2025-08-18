import { Link, useLocation } from "react-router-dom";
import demonstration from "../assets/demonstration.png";
import { useSelector } from "react-redux";
const SubDemonstrationHeader = () => {
    const location = useLocation();
    const loginState = useSelector((state) => state.loginState);
    return (
        <div className="bg-purple-100 w-full pt-8 pb-8">
            <div className="max-w-screen-xl mx-auto ">
                <div className="min-blank flex items-center justify-between">
                    <div className="newText-4xl font-bold text-gray-800">
                        실증
                    </div>
                    <div className="w-1/4">
                        <img src={demonstration} className="w-[185px] p-4" />
                        {/* <a href="https://www.flaticon.com/free-icons/notebook" title="notebook icons">Notebook icons created by Freepik - Flaticon</a>*/}
                    </div>
                </div>

                <div className="newText-lg min-blank border-b-2 border-blue-500 pb-4">
                    <ul className="flex">
                        <li className="mr-6 pb-2">
                            <Link
                                to="/demonstration/demInfo"
                                className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${location.pathname.includes("/demonstration/demInfo") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                    }`}
                            >
                                실증소개
                            </Link>
                        </li>

                        <li className="mr-6 pb-2">
                            <Link
                                to="/demonstration/list"
                                className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${location.pathname.includes("/demonstration/list") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                    }`}
                            >
                                실증물품
                            </Link>
                        </li>

                        {loginState.role === "COMPANY" ? (
                            <li className="mr-6 pb-2">
                                <Link
                                    to="/demonstration/add"
                                    className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${location.pathname.includes("/demonstration/add")
                                        ? " text-blue-600 border-b-2 border-blue-600 active:text-blue-600"
                                        : ""
                                        }`}
                                >
                                    실증등록
                                </Link>
                            </li>
                        ) : <></>}

                        {loginState.role === "TEACHER" ? (
                            <li className="mr-6 pb-2">
                                <Link
                                    to="/demonstration/rentalList"
                                    className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${location.pathname.includes("/demonstration/rentalList") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                        }`}
                                >
                                    실증대여내역
                                </Link>
                            </li>
                        ) : <></>}

                        {loginState.role === "COMPANY" ? (
                            <li className="mr-6 pb-2">
                                <Link
                                    to="/demonstration/borrowList"
                                    className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${location.pathname.includes("/demonstration/borrowList") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                        }`}
                                >
                                    실증신청내역
                                </Link>
                            </li>
                        ) : <></>}
                    </ul>
                </div>
            </div>
        </div>
    );
};
export default SubDemonstrationHeader;