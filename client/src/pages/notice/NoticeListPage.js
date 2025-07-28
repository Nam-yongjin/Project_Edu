import React, { useEffect, useState } from "react";
import { NoticeList } from "../../api/noticeApi";
import NoticeListComponent from "../../components/notice/NoticeListComponent";
import useMove from "../../hooks/useMove";


const NoticeListPage = () => {

    const [noticeList, setNoticeList] = useState([]);
    const [page, setPage] = useState(0);
    const [size] = useState(10);
    const [totalPages, setTotalPages] = useState(0);
    const [search, setSearch] = useState("");
    const [searchType, setSearchType] = useState("title");

    const {moveToPath} = useMove()

    const fetchData = async () => {
        try {
            const params = {
                page,
                size,
                keyword: search,
                searchType,
                sort: "createdAt",
                orderBy: "desc",
            };
            const data = await NoticeList(params); //Api호출
            setNoticeList(data.content); //공지사항 목록
            setTotalPages(data.totalPages); //전체 페이지 수
        } catch (e) {
            console.error("공지사항 목록 불러오기 실패", e);
        }
    };

    useEffect(() => {
        fetchData();
    }, [page]); //page 변경될 때마다 재조회

    const handleSearch = () => {
        setPage(0);
        fetchData();
    };

    return (
        <div className="w-11/12 max-w-5xl mx-auto py-8">
            <h2 className="text-2xl font-semibold mb-6">공지사항</h2>
            {/* 검색창 */}
            <div className="flex gap-2 mb-6">
                <select
                    className="border px-3 py-2 rounded-md"
                    value={searchType}
                    onChange={(e) => setSearchType(e.target.value)}
                >
                    <option value="title">제목</option>
                    <option value="content">내용</option>
                    <option value="name">작성자</option>
                </select>

                <input
                    type="text"
                    className="border px-4 py-2 rounded-md flex-1"
                    placeholder="검색어 입력"
                    value={search}
                    onChange={(e) => setSearch(e.target.value)} // 검색어 상태 업데이트
                />
                <button 
                    onClick={handleSearch}
                    className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700"
                >
                    검색
                </button>
            </div>
            {/* 리스트 */}
            <NoticeListComponent notices={noticeList} />
            {/* 페이지네이션 */}
            <div className="flex justify-center mt-6 space-x-2">
                {Array.from({ length: totalPages }, (_, i) => (
                    <button
                        key={i}
                        onClick={() => setPage(i)}
                        className={`px-3 py-1 border rounded-md ${
                            page === i
                            ? "bg-blue-600 text-white"
                            : "bg-white hover:bg-gray-100"
                        }`}
                    >
                        { i + 1 }
                    </button>
                ))}
            </div>
        </div>
    );
};

export default NoticeListPage;