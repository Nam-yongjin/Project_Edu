import { Outlet } from "react-router-dom";
import LoginComponent from "../../components/member/LoginComponent";
import BasicLayout from "../../layouts/BasicLayout";

const LoginPage = () => {
    return (
        <div>
            <BasicLayout>
                <LoginComponent />
                <Outlet />
            </BasicLayout>
        </div>
    );
};
export default LoginPage;