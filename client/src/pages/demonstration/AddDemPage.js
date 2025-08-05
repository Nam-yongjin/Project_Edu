import DemTitleComponent from "../../components/demonstration/DemTitleComponent";
import "react-datepicker/dist/react-datepicker.css";
import AddComponent from "../../components/demonstration/AddComponent";

const AddDemPage = () => {

  return (
   <>
      <DemTitleComponent title="실증 등록" />
      <AddComponent />
   </>
  );
};

export default AddDemPage;
