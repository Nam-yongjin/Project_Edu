import Loading from "./Loading";
import { Suspense, lazy } from "react";
import { Navigate } from "react-router-dom";

const NewsList = lazy(() => import("../pages/news/NewsListPage"))
const AddNews = lazy(() => import("../pages/news/AddNewsPage"))
const UpdateNews = lazy(() => import("../pages/news/UpdateNewsPage"))
const NewsDetail = lazy(() => import("../pages/news/NewsDetailPage"))

const newsRouter = () => {

    return [
        {
             path:"NewsList",
            element: <Suspense fallback={<Loading />}><NewsList /></Suspense>,
        },
        {
            // 자동 리다이렉션
            path: "",
            element: <Navigate replace to="NewsList" />
        },
        {
            path: "AddNews",
            element: <Suspense fallback={<Loading />}><AddNews /></Suspense>,
        },
        {
            path: "UpdateNews/:newsNum",
            element: <Suspense fallback={<Loading />}><UpdateNews /></Suspense>,
        },    
        {
            path:"NewsDetail/:newsNum",
            element: <Suspense fallback={<Loading />}><NewsDetail /></Suspense>,
        }
    ]

}

export default newsRouter;