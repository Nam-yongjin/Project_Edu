import EvtDetailComponent from "../../components/event/EvtDetailComponent"
import EvtTitleComponent from "../../components/event/EvtTitleComponent";
import { useParams } from "react-router-dom";

const EventDetailPage = () => {
    const {eventNum} = useParams();
    return (
        <div>
            <EvtTitleComponent title="행사 상세 보기" />
            <EvtDetailComponent eventNum={eventNum}/>
        </div>
    );
};

export default EventDetailPage;