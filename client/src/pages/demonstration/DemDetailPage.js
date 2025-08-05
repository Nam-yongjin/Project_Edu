import DemTitleComponent from "../../components/demonstration/DemTitleComponent";
import DetailComponent from "../../components/demonstration/DetailComponent";
import { useParams } from "react-router-dom";
const DemDetailPage = () => {
    const {demNum} = useParams();
    return (
        <>
            <DemTitleComponent title="장비 신청" />
            <DetailComponent demNum={demNum}/>
            </>
    );
}
export default DemDetailPage;