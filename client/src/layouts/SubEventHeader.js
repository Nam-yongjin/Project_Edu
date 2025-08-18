import { Link, useLocation } from "react-router-dom";
import { useSelector } from "react-redux";
import event from "../assets/event.png"

const SubEventHeader = () => {
    const location = useLocation();
    const loginState = useSelector((state) => state.loginState);
    return (
        <div className="bg-indigo-100 w-full pt-8 pb-8">
            <div className="max-w-screen-xl mx-auto ">
                <div className="min-blank flex items-center justify-between">
                    <div className="newText-4xl font-bold text-gray-800">
                        프로그램
                    </div>
                    <div className="w-1/4">
                        <img src={event} className="w-[185px] p-4" />
                        {/* <a href="https://www.flaticon.com/kr/free-icons/" title="프로그램 아이콘">프로그램 아이콘 제작자: itim2101 - Flaticon</a> */}
                    </div>
                </div>

                <div className="newText-lg min-blank border-b-2 border-blue-500 pb-4">
                    <ul className="flex">
                        <li className="mr-4 pb-2">
                            <Link
                                to="/event/list"
                                className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${location.pathname.includes("/event/list") || location.pathname.includes("/event/detail")? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                    }`}
                            >
                                프로그램안내
                            </Link>
                        </li>
                        {loginState.role === "ADMIN" ? (
                            <>
                         <li className="mr-4 pb-2">
                            <Link
                                to="/event/add"
                                className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${location.pathname.includes("/event/add") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                    }`}
                            >
                                프로그램추가
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
export default SubEventHeader;