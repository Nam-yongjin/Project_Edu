import "react-datepicker/dist/react-datepicker.css";
import EvtUpdateComponent from "../../components/event/EvtUpdateComponent";
import { useParams } from "react-router-dom";

const UpdatEvtPage = () => {
  const { eventNum } = useParams();
  const parsedEventNum = parseInt(eventNum, 10);

  return (
    <div className="w-full">
      <EvtUpdateComponent eventNum={parsedEventNum} />
    </div>
  );
};

export default UpdatEvtPage;
