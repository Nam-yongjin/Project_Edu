import Loading from "./Loading";
import { Suspense, lazy } from "react";


const Member = lazy(() => import("../pages/member/memberRegisterPage"))


const memberRouter = () => {

    return [
        {
            path: "myInfo",
            element: <Suspense fallback={<Loading />}></Suspense>,
        },
        {
            path: "modify",
            element: <Suspense fallback={<Loading />}></Suspense>,
        },

    ]

}

export default memberRouter