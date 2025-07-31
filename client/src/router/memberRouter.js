import Loading from "./Loading";
import { Suspense, lazy } from "react";

const InfoModify = lazy(() => import("../pages/member/memberInfoModifyPage"));

const memberRouter = () => {

    return [
        {
            path: "myInfo",
            element: <Suspense fallback={<Loading />}><InfoModify /></Suspense>,
        },

    ];

};

export default memberRouter;