import Loading from "./Loading";
import { Suspense, lazy } from "react";
import { Navigate } from "react-router-dom";

const Greeting = lazy(() => import("../pages/about/GreetingPage"));
const Business = lazy(() => import("../pages/about/BusinessPage"));
const Direction = lazy(() => import("../pages/about/DirectionPage"));

const aboutRouter = () => {

    return [
        {
            path: "greeting",
            element: <Suspense fallback={<Loading />}><Greeting /></Suspense>,
        },
        {
            // 자동 리다이렉션
            path: "",
            element: <Navigate replace to="greeting" />
        },
        {
            path: "business",
            element: <Suspense fallback={<Loading />}><Business /></Suspense>,
        },
        {
            path: "direction",
            element: <Suspense fallback={<Loading />}><Direction /></Suspense>
        }
    ];

};

export default aboutRouter;