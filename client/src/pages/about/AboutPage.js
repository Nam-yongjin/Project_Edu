import BasicLayout from "../../layouts/BasicLayout";
import { Outlet } from "react-router-dom";

const AboutPage = () => {
    return (
        <div>
            <BasicLayout>
                <Outlet />
            </BasicLayout>
        </div>
    );
};
export default AboutPage;