import { Outlet } from "react-router-dom"
import SubBoardHeader from "../../layouts/SubBoardHeader";
import BasicLayout from "../../layouts/BasicLayout"

const NewsPage = () => {
    return (
        <div>
             <BasicLayout isFullWidth={true}>
                <SubBoardHeader />
                <Outlet/>
            </BasicLayout>
        </div>
    )
}
export default NewsPage;