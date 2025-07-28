import Loading from "./Loading";
import { Suspense, lazy } from "react";


const EventList = lazy(() => import("../pages/event/EventListPage"))
const EventDetail = lazy(() => import("../pages/event/EventDetailPage"))

const eventRouter = () => {

    return [
        {
             path:"EventList",
            element: <Suspense fallback={<Loading />}><EventList /></Suspense>,
        } ,
         {
            path:"EventDetail",
            element: <Suspense fallback={<Loading />}><EventDetail /></Suspense>,
        }
    ]

}

export default eventRouter;