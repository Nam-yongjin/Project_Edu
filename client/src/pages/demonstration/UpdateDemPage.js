import "react-datepicker/dist/react-datepicker.css";
import UpdateComponent from "../../components/demonstration/UpdateComponent";
import { useParams } from "react-router-dom";
const UpdateDemPage = () => {
    const {demNum}=useParams();
    return (
        <div>
            <UpdateComponent demNum={demNum}/>
        </div>
    );
}
export default UpdateDemPage;