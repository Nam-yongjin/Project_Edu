import Loading from "./Loading";
import { Suspense, lazy } from "react";
import { Navigate } from "react-router-dom";

const FacilityAddPage = lazy(() => import("../pages/facility/FacilityAddPage"))
const FacilityListPage = lazy(() => import("../pages/facility/FacilityListPage"))
const FacilityDetailPage = lazy(() => import("../pages/facility/FacilityDetailPage"))
const FacilityHolidayPage = lazy(() => import("../pages/facility/FacilityHolidayPage"))
const FacilityReservationPage = lazy(() => import("../pages/facility/FacilityReservationPage"))
const FacilityAdminReservationPage = lazy(() => import("../pages/facility/FacilityAdminReservationPage"))

const facilityRouter = () => {

    return [
        {
            path:"add",
            element: <Suspense fallback={<Loading />}><FacilityAddPage /></Suspense>,
        },
        {
            path:"list",
            element: <Suspense fallback={<Loading />}><FacilityListPage /></Suspense>,
        },
        {
            path:"detail/:facRevNum",
            element: <Suspense fallback={<Loading />}><FacilityDetailPage /></Suspense>,
        },
        {
            path:"holiday",
            element: <Suspense fallback={<Loading />}><FacilityHolidayPage /></Suspense>,
        },
        {
            path:"reservation",
            element: <Suspense fallback={<Loading />}><FacilityReservationPage /></Suspense>,
        },
        {
            path:"adminreservations",
            element: <Suspense fallback={<Loading />}><FacilityAdminReservationPage /></Suspense>,
        },
        {
            // 자동 리다이렉션
            path: "",
            element: <Navigate replace to="list" />
        },
    ]

}

export default facilityRouter;