import Loading from "./Loading";
import { Suspense, lazy } from "react";
import { Navigate } from "react-router-dom";

const UpdateDem = lazy(() => import("../pages/demonstration/UpdateDemPage"))
const AddDem = lazy(() => import("../pages/demonstration/AddDemPage"))
const DemDetail = lazy(() => import("../pages/demonstration/DemDetailPage"))
const DemList = lazy(() => import("../pages/demonstration/DemListPage"))
const RentalList = lazy(() => import("../pages/demonstration/DemRentalPage"))
const BorrowList = lazy(() => import("../pages/demonstration/DemBorrowPage"))
const Info=lazy(() => import("../pages/demonstration/InfoPage"))
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
            path: "detail/:demNum",
            element: <Suspense fallback={<Loading />}><DemDetail /></Suspense>
        },
        {
            path: "list",
            element: <Suspense fallback={<Loading />}><DemList /></Suspense>
        },
        {
            path: "rentalList",
            element: <Suspense fallback={<Loading />}><RentalList /></Suspense>
        },
        {
            path: "borrowList",
            element: <Suspense fallback={<Loading />}><BorrowList /></Suspense>
        },
        {
            path: "demInfo",
            element: <Suspense fallback={<Loading />}><Info /></Suspense>
        },
        {
            // 자동 리다이렉션
            path: "",
            element: <Navigate replace to="demInfo" />
        }
    ]

}

export default demonstrationRouter