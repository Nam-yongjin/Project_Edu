import Loading from "./Loading";
import { Suspense, lazy } from "react";
import { Navigate } from "react-router-dom";

const InfoModify = lazy(() => import("../pages/member/company/CompanyInfoModifyPage"));

const companyRouter = () => {

    return [
        {
            path: "myInfo",
            element: <Suspense fallback={<Loading />}><InfoModify /></Suspense>,
        },
        {
            // 자동 리다이렉션
            path: "",
            element: <Navigate replace to="myInfo" />
        },
    ];

};

export default companyRouter;