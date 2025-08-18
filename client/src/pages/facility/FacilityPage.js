import { Outlet, useLocation } from "react-router-dom"
import BasicLayout from "../../layouts/BasicLayout"
import SubFacilityHeader from "../../layouts/SubFacilityHeader"

const FacilityPage = () => {

    const location = useLocation();

    const isReservationPage = location.pathname.includes("facility/reservation");

    return (
        <div>
            <BasicLayout  isFullWidth={true}>
                {!isReservationPage && <SubFacilityHeader />}
                <Outlet />
            </BasicLayout>
        </div>
    )
}
export default FacilityPage