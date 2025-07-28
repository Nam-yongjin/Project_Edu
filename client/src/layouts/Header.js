import searchIcon from '../assets/search.png';
import logo from '../assets/logo.png';
import { Link } from 'react-router-dom';
import { useSelector } from 'react-redux';
import useMove from '../hooks/useMove';
import sideIcon from '../assets/side.png'

const Header = () => {
    const loginState = useSelector((state) => state.loginState)
    const { moveToPath } = useMove()
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
        }
    }
    const handleClickLogo = (e) => {
        moveToPath('/')
    }

    return (
        <header className="flex items-center justify-between px-14 h-20 bg-white shadow">
            {/* 로고 */}
            <div className="flex items-center">
                <img src={logo} alt="로고" className="w-[300px] h-[120px] flex-shrink-0 cursor-pointer"
                    onClick={handleClickLogo} />
                {/* https://www.keris.or.kr/main/cm/cntnts/cntntsViewPop.do?cntntsId=1681 */}
            </div>

            {/* 메뉴 */}
            <nav className="flex-1 mr-12">
                <ul className="flex justify-center text-lg font-medium">
                    <li className="mx-11 hover:text-blue-400 cursor-pointer"> 소개 </li>
                    <li className="mx-11 hover:text-blue-400 cursor-pointer">프로그램</li>
                    <li className="mx-11 hover:text-blue-400 cursor-pointer">공간운영</li>
                    <li className="mx-11 hover:text-blue-400 cursor-pointer">지원사업</li>
                    <li className="mx-11 hover:text-blue-400 cursor-pointer">알림마당</li>
                    {loginState.role === 'ADMIN' ? (
                        <li className="mx-11 hover:text-blue-400 cursor-pointer">관리자</li>
                    ) : (<></>)}
                </ul>
            </nav>

            {/* 로그인 + 검색 */}
            <div className="flex items-center space-x-6 text-sm">
                {loginState && loginState.memId ? (
                    <>
                        <span className="hover:text-blue-400 cursor-pointer"><Link to={'/logout'}>로그아웃</Link></span>
                        <span className="hover:text-blue-400 cursor-pointer"><Link to={getMyPageLink(loginState.role)}>마이페이지</Link></span>
                    </>
                ) : (
                    <>
                        <span className="hover:text-blue-400 cursor-pointer"><Link to={'/login'}>로그인</Link></span>
                        <span className="hover:text-blue-400 cursor-pointer"><Link to={'/register'}>회원가입</Link></span>
                    </>
                )}
                <img src={searchIcon} alt="검색" className="h-6 cursor-pointer" />
                {/* 아이콘 제작자<a href="https://www.flaticon.com/kr/authors/andy-horvath"title="Andy Horvath">
                Andy Horvath </a> from <a href="https://www.flaticon.com/kr/" title="Flaticon">www.flaticon.com'</a> */}
                <img src={sideIcon} alt="사이드메뉴" className="h-6 cursor-pointer" />
                {/* <a href="https://www.flaticon.com/kr/free-icons/" title="햄버거 아이콘">햄버거 아이콘 제작자: Lizel Arina - Flaticon</a> */}
            </div>
        </header>


    )
}
export default Header