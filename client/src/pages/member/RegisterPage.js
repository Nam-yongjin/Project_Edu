import { Outlet } from "react-router-dom"
import BasicLayout from "../../layouts/BasicLayout"

const RegisterPage = () => {
    return (
        <div>
            <BasicLayout>
                <Outlet />
            </BasicLayout>
        </div>
    )
}
export default RegisterPage