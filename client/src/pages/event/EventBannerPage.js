import SubAdminHeader from "../../layouts/SubAdminHeader";
import "react-datepicker/dist/react-datepicker.css";
import EvtBannerListComponent from "../../components/event/EvtBannerListComponent";

const EventBannerPage = () => {
    return (
        <div>
            <SubAdminHeader title="행사 배너" />
            <EvtBannerListComponent />
        </div>
    );
}
export default EventBannerPage;