import searchIcon from '../assets/search.png';
import logo from '../assets/logo.png';

const Header = () => {
    return (
        <header className="flex items-center justify-between px-14 h-20 bg-white shadow">
            {/* 로고 */}
            <div className="flex items-center">
                <img src={logo} alt="로고" className="h-[120px] max-h-[120px]" />
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
                </ul>
            </nav>

            {/* 로그인 + 검색 */}
            <div className="flex items-center space-x-6 text-sm">
                <span className="hover:text-blue-400 cursor-pointer">로그인</span>
                <span className="hover:text-blue-400 cursor-pointer">회원가입</span>
                <img src={searchIcon} alt="검색" className="h-6" />
                {/* 아이콘 제작자<a href="https://www.flaticon.com/kr/authors/andy-horvath"title="Andy Horvath">
                Andy Horvath </a> from <a href="https://www.flaticon.com/kr/" title="Flaticon">www.flaticon.com'</a> */}
            </div>
        </header>
            
          
    )
}
export default Header