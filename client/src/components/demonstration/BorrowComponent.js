import React, { useEffect, useState } from "react";
import { getBorrow, getBorrowSearch } from "../../api/demApi";
import PageComponent from "../common/PageComponent";
import SearchComponent from "../../components/demonstration/SearchComponent";
import useMove from "../../hooks/useMove";
const BorrowComponent = () => {
    const initState = {
        content: [],
        totalPages: 0,
        currentPage: 0,
    };
    const searchOptions = [
        { value: "demName", label: "상품명" },
        { value: "demMfr", label: "제조사" },
    ];
    // 검색/필터 상태 (필요하면 추가)
    const [search, setSearch] = useState("");
    const [type, setType] = useState("demName");
    const [sortBy, setSortBy] = useState("regDate");
    const [sort, setSort] = useState("desc");
    const [statusFilter, setStatusFilter] = useState("total");
    const { moveToPath} = useMove(); // 원하는 곳으로 이동할 변수
    const [listData, setListData] = useState({ content: [] }); // 받아올 content 데이터
    const [pageData, setPageData] = useState(initState); // 페이지 데이터
    // 현재 페이지
    const [current, setCurrent] = useState(0);
    const fetchData = () => {
        if (search && search.trim() !== "") {
            getBorrowSearch(search, type, current, sortBy, sort, statusFilter).then((data) => {
                setListData(data);
                setPageData(data);
            });
        } else {
            getBorrow(current, sort, sortBy, statusFilter).then((data) => {
                setListData(data);
                setPageData(data);
            });
        }
    };

    useEffect(() => {
        fetchData();
    }, [current, sort, sortBy, statusFilter]);

    const onSearchClick = () => {
        fetchData();
    };

    const handleSortChange = (value) => {
        if (sortBy === value) {
            setSort(sort === "asc" ? "desc" : "asc");
        } else {
            setSortBy(value);
            setSort("asc");
        }
        setCurrent(0); // 정렬 변경 시 페이지 초기화
    };


    return (
        <div className="max-w-7xl mx-auto px-4 py-6">
            <SearchComponent
                search={search}
                setSearch={setSearch}
                type={type}
                setType={setType}
                onSearchClick={onSearchClick}
                searchOptions={searchOptions}
            />

            <div className="max-w-7xl mx-auto px-4 py-6">
                <div className="overflow-x-auto mt-6">
                    <table className="min-w-full bg-white rounded-lg shadow-md">
                        <thead>
                            <tr className="bg-gray-100 text-gray-700 uppercase text-sm leading-normal">
                                <th className="py-3 px-4 text-left rounded-tl-lg">대표 이미지</th>
                                <th className="py-3 px-4 text-left">상품명</th>
                                <th className="py-3 px-4 text-left">제조사</th>
                                <th className="py-3 px-4 text-center cursor-pointer select-none rounded-tl-lg"
                                    onClick={() => handleSortChange("expDate")}>
                                    <div className="flex items-center justify-center space-x-1">
                                        <span>반납 예정일</span>
                                        <div className="flex flex-col">
                                            <span className={`text-xs leading-none ${sortBy === "expDate" && sort === "asc" ? "text-black" : "text-gray-300"}`}>▲</span>
                                            <span className={`text-xs leading-none ${sortBy === "expDate" && sort === "desc" ? "text-black" : "text-gray-300"}`}>▼</span>
                                        </div>
                                    </div>
                                </th>

                                <th className="py-3 px-4 text-center cursor-pointer select-none"
                                    onClick={() => handleSortChange("regDate")}>
                                    <div className="flex items-center justify-center space-x-1">
                                        <span>등록일</span>
                                        <div className="flex flex-col">
                                            <span className={`text-xs leading-none ${sortBy === "regDate" && sort === "asc" ? "text-black" : "text-gray-300"}`}>▲</span>
                                            <span className={`text-xs leading-none ${sortBy === "regDate" && sort === "desc" ? "text-black" : "text-gray-300"}`}>▼</span>
                                        </div>
                                    </div>
                                </th>
                                <th className="py-3 px-4 text-left">
                                    상태
                                    <select
                                        value={statusFilter}
                                        onChange={(e) => {
                                            setStatusFilter(e.target.value);
                                            setCurrent(0);
                                        }}
                                        className="ml-2 border rounded px-1 text-sm"
                                    >
                                        <option value="">전체</option>
                                        <option value="reject">reject</option>
                                        <option value="accept">accept</option>
                                        <option value="wait">wait</option>
                                        <option value="cancel">cancel</option>
                                    </select>
                                </th>
                                <th className="py-3 px-4 text-center rounded-tr-lg"></th>
                            </tr>
                        </thead>
                        <tbody className="text-gray-600 text-sm">
                            {listData.content.map((item) => {
                                const mainImage = item.imageList?.find((img) => img.isMain === true);
                                return (
                                    <tr
                                        key={item.demNum}
                                        className="border-b border-gray-200 hover:bg-gray-50 cursor-default"
                                    >
                                        <td className="py-3 px-4">
                                            {mainImage ? (
                                                <img
                                                    src={`http://localhost:8090/view/${mainImage.imageUrl}`}
                                                    alt={item.demName}
                                                    className="w-20 h-20 object-contain rounded-md shadow-sm hover:scale-105 transition-transform cursor-pointer"
                                                />
                                            ) : (
                                                <div className="w-20 h-20 flex items-center justify-center bg-gray-100 text-gray-400 rounded-md">
                                                    이미지 없음
                                                </div>
                                            )}
                                        </td>
                                        <td className="py-3 px-4">{item.demName}</td>
                                        <td className="py-3 px-4">{item.demMfr}</td>
                                        <td className="py-3 px-4 text-center">
                                            {item.expDate ? new Date(item.expDate).toLocaleDateString() : "-"}
                                        </td>
                                        <td className="py-3 px-4 text-center">
                                            {item.regDate ? new Date(item.regDate).toLocaleDateString() : "-"}
                                        </td>
                                        <td className="py-3 px-4 text-center">{item.state}</td>
                                        <td className="py-3 px-4 text-center space-y-2">

                                            <button
                                                className="bg-gradient-to-r from-green-500 to-teal-600 hover:from-teal-600 hover:to-green-700
                                                 text-white px-3 py-1 rounded-lg text-xs font-semibold shadow-md
                                                    transition duration-300 ease-in-out flex items-center justify-center gap-1 w-full"
                                                     onClick={() => moveToPath(`/demonstration/update/${item.demNum}`)}
                                            >
                                                <svg
                                                    xmlns="http://www.w3.org/2000/svg"
                                                    className="h-4 w-4"
                                                    fill="none"
                                                    viewBox="0 0 24 24"
                                                    stroke="currentColor"
                                                    strokeWidth={2}
                                                >
                                                    <path strokeLinecap="round" strokeLinejoin="round" d="M15.232 5.232l3.536 3.536M16.5 3.75a2.25 2.25 0 013.182 3.182L7.5 19.5H4.5v-3l12-12z" />
                                                </svg>
                                                상품 수정하기
                                            </button>
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                </div>

                <div className="flex justify-center mt-6">
                    <PageComponent
                        totalPages={pageData.totalPages}
                        current={current}
                        setCurrent={setCurrent}
                    />
                </div>
            </div>
        </div>
    );
};

export default BorrowComponent;
