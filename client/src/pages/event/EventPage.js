import { Outlet, useLocation } from "react-router-dom"
import BasicLayout from "../../layouts/BasicLayout"
import SubEventHeader from "../../layouts/SubEventHeader"

const EventPage = () => {
    const location = useLocation();

    const isReservationPage = location.pathname.includes("/event/Reservation");

    return (
        <div>
            <BasicLayout isFullWidth={true}>
                {!isReservationPage && <SubEventHeader />}
                <Outlet />
            </BasicLayout>
        </div>
    )
}
export default EventPage