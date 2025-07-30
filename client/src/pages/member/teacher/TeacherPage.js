import { Outlet } from "react-router-dom";
import BasicLayout from "../../../layouts/BasicLayout";

const TeacherPage = () => {
    return (
        <div>
            <BasicLayout>
                <Outlet />
            </BasicLayout>
        </div>
    );
};
export default TeacherPage;