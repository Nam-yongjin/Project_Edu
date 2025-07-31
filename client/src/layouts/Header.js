import searchIcon from '../assets/search.png';
import logo from '../assets/logo.png';
import { Link } from 'react-router-dom';
import { useSelector } from 'react-redux';
import useMove from '../hooks/useMove';
import sideIcon from '../assets/side.png';
import useLogin from '../hooks/useLogin';

const Header = () => {
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
                return "/member/myInfo"; // 기본 경로
        };
    };
    const handleClickLogo = (e) => {
        moveToPath('/');
    };
    const handleLogout = () => {
        doLogout();
        moveToPath('/');
    };

    return (
        <header className="bg-white shadow">
            <div className='max-w-[1200px] mx-auto flex items-center justify-between h-20 '>
                {/* 로고 */}
                <div className="flex items-center">
                    <img src={logo} alt="로고" className="w-[240px] h-[100px] flex-shrink-0 cursor-pointer"
                        onClick={handleClickLogo} />
                    {/* https://www.keris.or.kr/main/cm/cntnts/cntntsViewPop.do?cntntsId=1681 */}
                </div>

                {/* 메뉴 */}
                <nav className="flex-1 mr-12">
                    <ul className="flex justify-center text-lg font-medium relative">
                        <li className="mx-11 relative group cursor-pointer">
                            <span className="hover:text-blue-400 active:text-blue-600"><Link to="/">소개</Link></span>
                            <ul className="
                            absolute left-1/2 transform -translate-x-1/2 mt-2 w-36
                            bg-white shadow-lg rounded-md border
                            opacity-0 translate-y-2 invisible
                            group-hover:opacity-100 group-hover:translate-y-0 group-hover:visible
                            transition-all duration-300 ease-in-out z-50
                            text-sm text-gray-700 text-center
                            "
                            >
                                <li className="group-hover:rounded-md hover:bg-blue-100 active:bg-blue-200 px-3 py-3">
                                    <Link to="/">인사말</Link>
                                </li>
                                <li className="group-hover:rounded-md hover:bg-blue-100 active:bg-blue-200 px-3 py-3">
                                    <Link to="/">사업소개</Link>
                                </li>
                                <li className="group-hover:rounded-md hover:bg-blue-100 active:bg-blue-200 px-3 py-3">
                                    <Link to="/">오시는길</Link>
                                </li>
                            </ul>
                        </li>

                        <li className="mx-11 relative group cursor-pointer">
                            <span className="hover:text-blue-400 active:text-blue-600"><Link to="/">프로그램</Link></span>
                            <ul className="
                            absolute left-1/2 transform -translate-x-1/2 mt-2 w-36
                            bg-white shadow-lg rounded-md border
                            opacity-0 translate-y-2 invisible
                            group-hover:opacity-100 group-hover:translate-y-0 group-hover:visible
                            transition-all duration-300 ease-in-out z-50
                            text-sm text-gray-700 text-center
                            "
                            >
                                <li className="group-hover:rounded-md hover:bg-blue-100 active:bg-blue-200 px-3 py-3">
                                    <Link to="/">메뉴1</Link>
                                </li>
                                <li className="group-hover:rounded-md hover:bg-blue-100 active:bg-blue-200 px-3 py-3">
                                    <Link to="/">메뉴2</Link>
                                </li>
                                <li className="group-hover:rounded-md hover:bg-blue-100 active:bg-blue-200 px-3 py-3">
                                    <Link to="/">메뉴3</Link>
                                </li>
                            </ul>
                        </li>

                        <li className="mx-11 relative group cursor-pointer">
                            <span className="hover:text-blue-400 active:text-blue-600"><Link to="/">공간운영</Link></span>
                            <ul className="
                            absolute left-1/2 transform -translate-x-1/2 mt-2 w-36
                            bg-white shadow-lg rounded-md border
                            opacity-0 translate-y-2 invisible
                            group-hover:opacity-100 group-hover:translate-y-0 group-hover:visible
                            transition-all duration-300 ease-in-out z-50
                            text-sm text-gray-700 text-center
                            "
                            >
                                <li className="group-hover:rounded-md hover:bg-blue-100 active:bg-blue-200 px-3 py-3">
                                    <Link to="/">메뉴4</Link>
                                </li>
                                <li className="group-hover:rounded-md hover:bg-blue-100 active:bg-blue-200 px-3 py-3">
                                    <Link to="/">메뉴5</Link>
                                </li>
                                <li className="group-hover:rounded-md hover:bg-blue-100 active:bg-blue-200 px-3 py-3">
                                    <Link to="/">메뉴6</Link>
                                </li>
                            </ul>
                        </li>

                        <li className="mx-11 relative group cursor-pointer">
                            <span className="hover:text-blue-400 active:text-blue-600"><Link to="/">지원사업</Link></span>
                            <ul className="
                            absolute left-1/2 transform -translate-x-1/2 mt-2 w-36
                            bg-white shadow-lg rounded-md border
                            opacity-0 translate-y-2 invisible
                            group-hover:opacity-100 group-hover:translate-y-0 group-hover:visible
                            transition-all duration-300 ease-in-out z-50
                            text-sm text-gray-700 text-center
                            "
                            >
                                <li className="group-hover:rounded-md hover:bg-blue-100 active:bg-blue-200 px-3 py-3">
                                    <Link to="/">메뉴7</Link>
                                </li>
                                <li className="group-hover:rounded-md hover:bg-blue-100 active:bg-blue-200 px-3 py-3">
                                    <Link to="/">메뉴8</Link>
                                </li>
                                <li className="group-hover:rounded-md hover:bg-blue-100 active:bg-blue-200 px-3 py-3">
                                    <Link to="/">메뉴9</Link>
                                </li>
                            </ul>
                        </li>

                        <li className="mx-11 relative group cursor-pointer">
                            <span className="hover:text-blue-400 active:text-blue-600"><Link to="/">알림마당</Link></span>
                            <ul className="
                            absolute left-1/2 transform -translate-x-1/2 mt-2 w-36
                            bg-white shadow-lg rounded-md border
                            opacity-0 translate-y-2 invisible
                            group-hover:opacity-100 group-hover:translate-y-0 group-hover:visible
                            transition-all duration-300 ease-in-out z-50
                            text-sm text-gray-700 text-center
                            "
                            >
                                <li className="group-hover:rounded-md hover:bg-blue-100 active:bg-blue-200 px-3 py-3">
                                    <Link to="/">공지사항</Link>
                                </li>
                                <li className="group-hover:rounded-md hover:bg-blue-100 active:bg-blue-200 px-3 py-3">
                                    <Link to="/">문의사항</Link>
                                </li>
                                <li className="group-hover:rounded-md hover:bg-blue-100 active:bg-blue-200 px-3 py-3">
                                    <Link to="/">언론보도</Link>
                                </li>
                            </ul>
                        </li>
                        {loginState.role === 'ADMIN' ? (
                            <li className="mx-11 relative group cursor-pointer">
                                <span className="hover:text-blue-400 active:text-blue-600"><Link to="/">관리자</Link></span>
                                <ul className="
                            absolute left-1/2 transform -translate-x-1/2 mt-2 w-36
                            bg-white shadow-lg rounded-md border
                            opacity-0 translate-y-2 invisible
                            group-hover:opacity-100 group-hover:translate-y-0 group-hover:visible
                            transition-all duration-300 ease-in-out z-50
                            text-sm text-gray-700 text-center
                            "
                                >
                                    <li className="group-hover:rounded-md hover:bg-blue-100 active:bg-blue-200 px-3 py-3">
                                        <Link to="/">관리1</Link>
                                    </li>
                                    <li className="group-hover:rounded-md hover:bg-blue-100 active:bg-blue-200 px-3 py-3">
                                        <Link to="/">관리2</Link>
                                    </li>
                                    <li className="group-hover:rounded-md hover:bg-blue-100 active:bg-blue-200 px-3 py-3">
                                        <Link to="/">관리3</Link>
                                    </li>
                                </ul>
                            </li>
                        ) : (<></>)}
                    </ul>
                </nav>

                {/* 로그인 + 검색 */}
                <div className="flex items-center space-x-6 text-sm">
                    {loginState && loginState.memId ? (
                        <>
                            <span className="hover:text-blue-400 cursor-pointer active:text-blue-600" onClick={handleLogout}><Link to={'/logout'}>로그아웃</Link></span>
                            <span className="hover:text-blue-400 cursor-pointer active:text-blue-600"><Link to={getMyPageLink(loginState.role)}>마이페이지</Link></span>
                        </>
                    ) : (
                        <>
                            <span className="hover:text-blue-400 cursor-pointer active:text-blue-600"><Link to={'/login'}>로그인</Link></span>
                            <span className="hover:text-blue-400 cursor-pointer active:text-blue-600"><Link to={'/register'}>회원가입</Link></span>
                        </>
                    )}
                    <img src={searchIcon} alt="검색" className="h-6 cursor-pointer active:bg-gray-200" />
                    {/* 아이콘 제작자<a href="https://www.flaticon.com/kr/authors/andy-horvath"title="Andy Horvath">
                Andy Horvath </a> from <a href="https://www.flaticon.com/kr/" title="Flaticon">www.flaticon.com'</a> */}
                    <img src={sideIcon} alt="사이드메뉴" className="h-6 cursor-pointer active:bg-gray-200" />
                    {/* <a href="https://www.flaticon.com/kr/free-icons/" title="햄버거 아이콘">햄버거 아이콘 제작자: Lizel Arina - Flaticon</a> */}
                </div>
            </div>
        </header>


    )
}
export default Header