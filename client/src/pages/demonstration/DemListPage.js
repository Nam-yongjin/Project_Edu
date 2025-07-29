import DemTitleComponent from "../../components/demonstration/DemTitleComponent";
import ListComponent from "../../components/demonstration/ListComponent";

const DemListPage = () => {
    const initState={ // 페이지 초기화
    demList:[],totalPage:0,current:0
}
    return (
        <div className="w-full">
            <DemTitleComponent title="장비 신청" />
            <ListComponent />
        </div>
    );
}
export default DemListPage;