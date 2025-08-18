import { Outlet } from "react-router-dom"
import BasicLayout from "../../layouts/BasicLayout"
import SubFacilityHeader from "../../layouts/SubFacilityHeader"


const facilityPage = () => {
    return (
        <div>
            <BasicLayout  isFullWidth={true}>
                <SubFacilityHeader />
                <Outlet />
            </BasicLayout>
        </div>
    )
}
export default facilityPage