import EvtTitleComponent from "../../components/event/EvtTitleComponent";
import "react-datepicker/dist/react-datepicker.css";
import EvtListComponent from "../../components/event/EvtListComponent";

const EventListPage = () => {
    return (
        <div>
            <EvtTitleComponent title="행사 신청" />
            <EvtListComponent />
        </div>
    );
}
export default EventListPage;