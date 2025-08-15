import SubMyInfoHeader from "../../layouts/SubMyInfoHeader";
import FacilityReservationComponent from "../../components/facility/FacilityReservationComponent"
import "react-datepicker/dist/react-datepicker.css";

const FacilityReservationPage = () => {

  return(
    <div className="w-full">
      <SubMyInfoHeader />
      <FacilityReservationComponent />
    </div>
  )
}
export default FacilityReservationPage;