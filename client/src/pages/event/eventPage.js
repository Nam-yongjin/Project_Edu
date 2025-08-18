import { Outlet } from "react-router-dom"
import BasicLayout from "../../layouts/BasicLayout"
import SubEventHeader from "../../layouts/SubEventHeader"


const eventPage = () => {
    return (
        <div>
            <BasicLayout  isFullWidth={true}>
                <SubEventHeader />
                <Outlet />
            </BasicLayout>
        </div>
    )
}
export default eventPage