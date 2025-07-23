import { Navigate } from "react-router-dom";
import Loading from "./Loading";
import { Suspense, lazy } from "react";


const KakaoRedirect = lazy(() => import("../pages/member/KakaoRedirectPage"))

const loginRouter = () => {

    return [
        {
            path: "",
            element: <Navigate replace to="login"/>,
        },
        {
            path: "kakao",
            element: <Suspense fallback={<Loading />}><KakaoRedirect /></Suspense>,
        },

    ]

}

export default loginRouter