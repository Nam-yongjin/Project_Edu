import Loading from "./Loading";
import { Suspense, lazy } from "react";


const UpdateDem = lazy(() => import("../pages/demonstration/UpdateDemPage"))
const AddDem = lazy(() => import("../pages/demonstration/AddDemPage"))
const DemDetail = lazy(() => import("../pages/demonstration/DemDetailPage"))
const DemList = lazy(() => import("../pages/demonstration/DemListPage"))

const demonstrationRouter = () => {

    return [
        {
            path: "add",
            element: <Suspense fallback={<Loading />}><AddDem /></Suspense>,
        },
        {
            path: "update/:demNum",
            element: <Suspense fallback={<Loading />}><UpdateDem /></Suspense>,
        },
        {
            path:"detail",
            element: <Suspense fallback={<Loading />}><DemDetail /></Suspense>
        },
        {
             path:"list",
            element: <Suspense fallback={<Loading />}><DemList /></Suspense>
        }
    ]

}

export default demonstrationRouter