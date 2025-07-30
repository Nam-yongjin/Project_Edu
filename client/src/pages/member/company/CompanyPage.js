import { Outlet } from "react-router-dom";
import BasicLayout from "../../../layouts/BasicLayout";

const CompanyPage = () => {
    return (
        <div>
            <BasicLayout>
                <Outlet />
            </BasicLayout>
        </div>
    );
};
export default CompanyPage;