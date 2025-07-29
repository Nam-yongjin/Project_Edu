import Loading from "./Loading";
import { Suspense, lazy } from "react";

const Modify = lazy(() => import("../pages/member/company/companyModifyPage"))

const companyRouter = () => {

    return [
        {
            path: "myInfo",
            element: <Suspense fallback={<Loading />}></Suspense>,
        },
        {
            path: "modify",
            element: <Suspense fallback={<Loading />}><Modify /></Suspense>,
        },

    ]

}

export default companyRouter