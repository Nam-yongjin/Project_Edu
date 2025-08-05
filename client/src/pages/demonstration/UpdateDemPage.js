import DemTitleComponent from "../../components/demonstration/DemTitleComponent";
import "react-datepicker/dist/react-datepicker.css";
import UpdateComponent from "../../components/demonstration/UpdateComponent";
import { useParams } from "react-router-dom";
const UpdateDemPage = () => {
    const {demNum}=useParams();
    return (
        <div>
            <DemTitleComponent title="실증 수정" />
            <UpdateComponent demNum={demNum}/>
        </div>
    );
}
export default UpdateDemPage;