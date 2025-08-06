import BasicLayout from "../../layouts/BasicLayout";
import { Outlet } from "react-router-dom";
import SubAdminHeader from "../../layouts/SubAdminHeader";

const AdminPage = () => {
    return (
        <div>
            <BasicLayout isFullWidth = {true}>
                <SubAdminHeader />
                <Outlet />
            </BasicLayout>
        </div>
    );
};
export default AdminPage;