import { Link } from "react-router-dom";
import logo from "../assets/logo.png";
import searchIcon from "../assets/search.png";
import sideIcon from "../assets/side.png";
import { useSelector } from "react-redux";
import useMove from "../hooks/useMove";
import useLogin from "../hooks/useLogin";

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
        return "/member/myInfo";
    }
  };

  const handleClickLogo = () => moveToPath("/");
  const handleLogout = () => {
    doLogout();
    moveToPath("/");
  };

  const mainMenus = [
    {
      name: "소개",
      link: "/about",
      sub: [
        { name: "인사말", link: "/about/greeting" },
        { name: "사업소개", link: "/about/business" },
        { name: "오시는길", link: "/about/direction" },
      ],
    },
    {
      name: "프로그램",
      link: "/event",
      sub: [
        { name: "메뉴1", link: "/event" },
        { name: "메뉴2", link: "/" },
        { name: "메뉴3", link: "/" },
      ],
    },
    {
      name: "공간운영",
      link: "/facility",
      sub: [
        { name: "메뉴4", link: "/facility" },
        { name: "메뉴5", link: "/" },
        { name: "메뉴6", link: "/" },
      ],
    },
    {
      name: "지원사업",
      link: "/demonstration",
      sub: [
        { name: "메뉴7", link: "/demonstration" },
        { name: "메뉴8", link: "/" },
        { name: "메뉴9", link: "/" },
      ],
    },
    {
      name: "알림마당",
      link: "/notice",
      sub: [
        { name: "공지사항", link: "/notice" },
        { name: "문의사항", link: "/question" },
        { name: "언론보도", link: "/news" },
      ],
    },
  ];

  if (loginState.role === "ADMIN") {
    mainMenus.push({
      name: "관리자",
      link: "/admin",
      sub: [
        { name: "관리", link: "/admin" },
        { name: "관리", link: "/" },
        { name: "관리", link: "/" },
      ],
    });
  }

  return (
    <header className="fixed top-0 left-0 w-full bg-white shadow z-50 ">
      <div className="max-w-screen-xl mx-auto flex items-center justify-between h-20">
        {/* 로고 */}
        <div className="flex items-center flex-none">
          <img
            src={logo}
            alt="로고"
            className="w-[240px] h-[100px] object-contain cursor-pointer"
            onClick={handleClickLogo}
          />
          {/* https://www.keris.or.kr/main/cm/cntnts/cntntsViewPop.do?cntntsId=1681 */}
        </div>

        {/* 메뉴 */}
        <nav className="relative group hidden lg:block flex-none w-[640px]">
          <div className="flex justify-between">
            {mainMenus.map((menu, idx) => (
              <div key={idx} className="relative text-center flex-1">
                <Link
                  to={menu.link}
                  className="text-lg font-semibold hover:text-blue-400 active:text-blue-600 block"
                >
                  {menu.name}
                </Link>
              </div>
            ))}
          </div>

          {/* 드롭다운 영역 */}
          <div
            className="
              absolute inset-x-0 top-full mt-2 bg-white shadow-lg border-t
              opacity-0 invisible translate-y-2 rounded
              group-hover:opacity-100 group-hover:visible group-hover:translate-y-0
              transition-all duration-300 ease-in-out
              py-6
            "
          >
            <div className={`mx-auto grid gap-6 text-sm text-gray-700 px-4 ${loginState.role === "ADMIN" ? "grid-cols-6" : "grid-cols-5"}`}>
              {mainMenus.map((menu, idx) => (
                <div key={idx} className="space-y-2 text-center">
                  {menu.sub.map((item, subIdx) => (
                    <div key={subIdx}>
                      <Link
                        to={item.link}
                        className="hover:text-blue-400 active:text-blue-600 block px-2 py-1 rounded hover:bg-blue-100 active:bg-blue-200"
                      >
                        {item.name}
                      </Link>
                    </div>
                  ))}
                </div>
              ))}
            </div>
          </div>
        </nav>

        {/* 로그인/검색/사이드 */}
        <div className="flex items-center space-x-6 text-sm pl-8 flex-none pr-3">
          {loginState && loginState.memId ? (
            <>
              <span
                className="hover:text-blue-400 cursor-pointer active:text-blue-600 hidden lg:block"
                onClick={handleLogout}
              >
                로그아웃
              </span>
              <span className="hover:text-blue-400 cursor-pointer active:text-blue-600 hidden lg:block">
                <Link to={getMyPageLink(loginState.role)}>마이페이지</Link>
              </span>
            </>
          ) : (
            <>
              <span className="hover:text-blue-400 cursor-pointer active:text-blue-600 hidden lg:block">
                <Link to={"/login"}>로그인</Link>
              </span>
              <span className="hover:text-blue-400 cursor-pointer active:text-blue-600 hidden lg:block">
                <Link to={"/register"}>회원가입</Link>
              </span>
            </>
          )}

        </div>
        <div className="flex space-x-3 mx-3">
          <img src={searchIcon} alt="검색" className="h-6 cursor-pointer active:bg-gray-200" />
          {/* 아이콘 제작자<a href="https://www.flaticon.com/kr/authors/andy-horvath"title="Andy Horvath">
Andy Horvath </a> from <a href="https://www.flaticon.com/kr/" title="Flaticon">www.flaticon.com'</a> */}
          <img src={sideIcon} alt="사이드" className="h-6 cursor-pointer active:bg-gray-200" />
          {/* <a href="https://www.flaticon.com/kr/free-icons/" title="햄버거 아이콘">햄버거 아이콘 제작자: Lizel Arina - Flaticon</a> */}
        </div>

      </div>

    </header>
  );
};

export default Header;