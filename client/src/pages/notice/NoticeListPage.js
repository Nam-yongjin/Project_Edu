import React, { useEffect, useState } from "react";
import axios from "axios";
import useMove from "../../hooks/useMove";
import NoticeListComponent from "../../components/notice/NoticeListComponent";
import NoticeTitleComponent from "../../components/notice/NoticeTitleComponent";


const NoticeListPage = () => {
    const { moveToPath } = useMove()

    const [notices, setNotices] = useState([]);
    const [selectedNotices, setSelectedNotices] = useState([]);
    const [isAdmin, setIsAdmin] = useState(false);

    const [search, setSearch] = useState("");
    const [searchType, setSearchType] = useState("title");

    // 공지 불러오기
    const fetchNotices = async () => {
        try {
            const res = await axios.get("/api/notice", {
                params: {
                    keyword: search,
                    searchType,
                    sort: "created_at",
                    orderBy: "desc",
                },
            });
            setNotices(res.data.content || res.data);
        } catch (error) {
            console.error("공지 불러오기 실패:", error);
        }
    };

    // 검색
    const handleSearch = () => {
        fetchNotices();
        };

    // 글쓰기
    const handleWrite = (e) => {
        e.preventDefault();
        moveToPath("/notice/add");
    };

    //선택 삭제
    const handleDelete = async () => {
        if (selectedNotices.length === 0) {
        alert("삭제할 공지를 선택해 주세요.");
        return;
    }
        if (!window.confirm("선택한 공지를 삭제하시겠습니까?")) return;

        try {
            await axios.post("/api/notice/deleteNotices", {
                noticeNums: selectedNotices,
            });

            alert("삭제되었습니다.");
            setSelectedNotices([]);
            fetchNotices(); //목록 새로고침
        } catch (error) {
            console.error("삭제 실패:", error);
        }
    };
    //관리자 여부
    useEffect(() => {
        const role = sessionStorage.getItem("role");
        setIsAdmin(role === "ADMIN");
        fetchNotices(); //공지목록
    }, []);

    return (
        <div>
            <NoticeTitleComponent title="공지사항" />
            {/* 검색창 */}
            <div>
                <select
                    value={searchType}
                    onChange={(e) => setSearchType(e.target.value)}
                >
                    <option value="title">제목</option>
                    <option value="content">내용</option>
                    <option value="name">작성자</option>
                    {isAdmin && (<option value="isPinned">고정글</option>)}
                </select>
                <input
                    type="text"
                    placeholder="검색어입력"
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                />
                <button
                    onClick={handleSearch}
                    className="bg-gray-200"
                >
                    검색
                </button>
            </div>

            <h2>공지사항</h2>
            <NoticeListComponent
                notices={notices}
                selectedNotices={selectedNotices}
                setSelectedNotices={setSelectedNotices}
                isAdmin={isAdmin}
            />
            {/* <NoticeListComponent notices={noticeList} /> */}

            {isAdmin && (
                <div>
                    <button onClick={handleWrite}>
                        글쓰기
                    </button>
                    <button onClick={handleDelete}>
                        삭제하기
                    </button>
                </div>
            )}
        </div>
    );
};

export default NoticeListPage;