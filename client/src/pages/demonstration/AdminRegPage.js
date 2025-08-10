import DemTitleComponent from "../../components/demonstration/DemTitleComponent";
import "react-datepicker/dist/react-datepicker.css";
import AdminRegComponent from "../../components/demonstration/AdminRegComponent";

const AddDemPage = () => {

  return (
   <>
      <DemTitleComponent title="실증 현황 관리" />
      <AdminRegComponent />
   </>
  );
};

export default AddDemPage;
