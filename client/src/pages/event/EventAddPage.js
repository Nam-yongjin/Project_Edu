import EvtTitleComponent from "../../components/event/EvtTitleComponent";
import EvtAddComponent from "../../components/event/EvtAddComponent";
import "react-datepicker/dist/react-datepicker.css";

const AddEvtPage = () => {

  return (
    <div className="w-full">
      <EvtTitleComponent title="행사 등록" />
      <EvtAddComponent />
      </div>
  );
};

export default AddEvtPage;