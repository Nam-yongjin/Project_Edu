import Loading from "./Loading";
import { Suspense, lazy } from "react";

const Info = lazy(() => import("../pages/member/student/studentInfoPage"));
const Modify = lazy(() => import("../pages/member/student/studentModifyPage"));

const studentRouter = () => {

    return [
        {
            path: "myInfo",
            element: <Suspense fallback={<Loading />}><Info /></Suspense>,
        },
        {
            path: "modify",
            element: <Suspense fallback={<Loading />}><Modify /></Suspense>,
        },

    ];

};

export default studentRouter;