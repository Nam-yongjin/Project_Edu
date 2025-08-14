import FacilityTitleComponent from "../../components/facility/FacilityTitleComponent";
import FacilityReservationComponent from "../../components/facility/FacilityReservationComponent"
import "react-datepicker/dist/react-datepicker.css";

const FacilityReservationPage = () => {

  return(
    <div className="w-full">
      <FacilityTitleComponent title="내 시설 예약" />
      <FacilityReservationComponent />
    </div>
  )
}
export default FacilityReservationPage;