import DetailComponent from "../../components/demonstration/DetailComponent";
import { useParams } from "react-router-dom";
const DemDetailPage = () => {
    const { demNum } = useParams();
    return (
        <>
            <DetailComponent demNum={demNum} />
        </>
    );
}
export default DemDetailPage;