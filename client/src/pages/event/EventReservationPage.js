import "react-datepicker/dist/react-datepicker.css";
import EvtReservation from "../../components/event/EvtReservation";
import SubMyInfoHeader from "../../layouts/SubMyInfoHeader";

const EventReservationPage = () => {
    return (
        <div>
            <SubMyInfoHeader />
            <EvtReservation />
        </div>
    );
}
export default EventReservationPage;