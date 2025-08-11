import BasicLayout from "../../layouts/BasicLayout";
import { Outlet } from "react-router-dom";
import SubAdminHeader from "../../layouts/SubAdminHeader";
import { useEffect } from "react";
import useMove from "../../hooks/useMove";
import { useSelector } from "react-redux";

const AdminPage = () => {
    const { moveToPath } = useMove();
    const loginState = useSelector((state) => state.loginState);

    useEffect(() => {
        if (loginState.role!=="ADMIN") {
            alert("권한이 없습니다.");
            moveToPath("/");
        }
    }, [loginState.role]);

    return (
        <div>
            <BasicLayout isFullWidth={true}>
                <SubAdminHeader />
                <Outlet />
            </BasicLayout>
        </div>
    );
};
export default AdminPage;