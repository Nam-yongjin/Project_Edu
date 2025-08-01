import React from "react";
import NoticeTitleComponent from "../../components/notice/NoticeTitleComponent";
import NoticeDetailComponent from "../../components/notice/NoticeDetailComponent";

const NoticeDetailPage = () => {
    return (
        <div className="w-full">
            <NoticeTitleComponent title="공지사항" />
            <NoticeDetailComponent />
        </div>
    );
};

export default NoticeDetailPage;