import { Outlet } from "react-router-dom"
import BasicLayout from "../../layouts/BasicLayout"

const DemonstrationPage = () => {
    return (
        <div>
            <BasicLayout isFullWidth={true}>
                <Outlet/>
            </BasicLayout>
        </div>
    )
}
export default DemonstrationPage