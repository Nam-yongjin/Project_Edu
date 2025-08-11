import FacilityTitleComponent from "../../components/facility/FacilityTitleComponent";
import FacilityAddComponent from "../../components/facility/FacilityAddComponent"
import "react-datepicker/dist/react-datepicker.css";

const FacilityAddPage = () => {

  return (
    <div className="w-full">
      <FacilityTitleComponent title="시설 등록" />
      <FacilityAddComponent />
      </div>
  );
};

export default FacilityAddPage;