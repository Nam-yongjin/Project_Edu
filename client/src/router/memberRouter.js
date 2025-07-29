import Loading from "./Loading";
import { Suspense, lazy } from "react";

const Info = lazy(() => import("../pages/member/memberInfoPage"))
const Modify = lazy(() => import("../pages/member/memberModifyPage"))

const memberRouter = () => {

    return [
        {
            path: "myInfo",
            element: <Suspense fallback={<Loading />}><Info /></Suspense>,
        },
        {
            path: "modify",
            element: <Suspense fallback={<Loading />}><Modify /></Suspense>,
        },

    ]

}

export default memberRouter