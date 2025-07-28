import Loading from "./Loading";
import { Suspense, lazy } from "react";


const EventList = lazy(() => import("../pages/event/EventListPage"))
const EventDetail = lazy(() => import("../pages/event/EventDetailPage"))
const EventAddPage = lazy(() => import("../pages/event/EventAddPage"))

const eventRouter = () => {

    return [
        {
             path:"List",
            element: <Suspense fallback={<Loading />}><EventList /></Suspense>,
        },
         {
            path:"Detail",
            element: <Suspense fallback={<Loading />}><EventDetail /></Suspense>,
        },
        {
            path:"Add",
            element: <Suspense fallback={<Loading />}><EventAddPage /></Suspense>,
        }

    ]

}

export default eventRouter;