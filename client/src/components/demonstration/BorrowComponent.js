import React, { useEffect, useState } from "react";
import { getBorrow, getBorrowSearch, delDem } from "../../api/demApi";
import PageComponent from "../common/PageComponent";
import SearchComponent from "../../components/demonstration/SearchComponent";
import useMove from "../../hooks/useMove";
import RentalMemberInfoModal from "../../components/demonstration/RentalMemberInfoModal";
import { useSelector } from "react-redux";

const BorrowComponent = () => {
    const isCompany = useSelector((state) => state.loginState?.role === "COMPANY");
    const isAdmin = useSelector((state) => state.loginState?.role === "ADMIN");

    const { moveToPath } = useMove();

    useEffect(() => {
        if (!isCompany && !isAdmin) {
            alert("권한이 없습니다.");
            moveToPath("/");
        }
    }, []);

    const initState = { totalPages: 0, currentPage: 0 };
    const searchOptions = [
        { value: "demName", label: "상품명" },
        { value: "demMfr", label: "제조사" },
    ];

    const [search, setSearch] = useState("");
    const [type, setType] = useState("demName");
    const [sortBy, setSortBy] = useState("regDate");
    const [sort, setSort] = useState("desc");
    const [statusFilter, setStatusFilter] = useState("total");
    const [listData, setListData] = useState({ content: [] });
    const [pageData, setPageData] = useState(initState);
    const [current, setCurrent] = useState(0);

    const [selectedDemNum, setSelectedDemNum] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);

    const fetchData = () => {
        if (search && search.trim() !== "") {
            getBorrowSearch(search, type, current, sortBy, sort, statusFilter).then((data) => {
                setListData(data);
                setPageData(data);
            });
        } else {
            getBorrow(current, sort, sortBy, statusFilter).then((data) => {
                console.log(data);
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
        if (sortBy === value) setSort(sort === "asc" ? "desc" : "asc");
        else {
            setSortBy(value);
            setSort("asc");
        }
        setCurrent(0);
    };

    const onDeleteDem = (demNum) => {
        if (demNum === null) {
            alert("물품 번호가 없습니다");
            return;
        }
        delDem(demNum);
        alert("물품이 삭제되었습니다.");
        window.location.reload();
    };

    const getStateLabel = (state) => {
        switch (state) {
            case "ACCEPT":
                return "수락";
            case "REJECT":
                return "거부";
            case "WAIT":
                return "대기";
            case "CANCEL":
                return "취소";
            case "EXPIRED":
                return "만료";
            default:
                return state || "-";
        }
    };

    return (
        <div className="max-w-screen-xl mx-auto my-10">
            <div className="newText-2xl font-bold mb-4 ">물품 대여 신청 관리</div>
            <div className="max-w-7xl mx-auto px-4 py-6">
                <SearchComponent
                    search={search}
                    setSearch={setSearch}
                    type={type}
                    setType={setType}
                    onSearchClick={onSearchClick}
                    searchOptions={searchOptions}
                />

                <div className="overflow-x-auto mt-6">
                    <p className="text-gray-600 mt-1">
                        전체 {pageData.totalElements}건의 신청 내역이 있습니다.</p>
                    <div className="overflow-x-auto mt-6">
                        <table className="min-w-full bg-white rounded-lg shadow-md">
                            <thead>
                                <tr className="bg-gray-100 text-gray-700 uppercase text-sm leading-normal">
                                    <th className="py-3 px-4 text-left rounded-tl-lg">대표 이미지</th>
                                    <th className="py-3 px-4 text-left">상품명</th>
                                    <th className="py-3 px-4 text-left">제조사</th>
                                    <th className="py-3 px-4 text-left">개수</th>
                                    <th
                                        className="py-3 px-4 text-center cursor-pointer select-none"
                                        onClick={() => handleSortChange("expDate")}
                                    >
                                        <div className="flex items-center justify-center space-x-1">
                                            <span>반납 예정일</span>
                                            <div className="flex flex-col">
                                                <span className={`text-xs leading-none ${sortBy === "expDate" && sort === "asc" ? "text-black" : "text-gray-300"}`}>▲</span>
                                                <span className={`text-xs leading-none ${sortBy === "expDate" && sort === "desc" ? "text-black" : "text-gray-300"}`}>▼</span>
                                            </div>
                                        </div>
                                    </th>
                                    <th
                                        className="py-3 px-4 text-center cursor-pointer select-none"
                                        onClick={() => handleSortChange("regDate")}
                                    >
                                        <div className="flex items-center justify-center space-x-1">
                                            <span>등록일</span>
                                            <div className="flex flex-col">
                                                <span className={`text-xs leading-none ${sortBy === "regDate" && sort === "asc" ? "text-black" : "text-gray-300"}`}>▲</span>
                                                <span className={`text-xs leading-none ${sortBy === "regDate" && sort === "desc" ? "text-black" : "text-gray-300"}`}>▼</span>
                                            </div>
                                        </div>
                                    </th>
                                    <th className="py-3 px-4 text-center">
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
                                            <option value="REJECT">거부</option>
                                            <option value="ACCEPT">수락</option>
                                            <option value="WAIT">대기</option>
                                            <option value="CANCEL">취소</option>
                                            <option value="EXPIRED">만료</option>
                                        </select>
                                    </th>
                                    <th className="py-3 px-4 text-center rounded-tr-lg">액션</th>
                                </tr>
                            </thead>

                            <tbody className="text-gray-600 text-sm">
                                {listData.content.length === 0 ? (
                                    <tr>
                                        <td colSpan={8} className="text-center py-10">
                                            <p className="text-gray-500 text-lg">등록된 상품이 없습니다.</p>
                                        </td>
                                    </tr>
                                ) : (
                                    listData.content.map((item) => {
                                        const mainImage = item.imageList?.find((img) => img.isMain) || item.imageList?.[0]; // CANCEL 상태도 이미지 보이게
                                        const itemState = item.state;
                                        const hasWaitState = false; // 필요 시 수정

                                        return (
                                            <tr
                                                key={item.demNum}
                                                className={`border-b border-gray-200 cursor-default ${itemState === "CANCEL" ? "bg-gray-100 text-gray-400" : "hover:bg-gray-50"}`}
                                            >
                                                <td className="py-3 px-4">
                                                    {mainImage ? (
                                                        <img
                                                            src={`http://localhost:8090/view/${mainImage.imageUrl}`}
                                                            alt={item.demName}
                                                            onClick={() => moveToPath(`../detail/${item.demNum}`)}
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
                                                <td className="py-3 px-4">{item.itemNum}</td>
                                                <td className="py-3 px-4 text-center">
                                                    {item.expDate ? new Date(item.expDate).toLocaleDateString() : "-"}
                                                </td>
                                                <td className="py-3 px-4 text-center">
                                                    {item.regDate ? new Date(item.regDate).toLocaleDateString() : "-"}
                                                </td>
                                                <td className="py-3 px-4 text-center">{getStateLabel(itemState)}</td>
                                                <td className="py-3 px-4 text-center flex flex-col gap-1 items-center">
                                                    {/* 버튼 세로 정렬, w-full 제거 */}
                                                    <button
                                                        disabled={itemState !== "WAIT"}
                                                        className={`px-2 py-1 rounded text-xs ${itemState === "WAIT" ? "bg-yellow-400 hover:bg-yellow-500 text-white cursor-pointer" : "bg-gray-300 text-gray-500 cursor-not-allowed"}`}
                                                        onClick={() => moveToPath(`/demonstration/update/${item.demNum}`)}
                                                    >
                                                        상품 수정
                                                    </button>

                                                    <button
                                                        disabled={itemState !== "WAIT"}
                                                        className={`px-2 py-1 rounded text-xs ${itemState === "WAIT" ? "bg-green-500 hover:bg-green-600 text-white cursor-pointer" : "bg-gray-300 text-gray-500 cursor-not-allowed"}`}
                                                        onClick={() => onDeleteDem(item.demNum)}
                                                    >
                                                        상품 삭제
                                                    </button>
                                                    <button
                                                        disabled={itemState === "REJECT" || itemState === "EXPIRED" || itemState === "CANCEL"}
                                                        className={`px-2 py-1 rounded text-xs ${itemState === "WAIT" || itemState === "ACCEPT" ? "bg-red-500 hover:bg-red-600 text-white cursor-pointer" : "bg-gray-300 text-gray-500 cursor-not-allowed"}`}
                                                        onClick={() => {
                                                            setSelectedDemNum(item.demNum);
                                                            setIsModalOpen(true);
                                                        }}
                                                    >
                                                        회원 정보
                                                    </button>
                                                </td>
                                            </tr>
                                        );
                                    })
                                )}
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

                {isModalOpen && (
                    <RentalMemberInfoModal
                        demNum={selectedDemNum}
                        onClose={() => setIsModalOpen(false)}
                    />
                )}
            </div>
        </div>

    );
};

export default BorrowComponent;
