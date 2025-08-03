import Loading from "./Loading";
import { Suspense, lazy } from "react";

const EventAddPage = lazy(() => import("../pages/event/EventAddPage"))
const EventList = lazy(() => import("../pages/event/EventListPage"))
const EventDetail = lazy(() => import("../pages/event/EventDetailPage"))
const EventUpdate = lazy(() => import("../pages/event/EventUpdatePage"))

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
        }
    ]

}

export default eventRouter;