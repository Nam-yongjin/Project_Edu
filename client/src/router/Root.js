import { Suspense, lazy } from "react";
import Loading from "./Loading";

const { createBrowserRouter } = require("react-router-dom")

const Main = lazy(() => import("../pages/MainPage"))

const root = createBrowserRouter([
    {
        path: "",
        element: <Suspense fallback={Loading}><Main /></Suspense>
    },
])

export default root
