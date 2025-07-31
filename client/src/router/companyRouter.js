import Loading from "./Loading";
import { Suspense, lazy } from "react";

const InfoModify = lazy(() => import("../pages/member/company/companyInfoModifyPage"));

const companyRouter = () => {

    return [
        {
            path: "myInfo",
            element: <Suspense fallback={<Loading />}><InfoModify /></Suspense>,
        },

    ];

};

export default companyRouter;