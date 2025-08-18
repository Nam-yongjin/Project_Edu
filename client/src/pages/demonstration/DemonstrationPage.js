import { Outlet, useLocation } from "react-router-dom"
import BasicLayout from "../../layouts/BasicLayout"
import SubDemonstrationHeader from "../../layouts/SubDemonstrationHeader"

const DemonstrationPage = () => {
    const location = useLocation();

    const isBorrowPage = location.pathname.includes("/demonstration/borrowList");
    const isRentalPage = location.pathname.includes("/demonstration/rentalList");

    return (
        <div>
            <BasicLayout isFullWidth={true}>
                {!isBorrowPage && !isRentalPage && <SubDemonstrationHeader/>}
                <Outlet/>
            </BasicLayout>
        </div>
    )
}
export default DemonstrationPage