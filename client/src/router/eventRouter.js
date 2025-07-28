import Loading from "./Loading";
import { Suspense, lazy } from "react";


const EventList = lazy(() => import("../pages/event/EventListPage"))
const EventDetail = lazy(() => import("../pages/event/EventDetailPage"))

const eventRouter = () => {

    return [
        {
             path:"evList",
            element: <Suspense fallback={<Loading />}><evList /></Suspense>,
        } ,
         {
            path:"evDetail",
            element: <Suspense fallback={<Loading />}><evDetail /></Suspense>,
        }
    ]

}

export default eventRouter;