import { Link, useLocation } from "react-router-dom";
import { useSelector } from "react-redux";
import facility from "../assets/facility.png"

const SubFacilityHeader = () => {
    const location = useLocation();
    const loginState = useSelector((state) => state.loginState);
    return (
        <div className="bg-orange-100 w-full pt-8 pb-8">
            <div className="max-w-screen-xl mx-auto ">
                <div className="min-blank flex items-center justify-between">
                    <div className="newText-4xl font-bold text-gray-800">
                        공간
                    </div>
                    <div className="w-1/4">
                        <img src={facility} className="w-[185px] p-4" />
                        {/* <a href="https://www.flaticon.com/kr/free-icons/-" title="증강 현실 아이콘">증강 현실 아이콘 제작자: yunaspandusatria - Flaticon</a> */}
                    </div>
                </div>

                <div className="newText-lg min-blank border-b-2 border-blue-500 pb-4">
                    <ul className="flex">
                        <li className="mr-4 pb-2">
                            <Link
                                to="/facility/list"
                                className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${location.pathname.includes("/facility/list") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                    }`}
                            >
                                공간안내
                            </Link>
                        </li>
                        {loginState.role === "ADMIN" ? (
                            <>
                         <li className="mr-4 pb-2">
                            <Link
                                to="/facility/add"
                                className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${location.pathname.includes("/facility/add") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                    }`}
                            >
                                공간추가
                            </Link>
                        </li>
                         <li className="mr-4 pb-2">
                            <Link
                                to="/facility/adminreservations"
                                className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${location.pathname.includes("/facility/adminreservations") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                    }`}
                            >
                                공간대여관리
                            </Link>
                        </li>
                        </>
                        ) : <></>}
                    </ul>
                </div>
            </div>
        </div>
    );
};
export default SubFacilityHeader;