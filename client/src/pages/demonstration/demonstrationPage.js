import { Outlet } from "react-router-dom"
import BasicLayout from "../../layouts/BasicLayout"
import SubDemonstrationHeader from "../../layouts/SubDemonstrationHeader"
const DemonstrationPage = () => {
    return (
        <div>
            <BasicLayout isFullWidth={true}>
                <SubDemonstrationHeader/>
                <Outlet/>
            </BasicLayout>
        </div>
    )
}
export default DemonstrationPage