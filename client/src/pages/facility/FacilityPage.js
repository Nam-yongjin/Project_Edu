import { Outlet, useLocation } from "react-router-dom"
import BasicLayout from "../../layouts/BasicLayout"
import SubFacilityHeader from "../../layouts/SubFacilityHeader"

const FacilityPage = () => {

    const location = useLocation();

    const isReservationPage = location.pathname.includes("facility/reservation");
    const isHolidayPage = location.pathname.includes("facility/holiday");

    return (
        <div>
            <BasicLayout  isFullWidth={true}>
                {!isReservationPage && !isHolidayPage && <SubFacilityHeader />}
                <Outlet />
            </BasicLayout>
        </div>
    )
}
export default FacilityPage