import Loading from "./Loading";
import { Suspense, lazy } from "react";
import { Navigate } from "react-router-dom";

const FacilityAddPage = lazy(() => import("../pages/facility/FacilityAddPage"))

const facilityRouter = () => {

    return [
        {
            path:"add",
            element: <Suspense fallback={<Loading />}><FacilityAddPage /></Suspense>,
        },
        {
            // 자동 리다이렉션
            path: "",
            element: <Navigate replace to="add" />
        },
    ]

}

export default facilityRouter;