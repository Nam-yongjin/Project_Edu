import Loading from "./Loading";
import { Suspense, lazy } from "react";

const InfoModify = lazy(() => import("../pages/member/student/studentInfoModifyPage"));

const studentRouter = () => {

    return [
        {
            path: "myInfo",
            element: <Suspense fallback={<Loading />}><InfoModify /></Suspense>,
        },

    ];

};

export default studentRouter;