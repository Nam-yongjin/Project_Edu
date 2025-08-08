import { Link, useLocation } from "react-router-dom";
import myInfo from "../assets/myInfo.png";
import { useSelector } from "react-redux";

const SubMyInfoHeader = () => {
    const location = useLocation();
    const loginState = useSelector((state) => state.loginState);

    const getMyPageLink = (role) => {
        switch (role) {
            case "STUDENT":
                return "/student/myInfo";
            case "TEACHER":
                return "/teacher/myInfo";
            case "COMPANY":
                return "/company/myInfo";
            default:
                return "/member/myInfo";
        }
    };

    return (
        <div className="bg-blue-100 w-full pt-8 pb-8">
            <div className="max-w-screen-xl mx-auto ">
                <div className="flex items-center justify-between">
                    <div className="newText-4xl font-bold text-gray-800">
                        마이페이지
                    </div>
                    <div className="w-1/4">
                        <img src={myInfo} className="w-[252px]" />
                    </div>
                </div>

                <div className="newText-lg border-b-2 border-blue-500 pb-4">
                    <ul className="flex">
                        <li className="mr-6 pb-2">
                            <Link
                                to={`${getMyPageLink(loginState.role)}`}
                                className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${location.pathname.includes("/myInfo") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                    }`}
                            >
                                내정보
                            </Link>
                        </li>
                        <li className="mr-6 pb-2">
                            <Link
                                to="/event/Reservation"
                                className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${location.pathname.includes("/event") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                    }`}
                            >
                                프로그램신청내역
                            </Link>
                        </li>
                        <li className="mr-6 pb-2">
                            <Link
                                to="/facility"
                                className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${location.pathname.includes("/facility") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                    }`}
                            >
                                공간예약내역
                            </Link>
                        </li>
                        <li className="mr-6 pb-2">
                            <Link
                                to="/demonstration"
                                className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${location.pathname.includes("/demonstration") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                    }`}
                            >
                                실증신청내역
                            </Link>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    );
};

export default SubMyInfoHeader;