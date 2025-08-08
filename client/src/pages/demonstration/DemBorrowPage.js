import BorrowComponent from "../../components/demonstration/BorrowComponent";
import DemTitleComponent from "../../components/demonstration/DemTitleComponent";
import { useParams } from "react-router-dom";
const DemDetailPage = () => {
    return (
        <>
            <DemTitleComponent title="실증 물품 목록" />
            <BorrowComponent/>
            </>
    );
}
export default DemDetailPage;