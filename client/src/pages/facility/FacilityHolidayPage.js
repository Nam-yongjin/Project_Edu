import FacilityTitleComponent from "../../components/facility/FacilityTitleComponent";
import FacilityHolidayComponent from "../../components/facility/FacilityHolidayComponent";
import "react-datepicker/dist/react-datepicker.css";

const FacilityHolidayPage = () => {
  return (
    <div className="w-full">
      <FacilityTitleComponent title="시설 휴무일" />
      <FacilityHolidayComponent />
    </div>
  );
};

export default FacilityHolidayPage;
