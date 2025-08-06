import EvtTitleComponent from "../../components/event/EvtTitleComponent";
import "react-datepicker/dist/react-datepicker.css";
import EvtBannerListComponent from "../../components/event/EvtBannerListComponent";

const EventBannerPage = () => {
    return (
        <div>
            <EvtTitleComponent title="행사 배너" />
            <EvtBannerListComponent />
        </div>
    );
}
export default EventBannerPage;