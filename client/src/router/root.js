import { Suspense, lazy } from "react";
import Loading from "./Loading";
import { createBrowserRouter } from "react-router-dom";
import memberRouter from "./memberRouter";

const Main = lazy(() => import("../pages/MainPage"))
const Register = lazy(() => import("../pages/member/RegisterPage"))
const Login = lazy(() => import("../pages/member/LoginPage"))
const KakaoRedirect = lazy(() => import("../pages/member/KakaoRedirectPage"))

const root = createBrowserRouter([
    {
        path: "",
        element: <Suspense fallback={<Loading />}><Main /></Suspense>
    },
    {
        path: "register",
        element: <Suspense fallback={<Loading />}><Register /></Suspense>
    },
    {
        path: "login",
        element: <Suspense fallback={<Loading />}><Login /></Suspense>,
        children: [
            {
                path: "kakao",
                element: <Suspense fallback={<Loading />}><KakaoRedirect /></Suspense>,
            }
        ]
    },

])

export default root
