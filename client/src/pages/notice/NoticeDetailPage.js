import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import useMove from "../../hooks/useMove";
import axios from "axios";

const NoticeDetailPage = () => {
    const { noticeNum } = useParams();
    const { moveToPath } = useMove();
    const navigate = useNavigate();
    const [notice, setNotice] = useState(null);
    //상세조회
    useEffect(() => {
        axios.get(`/api/notice/${noticeNum}`)
        .then((res) => {
            setNotice(res.data);
        })
        .catch((err) => {
            console.error("공지사항 조회 실패", err);
        });
    }, [noticeNum]);

    const handleList = () => {
        moveToPath("/notice/list");
    }
    const handleUpdate = () => navigate(`/notice/update/${noticeNum}`);
    const handleDelete = () => {
        if (window.confirm("정말 삭제하시겠습니까?")) {
            axios.delete(`api/notice/${noticeNum}`)
            .then(() => {
                alert("삭제되었습니다.");
                navigate("/notice/list");
            })
            .catch((err) => {
                console.error("삭제 실패", err);
                alert("삭제 중 오류가 발생했습니다.");
            });
        }
    }
    

    return (
        <div>
            <h2>공지사항</h2>
            <div>
                <h3>{notice.title}</h3>
                <ul>
                    <li>
                        <span>작성자</span>
                        <span>{notice.name}</span>
                    </li>
                    <li>
                        <span>작성일</span>
                        <span>{notice.createdAt}</span>
                    </li> 
                    <li>
                        <span>조회수</span>
                        <span>{notice.viewCount}</span>
                    </li> 
                </ul>
                <div>
                    <span>첨부파일</span>
                    {notice.fileNames && notice.fileNames.length > 0 && ( //파일이 존재할 때만
                        <ul>
                            {notice.files.map((file) => (
                                <li key={file.downloadUrl}>
                                    <a href={file.downloadUrl} download>{file.originalName}</a>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
                <div> 
                    {/* 내용 */}
                    <p>{notice.content}</p> 
                </div>
                <div>
                    <button onClick={handleList}>목록</button>
                    <button onClick={handleUpdate}>수정하기</button>
                    <button onClick={handleDelete}>삭제하기</button>
                </div>
            </div>
        </div>
    );
};

export default NoticeDetailPage;