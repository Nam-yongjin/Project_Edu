import RentalComponent from "../../components/demonstration/RentalComponent";
import DemTitleComponent from "../../components/demonstration/DemTitleComponent";
import SubMyInfoHeader from "../../layouts/SubMyInfoHeader";

const DemRentalPage =()=> {
    return (
    <>
    <SubMyInfoHeader />
    <DemTitleComponent title="대여 현황" />
    <RentalComponent/>
    </>
    );
}
export default DemRentalPage;