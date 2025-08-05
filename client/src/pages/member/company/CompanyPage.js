import { Outlet } from "react-router-dom";
import BasicLayout from "../../../layouts/BasicLayout";
import SubMyInfoHeader from "../../../layouts/SubMyInfoHeader";

const CompanyPage = () => {
    return (
        <div>
            <BasicLayout isFullWidth={true}>
                <SubMyInfoHeader />
                <Outlet />
            </BasicLayout>
        </div>
    );
};
export default CompanyPage;