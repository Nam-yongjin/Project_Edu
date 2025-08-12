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
import facilityRouter from "./facilityRouter";
import aboutRouter from "./aboutRouter";
import adminRouter from "./adminRouter";
import newsRouter from "./newsRouter";
import qnaRouter from "./qnaRouter";

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
const Facility = lazy(() => import("../pages/facility/facilityPage"))
const About = lazy(() => import("../pages/about/AboutPage"))
const Admin = lazy(() => import("../pages/admin/AdminPage"))
const News = lazy(() => import("../pages/news/newsPage"))
const QnA = lazy(() => import("../pages/qna/QnAPage"))

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
        path: "facility",
        element: <Suspense fallback={<Loading />}><Facility /></Suspense>,
        children: facilityRouter()
    },
    {
        path: "about",
        element: <Suspense fallback={<Loading />}><About /></Suspense>,
        children: aboutRouter()
    },
    {
        path: "admin",
        element: <Suspense fallback={<Loading />}><Admin /></Suspense>,
        children: adminRouter()
    },
    {
        path: "news",
        element: <Suspense fallback={<Loading />}><News /></Suspense>,
        children: newsRouter()
    },
    {
        path: "question",
        element: <Suspense fallback={<Loading />}><QnA /></Suspense>,
        children: qnaRouter()
    }

])

export default root
