import FacilityTitleComponent from "../../components/facility/FacilityTitleComponent";
import FacilityListComponent from "../../components/facility/FacilityListComponent"
import "react-datepicker/dist/react-datepicker.css";

const FacilityListPage = () => {

  return(
    <div className="w-full">
      <FacilityTitleComponent title="공간 목록" />
      <FacilityListComponent />
    </div>
  )
}
export default FacilityListPage;