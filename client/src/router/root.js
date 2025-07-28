import { Suspense, lazy } from "react";
import Loading from "./Loading";
import { createBrowserRouter } from "react-router-dom";
import registerRouter from "./registerRouter";
import memberRouter from "./memberRouter";
import demonstrationRouter from "./demonstrationRouter";
import noticeRouter from "./noticeRouter";
import eventRouter from "./eventRouter";

const Main = lazy(() => import("../pages/MainPage"))
const Register = lazy(() => import("../pages/member/RegisterPage"))
const Login = lazy(() => import("../pages/member/LoginPage"))
// const Member = lazy(() => import("../pages/member/Memberpage"))
const FindId = lazy(() => import("../pages/member/FindIdPage"))
const ResetPw = lazy(() => import("../pages/member/ResetPwPage"))
const KakaoRedirect = lazy(() => import("../pages/member/KakaoRedirectPage"))
const Demonstration = lazy(() => import("../pages/demonstration/demonstrationPage"))
const Notice = lazy(() => import("../pages/notice/noticePage"))
const Event = lazy(() => import("../pages/event/eventPages"))


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
        ]
    },
    // {
    //     path: "member",
    //     element: <Suspense fallback={<Loading />}><Member /></Suspense>,
    //     children: memberRouter()
    // },
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
        element: <Suspense fallbackk={<Loading />}><Event /></Suspense>,
        children: eventRouter()
    }

])

export default root
