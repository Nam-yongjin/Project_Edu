import BorrowComponent from "../../components/demonstration/BorrowComponent";

import DemTitleComponent from "../../components/demonstration/DemTitleComponent";
import SubMyInfoHeader from "../../layouts/SubMyInfoHeader";

const DemDetailPage = () => {
    return (
        <>
            <SubMyInfoHeader />
            <DemTitleComponent title="실증 물품 목록" />
            <BorrowComponent />
        </>
    );
}
export default DemDetailPage;