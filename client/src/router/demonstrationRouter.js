import Loading from "./Loading";
import { Suspense, lazy } from "react";


const demonstrationRouter = () => {

    return [
        {
            path: "addDem",
            element: <Suspense fallback={<Loading />}></Suspense>,
        },
        {
            path: "updateDem",
            element: <Suspense fallback={<Loading />}></Suspense>,
        },

    ]

}

export default demonstrationRouter