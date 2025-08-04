import NoticeTitleComponent from "../../components/notice/NoticeTitleComponent";
import NoticeListComponent from "../../components/notice/NoticeListComponent";

const NoticeListPage = () => {
    return (
        <div className="w-full">
            <NoticeTitleComponent title="공지사항" />
            <NoticeListComponent />
        </div>
    );
};

export default NoticeListPage;