import { useLocation } from "react-router-dom";
import AdminEmailComponent from "../../components/admin/AdminEmailComponent";

const AdminEmailPage = () => {
    const location = useLocation();
    const members = location.state?.selectedMembers || []; // 선택된 ID 배열

    return (
        <div>
            <AdminEmailComponent members={members} />
        </div>
    );
};

export default AdminEmailPage;
