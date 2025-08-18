import { Link } from "react-router-dom";
import logo from "../assets/logo.png";
import sideIcon from "../assets/side.png";
import { useSelector } from "react-redux";
import useMove from "../hooks/useMove";
import useLogin from "../hooks/useLogin";
import { useState } from 'react';
import Side from "./Side";

const Header = () => {
  const loginState = useSelector((state) => state.loginState);
  const { moveToPath } = useMove();
  const { doLogout } = useLogin();
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

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

  const handleOpenSidebar = () => setIsSidebarOpen(true);
  const handleCloseSidebar = () => setIsSidebarOpen(false);

  const mainMenus = [
    {
      name: "소개",
      link: "/about",
      sub: [
        { name: "인사말", link: "/about/greeting" },
        { name: "사업소개", link: "/about/business" },
        { name: "오시는 길", link: "/about/direction" },
      ],
    },
    {
      name: "프로그램",
      link: "/event",
      sub: [
        { name: "프로그램 안내", link: "/event/list" },
      ],
    },
    {
      name: "공간 운영",
      link: "/facility",
      sub: [
        { name: "공간 안내", link: "/facility/list" },
      ],
    },
    {
      name: "실증 지원",
      link: "/demonstration",
      sub: [
        { name: "실증 소개", link: "/demonstration/demInfo" },
        { name: "실증 물품", link: "/demonstration/list" },
      ],
    },
    {
      name: "알림마당",
      link: "/notice",
      sub: [
        { name: "공지사항", link: "/notice" },
        { name: "문의사항", link: "/question/select" },
        { name: "언론보도", link: "/news" },
      ],
    },
  ];

  if (loginState.role === "ADMIN") {
    mainMenus.push({
      name: "관리자",
      link: "/admin",
      sub: [
        { name: "회원 관리", link: "/admin/members" },
        { name: "배너 관리", link: "/admin/banner" },
        { name: "통계 확인", link: "/admin/stats" },
        { name: "휴무일 관리", link: "/facility/holiday" },
        { name: "메일 보내기", link: "/admin/adminSelectEmail" },
      ],
    });
    const eventMenu = mainMenus.find(menu => menu.name === "프로그램");
    eventMenu.sub.push({ name: "프로그램 추가", link: "/event/add" });
    const facilityMenu = mainMenus.find(menu => menu.name === "공간 운영");
    facilityMenu.sub.push(
      { name: "공간 추가", link: "/facility/add" },
      { name: "공간대여 관리", link: "/facility/adminreservations" },
    );
    const demonstrationMenu = mainMenus.find(menu => menu.name === "실증 지원");
    demonstrationMenu.sub.push(
      { name: "실증 등록", link: "/demonstration/add" },
      { name: "실증등록 관리", link: "/demonstration/adminReg" },
      { name: "실증대여 관리", link: "/demonstration/adminRes" }
    );
  }

  if (loginState.role === "TEACHER") {
    const teacherMenu = mainMenus.find(menu => menu.name === "실증 지원");
    teacherMenu.sub.push({ name: "실증 대여 내역", link: "/demonstration/rentalList" });
  }

  if (loginState.role === "COMPANY") {
    const companyMenu = mainMenus.find(menu => menu.name === "실증 지원");
    companyMenu.sub.push({ name: "실증 등록", link: "/demonstration/add" }, { name: "실증 등록 내역", link: "/demonstration/borrowList" });
  }

  return (
    <header className="fixed top-0 left-0 w-full bg-white shadow z-50 ">
      <div className="max-w-screen-xl mx-auto flex items-center justify-between h-20">
        {/* 로고 */}
        <div className="flex items-center flex-none xl:ml-4 md:ml-2 ml-0">
          <img
            src={logo}
            alt="로고"
            className="w-[240px] h-[100px] object-contain cursor-pointer"
            onClick={handleClickLogo}
          />
          {/* https://www.keris.or.kr/main/cm/cntnts/cntntsViewPop.do?cntntsId=1681 */}
        </div>

        {/* 메뉴 */}
        <nav className="relative group hidden lg:block flex-none w-[860px]">
          <div className="flex justify-between">
            {mainMenus.map((menu, idx) => (
              <div key={idx} className="relative text-center flex-1">
                <Link
                  to={menu.link}
                  className="text-lg font-semibold hover:text-blue-400 active:text-blue-600 block hover:scale-105 ease-in-out duration-300"
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
              transition-all duration-200 ease-in-out
              py-8 
            "
          >
            <div className={`mx-auto grid gap-6 text-base text-gray-700 px-4 ${loginState.role === "ADMIN" ? "grid-cols-6" : "grid-cols-5"}`}>
              {mainMenus.map((menu, idx) => (
                <div key={idx} className="space-y-3 text-center ">
                  {menu.sub.map((item, subIdx) => (
                    <div key={subIdx}>
                      <Link
                        to={item.link}
                        className="hover:text-blue-400 active:text-blue-600 block px-2 py-2 rounded hover:bg-blue-100 active:bg-blue-200 hover:scale-105 ease-in-out duration-300"
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
        <div className="flex items-center space-x-3 text-sm pl-3 pr-1 flex-none">
          {loginState && loginState.memId ? (
            <>
              <span
                className="hover:text-blue-400 cursor-pointer active:text-blue-600 hidden lg:block"
                onClick={handleLogout}
              >
                로그아웃
              </span>
              <span className="hover:text-blue-400 cursor-pointer active:text-blue-600 hidden lg:block">
                <Link to={getMyPageLink(loginState.role)}>내정보</Link>
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
        <div className="flex mx-2">
          {/* <img src={searchIcon} alt="검색" className="h-[24px] w-[24px] cursor-pointer active:bg-gray-200" /> */}
          {/* 아이콘 제작자<a href="https://www.flaticon.com/kr/authors/andy-horvath"title="Andy Horvath">
            Andy Horvath </a> from <a href="https://www.flaticon.com/kr/" title="Flaticon">www.flaticon.com'</a> */}
          <img src={sideIcon} alt="사이드" className="h-[24px] w-[24px] cursor-pointer active:bg-gray-200" onClick={handleOpenSidebar} />
          {/* <a href="https://www.flaticon.com/kr/free-icons/" title="햄버거 아이콘">햄버거 아이콘 제작자: Lizel Arina - Flaticon</a> */}
        </div>

      </div>

      <Side isOpen={isSidebarOpen} onClose={handleCloseSidebar} />
    </header>
  );
};

export default Header;