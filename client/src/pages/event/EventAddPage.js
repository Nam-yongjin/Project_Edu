import SubAdminHeader from "../../layouts/SubAdminHeader";
import EvtAddComponent from "../../components/event/EvtAddComponent";
import "react-datepicker/dist/react-datepicker.css";

const AddEvtPage = () => {

  return (
    <div className="w-full">
      <SubAdminHeader />
      <EvtAddComponent />
      </div>
  );
};

export default AddEvtPage;