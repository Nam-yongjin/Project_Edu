import Loading from "./Loading";
import { Suspense, lazy } from "react";
import { Navigate } from "react-router-dom";

const Role = lazy(() => import("../pages/member/RegisterRolePage"))
const Member = lazy(() => import("../pages/member/memberRegisterPage"))
const Student = lazy(() => import("../pages/member/student/studentRegisterPage"))
const Teacher = lazy(() => import("../pages/member/teacher/teacherRegisterPage"))
const Company = lazy(() => import("../pages/member/company/companyRegisterPage"))

const registerRouter = () => {

    return [
        {
            path: "role",
            element: <Suspense fallback={<Loading />}><Role /></Suspense>,
        },
        {
            // 자동 리다이렉션
            path: "",
            element: <Navigate replace to="role" />
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

    ]

}

export default registerRouter