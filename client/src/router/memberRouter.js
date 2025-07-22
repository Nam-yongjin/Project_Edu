import Loading from "./Loading";
import { Suspense, lazy } from "react";


// const KakaoRedirect = lazy(() => import("../pages/member/KakaoRedirectPage"))

const memberRouter = () => {

    return [
        
        // {
        //     path: "kakao",
        //     element: <Suspense fallback={<Loading />}><KakaoRedirect /></Suspense>,
        // },

    ]

}

export default memberRouter