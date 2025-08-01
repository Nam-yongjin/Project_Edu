import DemTitleComponent from "../../components/demonstration/DemTitleComponent";
import ListComponent from "../../components/demonstration/ListComponent";

const DemListPage = () => {
    return (
        <div className="w-full">
            <DemTitleComponent title="장비 신청" />
            <ListComponent />
        </div>
    );
}
export default DemListPage;