import StudentRegisterComponent from "../../../components/member/student/StudentRegisterComponent";
import BasicLayout from "../../../layouts/BasicLayout";

const studentRegisterPage = () => {
    return (
        <div>
            <BasicLayout>
                <StudentRegisterComponent/>
            </BasicLayout>
        </div>
    )
}

export default studentRegisterPage