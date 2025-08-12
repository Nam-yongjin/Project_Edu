import Loading from "./Loading";
import { Suspense, lazy } from "react";
import { Navigate } from "react-router-dom";

const QAdd = lazy(() => import("../pages/qna/AddQPage"))
const QDetail = lazy(() => import("../pages/qna/QDetailPage"))
const QSelect = lazy(() => import("../pages/qna/SelectQPage"))

const qnaRouter = () => {

    return [
        {
            path: "add",
            element: <Suspense fallback={<Loading />}><QAdd /></Suspense>,
        },
        {
            path: "detail",
            element: <Suspense fallback={<Loading />}><QDetail /></Suspense>,
        },
        {
            path: "select",
            element: <Suspense fallback={<Loading />}><QSelect /></Suspense>,
        },
        {
            // 자동 리다이렉션
            path: "",
            element: <Navigate replace to="detail" />
        }
    ]

}

export default qnaRouter;