import EvtDetailComponent from "../../components/event/EvtDetailComponent"
import { useParams } from "react-router-dom";

const EventDetailPage = () => {
    const {eventNum} = useParams();
    return (
        <div>
            <EvtDetailComponent eventNum={eventNum}/>
        </div>
    );
};

export default EventDetailPage;