import EvtTitleComponent from "../../components/event/EvtTitleComponent";
import "react-datepicker/dist/react-datepicker.css";
import EvtUpdateComponent from "../../components/event/EvtUpdateComponent";
import { useParams } from "react-router-dom";

const UpdatEvtPage = () => {
  const { eventNum } = useParams();
  const parsedEventNum = parseInt(eventNum, 10);

  return (
    <div className="w-full">
      <EvtTitleComponent title="행사 수정" />
      <EvtUpdateComponent eventNum={parsedEventNum} />
    </div>
  );
};

export default UpdatEvtPage;
