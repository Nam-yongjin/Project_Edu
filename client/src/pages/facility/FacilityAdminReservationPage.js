import SubAdminHeader from "../../layouts/SubAdminHeader";
import FacilityAdminReservationComponent from "../../components/facility/FacilityAdminReservationComponent"
import "react-datepicker/dist/react-datepicker.css";

const FacilityAdminReservationPage = () => {

  return(
    <div className="w-full">
      <SubAdminHeader />
      <FacilityAdminReservationComponent />
    </div>
  )
}
export default FacilityAdminReservationPage;