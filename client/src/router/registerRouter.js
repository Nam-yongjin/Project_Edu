import Loading from "./Loading";
import { Suspense, lazy } from "react";


const Member = lazy(() => import("../pages/member/memberRegisterPage"))
const Student = lazy(() => import("../pages/member/studentRegisterPage"))
const Teacher = lazy(() => import("../pages/member/teacherRegisterPage"))
const Company = lazy(() => import("../pages/member/companyRegisterPage"))

const registerRouter = () => {

    return [
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