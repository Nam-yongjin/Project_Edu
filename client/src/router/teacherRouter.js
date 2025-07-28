import Loading from "./Loading";
import { Suspense, lazy } from "react";

const teacherRouter = () => {

    return [
        {
            path: "myInfo",
            element: <Suspense fallback={<Loading />}></Suspense>,
        },
        {
            path: "modify",
            element: <Suspense fallback={<Loading />}></Suspense>,
        },

    ]

}

export default teacherRouter