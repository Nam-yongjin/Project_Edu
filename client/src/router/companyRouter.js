import Loading from "./Loading";
import { Suspense, lazy } from "react";

const Info = lazy(() => import("../pages/member/company/companyInfoPage"))
const Modify = lazy(() => import("../pages/member/company/companyModifyPage"))

const companyRouter = () => {

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

export default companyRouter