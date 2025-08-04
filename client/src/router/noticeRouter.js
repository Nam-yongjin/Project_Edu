import Loading from "./Loading";
import { Suspense, lazy } from "react";


const NoticeList = lazy(() => import("../pages/notice/NoticeListPage"))
const AddNotice = lazy(() => import("../pages/notice/AddNoticePage"))
const UpdateNotice = lazy(() => import("../pages/notice/UpdateNoticePage"))
const NoticeDetail = lazy(() => import("../pages/notice/NoticeDetailPage"))

const noticeRouter = () => {

    return [
        {
             path:"list",
            element: <Suspense fallback={<Loading />}><NoticeList /></Suspense>,
        },
        {
            path: "add",
            element: <Suspense fallback={<Loading />}><AddNotice /></Suspense>,
        },
        {
            path: "update/:noticeNum",
            element: <Suspense fallback={<Loading />}><UpdateNotice /></Suspense>,
        },    
        {
            path:"detail/:noticeNum",
            element: <Suspense fallback={<Loading />}><NoticeDetail /></Suspense>,
        }
    ]

}

export default noticeRouter;