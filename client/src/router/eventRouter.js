import Loading from "./Loading";
import { Suspense, lazy } from "react";
import { Navigate } from "react-router-dom";

const EventAddPage = lazy(() => import("../pages/event/EventAddPage"))
const EventList = lazy(() => import("../pages/event/EventListPage"))
const EventDetail = lazy(() => import("../pages/event/EventDetailPage"))
const EventUpdate = lazy(() => import("../pages/event/EventUpdatePage"))
const EventReservation = lazy(() => import("../pages/event/EventReservationPage"))
const EventBannerPage = lazy(() => import("../pages/event/EventBannerPage"))

const eventRouter = () => {

    return [
        {
            path:"add",
            element: <Suspense fallback={<Loading />}><EventAddPage /></Suspense>,
        },
        {
             path:"list",
            element: <Suspense fallback={<Loading />}><EventList /></Suspense>,
        },
        {
            path: "detail/:eventNum",
            element: <Suspense fallback={<Loading />}><EventDetail /></Suspense>,
        },
        {
            path: "update/:eventNum",
            element: <Suspense fallback={<Loading />}><EventUpdate /></Suspense>,
        },
        {
             path:"reservation",
            element: <Suspense fallback={<Loading />}><EventReservation /></Suspense>,
        },
        {
             path:"banner",
            element: <Suspense fallback={<Loading />}><EventBannerPage /></Suspense>,
        },
        {
            // 자동 리다이렉션
            path: "",
            element: <Navigate replace to="list" />
        },
    ]

}

export default eventRouter;