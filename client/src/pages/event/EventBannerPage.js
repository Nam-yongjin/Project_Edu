import "react-datepicker/dist/react-datepicker.css";
import EvtBannerComponent from "../../components/event/EvtBannerComponent";
import SubAdminHeader from "../../layouts/SubAdminHeader";
    
const EventBannerPage = () => {
    return (
        <div>
            <SubAdminHeader />
            <EvtBannerComponent />
        </div>
    );
}
export default EventBannerPage;