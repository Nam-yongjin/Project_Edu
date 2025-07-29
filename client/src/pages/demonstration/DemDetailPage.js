import DemTitleComponent from "../../components/demonstration/DemTitleComponent";
import DetailComponent from "../../components/demonstration/DetailComponent";

const DemDetailPage = () => {
    return (
        <div className="w-full">
           <DemTitleComponent title="장비 신청" />
           <DetailComponent />
        </div>
    );
}
export default DemDetailPage;