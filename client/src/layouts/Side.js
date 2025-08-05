import { Link } from 'react-router-dom';
import { useSelector } from 'react-redux';
import useMove from "../hooks/useMove";
import useLogin from "../hooks/useLogin";
import cancel from "../assets/cancel.png";

const Side = ({ isOpen, onClose }) => {
    const loginState = useSelector((state) => state.loginState);
    const { moveToPath } = useMove();
    const { doLogout } = useLogin();

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

    const handleLogout = () => {
        doLogout();
        moveToPath("/");
    };

    return (
        <div>
            {/* 오버레이 */}
            <div className={`fixed inset-0 bg-gray-900 bg-opacity-50 z-40 transition-opacity duration-200 ${isOpen ? 'opacity-100' : 'opacity-0 pointer-events-none'}`}
                onClick={onClose}
            ></div>

            {/* 사이드바 */}
            <div className={`fixed top-0 right-0 h-full w-64 bg-white shadow-xl z-50 transform transition-transform duration-300 ease-in-out ${isOpen ? 'translate-x-0' : 'translate-x-full'}`}
            >
                <div className="flex justify-between items-center p-4 border-b">
                    <h2 className="text-xl font-bold">🛠 메뉴</h2>
                    <button onClick={onClose} className="text-gray-500 hover:text-gray-700">
                        <img src={cancel} alt="검색" className="h-[20px] w-[20px] cursor-pointer active:bg-gray-200" />
                        {/*<a href="https://www.flaticon.com/kr/free-icons/" title="닫기 아이콘">닫기 아이콘 제작자: Roundicons - Flaticon</a>*/}
                    </button>
                </div>

                <nav className="p-4">
                    <ul className="space-y-2">
                        {loginState && loginState.memId ?
                            <>
                                <li>
                                    <div className="block p-2 rounded hover:bg-gray-100 hover:cursor-pointer" onClick={() => {
                                        handleLogout();
                                        onClose();
                                    }}>
                                        🔒 로그아웃
                                    </div>
                                </li>
                                <li>
                                    <Link to={getMyPageLink(loginState.role)} className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>
                                        🙍🏻‍♂️ 내정보
                                    </Link>
                                </li>
                            </>
                            :
                            <>
                                <li>
                                    <Link to={"/login"} className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>
                                        🔑 로그인
                                    </Link>
                                </li>
                                <li>
                                    <Link to={"/register"} className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>
                                        📝 회원가입
                                    </Link>
                                </li>
                            </>
                        }
                        <li>
                            <Link to={"/about"} className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>
                                💡 소개
                            </Link>
                        </li>
                        <li>
                            <Link to={"/event"} className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>
                                📅 프로그램
                            </Link>
                        </li>
                        <li>
                            <Link to={"/facility"} className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>
                                🏢 공간 운영
                            </Link>
                        </li>
                        <li>
                            <Link to={"/demonstration"} className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>
                                💻 실증 지원
                            </Link>
                        </li>
                        <li>
                            <Link to={"/notice"} className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>
                                📢 알림마당
                            </Link>
                        </li>
                        {loginState.role === "ADMIN" ?
                            <><li>
                                <Link to={"/admin"} className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>
                                    🔧 관리자
                                </Link>
                            </li></> : <></>}
                    </ul>
                </nav>
            </div>
        </div>
    );
};
export default Side;