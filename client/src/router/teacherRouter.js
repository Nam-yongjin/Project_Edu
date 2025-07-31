import Loading from "./Loading";
import { Suspense, lazy } from "react";

const InfoModify = lazy(() => import("../pages/member/teacher/TeacherInfoModifyPage"));

const teacherRouter = () => {

    return [
        {
            path: "myInfo",
            element: <Suspense fallback={<Loading />}><InfoModify /></Suspense>,
        },

    ];

};

export default teacherRouter;