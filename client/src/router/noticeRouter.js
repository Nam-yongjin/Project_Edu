import Loading from "./Loading";
import { Suspense, lazy } from "react";
import { Navigate } from "react-router-dom";
//파일명 소문자/대문자로 바꿨을 때 오류나면
//파일명을 우회해서 바꿔줘야 함
//noticRouter -> tempRouter -> noticeRouter
const NoticeList = lazy(() => import("../pages/notice/NoticeListPage"))
const AddNotice = lazy(() => import("../pages/notice/AddNoticePage"))
const UpdateNotice = lazy(() => import("../pages/notice/UpdateNoticePage"))
const NoticeDetail = lazy(() => import("../pages/notice/NoticeDetailPage"))

const noticeRouter = () => {

    return [
        {
             path:"",
            element: <Suspense fallback={<Loading />}><NoticeList /></Suspense>,
        },
        {
            // 자동 리다이렉션
            path: "",
            element: <Navigate replace to="notice" />
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