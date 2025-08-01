import EvtTitleComponent from "../../components/event/EvtTitleComponent";
import "react-datepicker/dist/react-datepicker.css";
import EvtUpdateComponent from "../../components/event/EvtUpdateComponent";
import { useParams } from "react-router-dom";
const UpdatEvtPage = () => {
    const {evtNum}=useParams();
    return (
        <div className="w-full">
            <EvtTitleComponent title="행사 수정" />
            <EvtUpdateComponent EvyNum={evtNum}/>
        </div>
    );
}
export default UpdatEvtPage;