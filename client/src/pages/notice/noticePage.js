import { Outlet } from "react-router-dom"
import SubBoardHeader from "../../layouts/SubBoardHeader";
import BasicLayout from "../../layouts/BasicLayout"

const NoticePage = () => {
    return (
        <div>
             <BasicLayout isFullWidth={true}>
                <SubBoardHeader />
                <Outlet/>
            </BasicLayout>
        </div>
    )
}
export default NoticePage;