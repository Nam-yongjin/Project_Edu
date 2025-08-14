import { Outlet } from "react-router-dom"
import BasicLayout from "../../layouts/BasicLayout"
import SubBoardHeader from "../../layouts/SubBoardHeader"
const QnAPage = () => {
    return (
        <div>
            <BasicLayout isFullWidth={true}>
                <SubBoardHeader/>
                <Outlet/>
            </BasicLayout>
        </div>
    )
}
export default QnAPage;