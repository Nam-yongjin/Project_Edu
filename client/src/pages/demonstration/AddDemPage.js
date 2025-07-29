import DemTitleComponent from "../../components/demonstration/DemTitleComponent";
import "react-datepicker/dist/react-datepicker.css";
import AddComponent from "../../components/demonstration/AddComponent";

const AddDemPage = () => {

  return (
    <div className="w-full">
      <DemTitleComponent title="실증 등록" />
      <AddComponent />
      </div>
  );
};

export default AddDemPage;
