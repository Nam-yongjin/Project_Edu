import BasicLayout from "../../layouts/BasicLayout";
import { Outlet } from "react-router-dom";
import SubAboutHeader from "../../layouts/SubAboutHeader";

const AboutPage = () => {
    return (
        <div>
            <BasicLayout isFullWidth = {true}>
                <SubAboutHeader />
                <Outlet />
            </BasicLayout>
        </div>
    );
};
export default AboutPage;