import Loading from "./Loading";
import { Suspense, lazy } from "react";


const NoticeList = lazy(() => import("../pages/notice/NoticeListPage"))
const NoticeDetail = lazy(() => import("../pages/notice/NoticeDetailPage"))
const AddNotice = lazy(() => import("../pages/notice/AddNoticePage"))
const UpdateNotice = lazy(() => import("../pages/notice/UpdateNoticePage"))

const noticeRouter = () => {

    return [
        {
             path:"noticeList",
            element: <Suspense fallback={<Loading />}><NoticeList /></Suspense>,
        },
         {
            path:"noticeDetail",
            element: <Suspense fallback={<Loading />}><NoticeDetail /></Suspense>,
        },
        {
            path: "addNotice",
            element: <Suspense fallback={<Loading />}><AddNotice /></Suspense>,
        },
        {
            path: "updateNotice",
            element: <Suspense fallback={<Loading />}><UpdateNotice /></Suspense>,
        }     
        
    ]

}

export default noticeRouter;