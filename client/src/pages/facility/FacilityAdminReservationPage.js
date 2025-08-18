import FacilityTitleComponent from "../../components/facility/FacilityTitleComponent";
import FacilityAdminReservationComponent from "../../components/facility/FacilityAdminReservationComponent"
import "react-datepicker/dist/react-datepicker.css";

const FacilityAdminReservationPage = () => {

  return(
    <div className="w-full">
      <FacilityTitleComponent title="공간 대여 관리" />
      <FacilityAdminReservationComponent />
    </div>
  )
}
export default FacilityAdminReservationPage;