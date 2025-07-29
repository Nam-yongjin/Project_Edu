import Loading from "./Loading";
import { Suspense, lazy } from "react";


const NoticeList = lazy(() => import("../pages/notice/NoticeListPage"))
const NoticeDetail = lazy(() => import("../pages/notice/NoticeDetailPage"))
const AddNotice = lazy(() => import("../pages/notice/AddNoticePage"))
const UpdateNotice = lazy(() => import("../pages/notice/UpdateNoticePage"))

const noticeRouter = () => {

    return [
        {
             path:"list",
            element: <Suspense fallback={<Loading />}><NoticeList /></Suspense>,
        },
         {
            path:"detail",
            element: <Suspense fallback={<Loading />}><NoticeDetail /></Suspense>,
        },
        {
            path: "add",
            element: <Suspense fallback={<Loading />}><AddNotice /></Suspense>,
        },
        {
            path: "update",
            element: <Suspense fallback={<Loading />}><UpdateNotice /></Suspense>,
        }     
        
    ]

}

export default noticeRouter;