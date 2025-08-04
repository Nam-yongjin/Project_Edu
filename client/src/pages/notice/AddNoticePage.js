import { useState } from "react";
import useMove from "../../hooks/useMove";
import axios from "axios";
import NoticeAddComponent from "../../components/notice/NoticeAddComponent";
import { createNotice } from "../../api/noticeApi";

const AddNoticePage = () => {
    const { moveToPath } = useMove();

    const initState = { //초기값 객체
        title: '',
        content: '',
        files: [] //새로 선택한 파일로 변경
    };
      
    const [notice, setNotice] = useState(initState);
    //initState는 초기값 객체, useState는 상태 만드는 훅
    //둘 다 쓰는 게 정석 -> 폼 초기화 할 때 편해짐

    //등록 버튼
    const handleAdd = async () => {
        try {
            const formData = new FormData();
            formData.append('title', notice.title);
            formData.append('content', notice.content);
            notice.files.forEach(file => formData.append('files', file));

            await createNotice(formData); //경로 포함된 함수

            alert("공지사항이 등록되었습니다.");
            moveToPath("/notice/list");
        } catch (error) {
            console.error("공지사항 등록 실패:", error);
            alert("등록 중 오류가 발생했습니다.");
        } 
    };

    //목록 버튼
    const handleCancel = () => {
        if (window.confirm("작성을 취소하고 목록으로 돌아가시겠습니까?")) {
            moveToPath("/notice/list");
        }
    }

    return (
        <NoticeAddComponent
            notice={notice}
            setNotice={setNotice}
            onAdd={handleAdd}
            onCancel={handleCancel}
        />
    );
};

export default AddNoticePage;