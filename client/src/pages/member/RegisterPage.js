import { Outlet } from "react-router-dom"
import BasicLayout from "../../layouts/BasicLayout"
import RegisterComponent from "../../components/member/RegisterComponent"

const RegisterPage = () => {
    return (
        <div>
            <BasicLayout>
                <div>register</div>
            </BasicLayout>
        </div>
    )
}
export default RegisterPage