import { Outlet } from "react-router-dom"
import BasicLayout from "../../layouts/BasicLayout"

const MemberPage = () => {
    return (
        <div>
            <BasicLayout>
                <Outlet />
            </BasicLayout>
        </div>
    )
}
export default MemberPage