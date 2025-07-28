import Loading from "./Loading";
import { Suspense, lazy } from "react";

const studentRouter = () => {

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

export default studentRouter