import Loading from "./Loading";
import { Suspense, lazy } from "react";

const Info = lazy(() => import("../pages/member/teacher/teacherInfoPage"))
const Modify = lazy(() => import("../pages/member/teacher/teacherModifyPage"))

const teacherRouter = () => {

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

export default teacherRouter