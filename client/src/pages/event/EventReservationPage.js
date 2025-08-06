import EvtTitleComponent from "../../components/event/EvtTitleComponent";
import "react-datepicker/dist/react-datepicker.css";
import EvtReservation from "../../components/event/EvtReservation";

const EventReservationPage = () => {
    return (
        <div>
            <EvtTitleComponent title="행사 신청" />
            <EvtReservation />
        </div>
    );
}
export default EventReservationPage;