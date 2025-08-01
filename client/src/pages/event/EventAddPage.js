import EvtTitleComponent from "../../components/event/EvtTitleComponent";
import "react-datepicker/dist/react-datepicker.css";
import EvtAddComponent from "../../components/event/EvtAddComponent";

const AddEvtPage = () => {

  return (
    <div className="w-full">
      <EvtTitleComponent title="행사 등록" />
      <EvtAddComponent />
      </div>
  );
};

export default AddEvtPage;