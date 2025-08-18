import { Link } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { useState } from 'react';
import useMove from "../hooks/useMove";
import useLogin from "../hooks/useLogin";
import cancel from "../assets/cancel.png";

const Side = ({ isOpen, onClose }) => {
    const loginState = useSelector((state) => state.loginState);
    const { moveToPath } = useMove();
    const { doLogout } = useLogin();
    const [openMenus, setOpenMenus] = useState({});

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

    const handleMenuToggle = (menuName) => {
        setOpenMenus(prevState => ({
            ...prevState,
            [menuName]: !prevState[menuName]
        }));
    };

    return (
        <div>
            {/* 오버레이 */}
            <div className={`fixed inset-0 bg-gray-900 bg-opacity-50 z-40 transition-opacity duration-200 ${isOpen ? 'opacity-100' : 'opacity-0 pointer-events-none'}`}
                onClick={onClose}
            ></div>

            {/* 사이드바 */}
            <div className={`fixed overflow-y-auto top-0 right-0 h-full lg:w-64 sm:w-56 w-48 bg-white shadow-xl z-50 transform transition-transform duration-300 ease-in-out ${isOpen ? 'translate-x-0' : 'translate-x-full'}`}
            >
                <div className="flex justify-between items-center p-4 border-b">
                    <h2 className="newText-xl font-bold">🛠 메뉴</h2>
                    <button onClick={onClose} className="text-gray-500 hover:text-gray-700">
                        <img src={cancel} alt="검색" className="h-[20px] w-[20px] cursor-pointer active:bg-gray-200" />
                        {/*<a href="https://www.flaticon.com/kr/free-icons/" title="닫기 아이콘">닫기 아이콘 제작자: Roundicons - Flaticon</a>*/}
                    </button>
                </div>

                <nav className="newText-base p-4">
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
                            <div
                                className="flex items-center justify-between p-2 rounded hover:bg-gray-100 cursor-pointer"
                                onClick={() => handleMenuToggle('about')}
                            >
                                <span>💡 소개</span>
                                <span className={`w-2 h-2 border-r-2 border-b-2 border-gray-500 transform transition-transform duration-300 ease-in-out  ${openMenus['about'] ? '-rotate-[135deg]' : 'rotate-45'}`}></span>
                            </div>
                            <ul
                                className={`pl-4 transition-all duration-300 ease-in-out overflow-hidden ${openMenus['about'] ? 'max-h-96 opacity-100 mt-2' : 'max-h-0 opacity-0'
                                    }`}
                            >
                                <li>
                                    <Link to="/about/greeting" className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>인사말</Link>
                                </li>
                                <li>
                                    <Link to="/about/business" className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>사업소개</Link>
                                </li>
                                <li>
                                    <Link to="/about/direction" className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>오시는길</Link>
                                </li>
                            </ul>
                        </li>
                        <li>
                            <div
                                className="flex items-center justify-between p-2 rounded hover:bg-gray-100 cursor-pointer"
                                onClick={() => handleMenuToggle('event')}
                            >
                                <span>📅 프로그램</span>
                                <span className={`w-2 h-2 border-r-2 border-b-2 border-gray-500 transform transition-transform duration-300 ease-in-out  ${openMenus['event'] ? '-rotate-[135deg]' : 'rotate-45'}`}></span>
                            </div>
                            <ul
                                className={`pl-4 transition-all duration-300 ease-in-out overflow-hidden ${openMenus['event'] ? 'max-h-96 opacity-100 mt-2' : 'max-h-0 opacity-0'
                                    }`}
                            >
                                <li>
                                    <Link to="/event/list" className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>프로그램 안내</Link>
                                </li>
                                {loginState.role === "ADMIN" ?
                                    <>
                                        <li>
                                            <Link to="/event/add" className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>프로그램 추가</Link>
                                        </li>
                                    </>
                                    : <></>}
                            </ul>
                        </li>
                        <li>
                            <div
                                className="flex items-center justify-between p-2 rounded hover:bg-gray-100 cursor-pointer"
                                onClick={() => handleMenuToggle('facility')}
                            >
                                <span>🏢 공간 운영</span>
                                <span className={`w-2 h-2 border-r-2 border-b-2 border-gray-500 transform transition-transform duration-300 ease-in-out  ${openMenus['facility'] ? '-rotate-[135deg]' : 'rotate-45'}`}></span>
                            </div>
                            <ul
                                className={`pl-4 transition-all duration-300 ease-in-out overflow-hidden ${openMenus['facility'] ? 'max-h-96 opacity-100 mt-2' : 'max-h-0 opacity-0'
                                    }`}
                            >
                                <li>
                                    <Link to="/facility/list" className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>공간 안내</Link>
                                </li>
                                {loginState.role === "ADMIN" ?
                                    <>
                                        <li>
                                            <Link to="/facility/add" className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>공간 추가</Link>
                                        </li>
                                        <li>
                                            <Link to="/facility/adminreservations" className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>공간 대여 관리</Link>
                                        </li>
                                    </>
                                    : <></>}
                            </ul>
                        </li>
                        <li>
                            <div
                                className="flex items-center justify-between p-2 rounded hover:bg-gray-100 cursor-pointer"
                                onClick={() => handleMenuToggle('demonstration')}
                            >
                                <span>💻 실증 지원</span>
                                <span className={`w-2 h-2 border-r-2 border-b-2 border-gray-500 transform transition-transform duration-300 ease-in-out  ${openMenus['demonstration'] ? '-rotate-[135deg]' : 'rotate-45'}`}></span>
                            </div>
                            <ul
                                className={`pl-4 transition-all duration-300 ease-in-out overflow-hidden ${openMenus['demonstration'] ? 'max-h-96 opacity-100 mt-2' : 'max-h-0 opacity-0'
                                    }`}
                            >
                                <li>
                                    <Link to="/demonstration/demInfo" className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>실증 소개</Link>
                                </li>
                                <li>
                                    <Link to="/demonstration/list" className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>실증 물품</Link>
                                </li>
                                {loginState.role === "TEACHER" ?
                                    <li>
                                        <Link to="/demonstration/rentalList" className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>실증 대여 내역</Link>
                                    </li>
                                    : <></>}
                                {loginState.role === "COMPANY" ?
                                    <>
                                        <li>
                                            <Link to="/demonstration/add" className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>실증 등록</Link>
                                        </li>
                                        <li>
                                            <Link to="/demonstration/borrowList" className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>실증 등록 내역</Link>
                                        </li>
                                    </>
                                    : <></>}
                                {loginState.role === "ADMIN" ?
                                    <>
                                        <li>
                                            <Link to="/demonstration/adminReg" className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>실증 등록 관리</Link>
                                        </li>
                                        <li>
                                            <Link to="/demonstration/adminRes" className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>실증 대여 관리</Link>
                                        </li>
                                    </>
                                    : <></>}
                            </ul>
                        </li>
                        <li>
                            <div
                                className="flex items-center justify-between p-2 rounded hover:bg-gray-100 cursor-pointer"
                                onClick={() => handleMenuToggle('notice')}
                            >
                                <span>📢 알림마당</span>
                                <span className={`w-2 h-2 border-r-2 border-b-2 border-gray-500 transform transition-transform duration-300 ease-in-out  ${openMenus['notice'] ? '-rotate-[135deg]' : 'rotate-45'}`}></span>
                            </div>
                            <ul
                                className={`pl-4 transition-all duration-300 ease-in-out overflow-hidden ${openMenus['notice'] ? 'max-h-96 opacity-100 mt-2' : 'max-h-0 opacity-0'
                                    }`}
                            >
                                <li>
                                    <Link to="/notice" className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>공지사항</Link>
                                </li>
                                <li>
                                    <Link to="/question" className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>문의사항</Link>
                                </li>
                                <li>
                                    <Link to="/news" className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>언론보도</Link>
                                </li>
                            </ul>
                        </li>
                        {loginState.role === "ADMIN" ?
                            <>
                                <li>
                                    <div
                                        className="flex items-center justify-between p-2 rounded hover:bg-gray-100 cursor-pointer"
                                        onClick={() => handleMenuToggle('admin')}
                                    >
                                        <span>🔧 관리자</span>
                                        <span className={`w-2 h-2 border-r-2 border-b-2 border-gray-500 transform transition-transform duration-300 ease-in-out  ${openMenus['admin'] ? '-rotate-[135deg]' : 'rotate-45'}`}></span>
                                    </div>
                                    <ul
                                        className={`pl-4 transition-all duration-300 ease-in-out overflow-hidden ${openMenus['admin'] ? 'max-h-96 opacity-100 mt-2' : 'max-h-0 opacity-0'
                                            }`}
                                    >
                                        <li>
                                            <Link to="/admin/members" className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>회원 관리</Link>
                                        </li>
                                        <li>
                                            <Link to="/admin/banner" className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>배너 관리</Link>
                                        </li>
                                        <li>
                                            <Link to="/admin/stats" className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>통계 확인</Link>
                                        </li>
                                        <li>
                                            <Link to="/facility/holiday" className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>휴무일 관리</Link>
                                        </li>
                                        <li>
                                            <Link to="/admin/adminSelectEmail" className="block p-2 rounded hover:bg-gray-100" onClick={onClose}>메일 보내기</Link>
                                        </li>
                                    </ul>
                                </li>
                            </> : <></>}
                    </ul>
                </nav>
            </div>
        </div>
    );
};
export default Side;