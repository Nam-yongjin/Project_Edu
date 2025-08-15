import SubAdminHeader from "../../layouts/SubAdminHeader";
import FacilityHolidayComponent from "../../components/facility/FacilityHolidayComponent";
import "react-datepicker/dist/react-datepicker.css";

const FacilityHolidayPage = () => {
  return (
    <div className="w-full">
      <SubAdminHeader />
      <FacilityHolidayComponent />
    </div>
  );
};

export default FacilityHolidayPage;
