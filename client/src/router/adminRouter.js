import Loading from "./Loading";
import { Suspense, lazy } from "react";
import { Navigate } from "react-router-dom";

const Members = lazy(() => import("../pages/admin/AdminMembersPage"));

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
        }
    ];
};
export default adminRouter;