import { Outlet } from "react-router-dom";
import BasicLayout from "../../layouts/BasicLayout";
import SubMyInfoHeader from "../../layouts/SubMyInfoHeader";

const MemberPage = () => {
    return (
        <div>
            <BasicLayout isFullWidth = {true}>
                <SubMyInfoHeader />
                <Outlet />
            </BasicLayout>
        </div>
    );
};
export default MemberPage;