import Loading from "./Loading";
import { Suspense, lazy } from "react";
import { Navigate } from "react-router-dom";

const Members = lazy(() => import("../pages/admin/AdminMembersPage"));
const Banner = lazy(() => import("../pages/admin/AdminBannerPage"));
const Stats = lazy(() => import("../pages/admin/StatsPage"));
const AdminRes = lazy(() => import("../pages/admin/AdminResPage"));
const AdminReg = lazy(() => import("../pages/admin/AdminRegPage"));
const AdminEmail=lazy(() => import("../pages/admin/AdminEmailPage"));
const AdminSelectEmail=lazy(() => import("../pages/admin/AdminEmailSelectMembersPage"));
const adminRouter = () => {
    return [
        {
            path: "members",
            element: <Suspense fallback={<Loading />}><Members /></Suspense>,
        },
        {
            // 자동 리다이렉션
            path: "",
            element: <Navigate replace to="members" />
        },
        {
            path: "banner",
            element: <Suspense fallback={<Loading />}><Banner /></Suspense>,
        },
        {
            path: "stats",
            element: <Suspense fallback={<Loading />}><Stats /></Suspense>
        },
         {
            path:"adminRes",
            element: <Suspense fallback={<Loading />}><AdminRes /></Suspense>
        },
        {
            path:"adminReg",
            element: <Suspense fallback={<Loading />}><AdminReg /></Suspense>
        },
         {
            path:"adminEmail",
            element: <Suspense fallback={<Loading />}><AdminEmail /></Suspense>
        },
        {
            path:"adminSelectEmail",
            element: <Suspense fallback={<Loading />}><AdminSelectEmail /></Suspense>
        }
    ];
};
export default adminRouter;