import { useLocation } from "react-router-dom";
import AdminEmailComponent from "../../components/admin/AdminEmailComponent";

const AdminEmailPage = () => {
    const location = useLocation();
    const selectedIds = location.state?.selectedIds || []; // 선택된 ID 배열

    return (
        <div>
            <AdminEmailComponent selectedIds={selectedIds} />
        </div>
    );
};

export default AdminEmailPage;
