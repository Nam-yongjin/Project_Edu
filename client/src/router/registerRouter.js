import Loading from "./Loading";
import { Suspense, lazy } from "react";
import { Navigate } from "react-router-dom";

const Terms = lazy(() => import("../pages/member/RegisterTermsPage"));
const Member = lazy(() => import("../pages/member/MemberRegisterPage"));
const Student = lazy(() => import("../pages/member/student/StudentRegisterPage"));
const Teacher = lazy(() => import("../pages/member/teacher/TeacherRegisterPage"));
const Company = lazy(() => import("../pages/member/company/CompanyRegisterPage"));

const registerRouter = () => {

    return [
        {
            path: "terms",
            element: <Suspense fallback={<Loading />}><Terms /></Suspense>,
        },
        {
            // 자동 리다이렉션
            path: "",
            element: <Navigate replace to="terms" />
        },
        {
            path: "member",
            element: <Suspense fallback={<Loading />}><Member /></Suspense>,
        },
        {
            path: "student",
            element: <Suspense fallback={<Loading />}><Student /></Suspense>,
        },
        {
            path: "teacher",
            element: <Suspense fallback={<Loading />}><Teacher /></Suspense>,
        },
        {
            path: "company",
            element: <Suspense fallback={<Loading />}><Company /></Suspense>,
        },

    ];

};

export default registerRouter;