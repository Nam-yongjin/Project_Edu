import Loading from "./Loading";
import { Suspense, lazy } from "react";

const Greeting = lazy(() => import("../pages/about/greetingPage"));
const Business = lazy(() => import("../pages/about/businessPage"));
const Direction = lazy(() => import("../pages/about/directionPage"));

const aboutRouter = () => {

    return [
        {
            path: "greeting",
            element: <Suspense fallback={<Loading />}><Greeting /></Suspense>,
        },
        {
            path: "business",
            element: <Suspense fallback={<Loading />}><Business /></Suspense>,
        },
        {
            path:"direction",
            element: <Suspense fallback={<Loading />}><Direction /></Suspense>
        }
    ];

};

export default aboutRouter;