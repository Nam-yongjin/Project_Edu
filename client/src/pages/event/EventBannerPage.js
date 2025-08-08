import "react-datepicker/dist/react-datepicker.css";
import EvtBannerListComponent from "../../components/event/EvtBannerListComponent";
import SubAdminHeader from "../../layouts/SubAdminHeader";

const EventBannerPage = () => {
    return (
        <div>
            <SubAdminHeader />
            <EvtBannerListComponent />
        </div>
    );
}
export default EventBannerPage;