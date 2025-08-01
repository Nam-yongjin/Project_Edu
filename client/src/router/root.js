import { Suspense, lazy } from "react";
import Loading from "./Loading";
import { createBrowserRouter } from "react-router-dom";
import registerRouter from "./registerRouter";
import memberRouter from "./memberRouter";
import studentRouter from "./studentRouter";
import teacherRouter from "./teacherRouter";
import companyRouter from "./companyRouter";
import demonstrationRouter from "./demonstrationRouter";
import noticeRouter from "./noticeRouter";
import eventRouter from "./eventRouter";
import aboutRouter from "./aboutRouter";

const Main = lazy(() => import("../pages/MainPage"))
const Register = lazy(() => import("../pages/member/RegisterPage"))
const Login = lazy(() => import("../pages/member/LoginPage"))
const Member = lazy(() => import("../pages/member/MemberPage"))
const Student = lazy(() => import("../pages/member/student/StudentPage"))
const Teacher = lazy(() => import("../pages/member/teacher/TeacherPage"))
const Company = lazy(() => import("../pages/member/company/CompanyPage"))
const FindId = lazy(() => import("../pages/member/FindIdPage"))
const ResetPw = lazy(() => import("../pages/member/ResetPwPage"))
const KakaoRedirect = lazy(() => import("../pages/member/KakaoRedirectPage"))
const NaverRedirect = lazy(() => import("../pages/member/NaverRedirectPage"))
const Demonstration = lazy(() => import("../pages/demonstration/demonstrationPage"))
const Notice = lazy(() => import("../pages/notice/noticePage"))
const Event = lazy(() => import("../pages/event/eventPage"))
const About = lazy(() => import("../pages/about/AboutPage"))


const root = createBrowserRouter([
    {
        path: "",
        element: <Suspense fallback={<Loading />}><Main /></Suspense>
    },
    {
        path: "register",
        element: <Suspense fallback={<Loading />}><Register /></Suspense>,
        children: registerRouter()
    },
    {
        path: "login",
        element: <Suspense fallback={<Loading />}><Login /></Suspense>,
        children: [
            {
                path: "kakao",
                element: <Suspense fallback={<Loading />}><KakaoRedirect /></Suspense>,
            },
            {
                path: "naver",
                element: <Suspense fallback={<Loading />}><NaverRedirect /></Suspense>,
            },
        ]
    },
    {
        path: "member",
        element: <Suspense fallback={<Loading />}><Member /></Suspense>,
        children: memberRouter()
    },
    {
        path: "student",
        element: <Suspense fallback={<Loading />}><Student /></Suspense>,
        children: studentRouter()
    },
    {
        path: "teacher",
        element: <Suspense fallback={<Loading />}><Teacher /></Suspense>,
        children: teacherRouter()
    },
    {
        path: "company",
        element: <Suspense fallback={<Loading />}><Company /></Suspense>,
        children: companyRouter()
    },
    {
        path: "findId",
        element: <Suspense fallback={<Loading />}><FindId /></Suspense>,
    },
    {
        path: "resetPw",
        element: <Suspense fallback={<Loading />}><ResetPw /></Suspense>,
    },
    {
        path: "demonstration",
        element: <Suspense fallback={<Loading />}><Demonstration /></Suspense>,
        children: demonstrationRouter()
    },
    {
        path: "notice",
        element: <Suspense fallback={<Loading />}><Notice /></Suspense>,
        children: noticeRouter()
    },
    {
        path: "event",
        element: <Suspense fallback={<Loading />}><Event /></Suspense>,
        children: eventRouter()
    },
    {
        path: "about",
        element: <Suspense fallback={<Loading />}><About /></Suspense>,
        children: aboutRouter()
    }

])

export default root
