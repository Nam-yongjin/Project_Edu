import NoticeTitleComponent from "../../components/notice/NoticeTitleComponent";
import NoticeListComponent from "../../components/notice/NoticeListComponent";

const NoticeListPage = () => {
    return (
            // 반응형
            <div> 
                <NoticeTitleComponent title="공지사항" />
                <NoticeListComponent />
            </div>
    );
};

export default NoticeListPage;