import { Outlet } from "react-router-dom"
import BasicLayout from "../../../layouts/BasicLayout"

const StudentPage = () => {
    return (
        <div>
            <BasicLayout>
                <Outlet />
            </BasicLayout>
        </div>
    )
}
export default StudentPage