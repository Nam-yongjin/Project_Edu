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
    // 권한 체크 useEffect
    useEffect(() => {
        if (!isCompany && !isAdmin) {
            alert("권한이 없습니다.");
            moveToPath("/");
        }

    }, []);
    const initState = {
        content: [],
        totalPages: 0,
        currentPage: 0,
    };
    const searchOptions = [
        { value: "demName", label: "상품명" },
        { value: "demMfr", label: "제조사" },
    ];

    const [search, setSearch] = useState("");
    const [type, setType] = useState("demName");
    const [sortBy, setSortBy] = useState("regDate");
    const [sort, setSort] = useState("desc");
    const [statusFilter, setStatusFilter] = useState("total");
    const { moveToPath } = useMove();
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
        setCurrent(0);
    };

    const onDeleteDem = (demNum) => {
        if (demNum === null) {
            alert('물품 번호가 없습니다');
            return;
        }
        delDem(demNum);
        alert('물품이 삭제되었습니다.');
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
                return "만료"
            default:
                return state || "-";
        }
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
                                <th className="py-3 px-4 text-left">개수</th>
                                <th
                                    className="py-3 px-4 text-center cursor-pointer select-none rounded-tl-lg"
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
                                        <option value="REJECT">거부</option>
                                        <option value="ACCEPT">수락</option>
                                        <option value="WAIT">대기</option>
                                        <option value="CANCEL">취소</option>
                                        <option value="EXPIRED">만료</option>
                                    </select>
                                </th>
                                <th className="py-3 px-4 text-center rounded-tr-lg"></th>
                            </tr>
                        </thead>
                        <tbody className="text-gray-600 text-sm">
                            {listData.content.map((item) => {
                                const mainImage = item.imageList?.find((img) => img.isMain === true);
                                const isCancelled = item.state === "CANCEL";

                                return (
                                    <tr
                                        key={item.demNum}
                                        className={`border-b border-gray-200 cursor-default ${isCancelled ? "bg-gray-100 text-gray-400" : "hover:bg-gray-50"}`}
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
                                        <div>{getStateLabel(item.state)}</div>
                                        <td className="py-3 px-4 text-center space-y-2">
                                            {/* 수정 버튼 */}
                                            <button
                                                className={`${item.state === 'CANCEL'
                                                    ? "bg-gray-300 text-gray-500 cursor-not-allowed"
                                                    : "bg-gradient-to-r from-green-500 to-teal-600 hover:from-teal-600 hover:to-green-700 text-white"
                                                    } px-3 py-1 rounded-lg text-xs font-semibold shadow-md transition duration-300 ease-in-out flex items-center justify-center gap-1 w-full`}
                                                onClick={() => {
                                                    if (item.state !== 'WAIT') {
                                                        alert('대기 상태에서만 수정 가능합니다.');
                                                        return;
                                                    }
                                                    moveToPath(`/demonstration/update/${item.demNum}`);
                                                }}
                                                disabled={item.state === 'CANCEL'}
                                            >
                                                상품 수정하기
                                            </button>

                                            {/* 삭제 버튼 */}
                                            <button
                                                className={`${item.state === 'CANCEL'
                                                    ? "bg-gray-300 text-gray-500 cursor-not-allowed"
                                                    : "bg-gradient-to-r from-green-500 to-teal-600 hover:from-teal-600 hover:to-green-700 text-white"
                                                    } px-3 py-1 rounded-lg text-xs font-semibold shadow-md transition duration-300 ease-in-out flex items-center justify-center gap-1 w-full`}
                                                onClick={() => {
                                                    if (item.state !== 'WAIT') {
                                                        alert('대기 상태에서만 삭제 가능합니다.');
                                                        return;
                                                    }
                                                    onDeleteDem(item.demNum);
                                                }}
                                                disabled={item.state === 'CANCEL'}
                                            >
                                                상품 삭제하기
                                            </button>

                                            {/* 회원 정보 보기 버튼 */}
                                            <button
                                                className={`${item.state === 'CANCEL'
                                                    ? "bg-gray-300 text-gray-500 cursor-not-allowed"
                                                    : "bg-gradient-to-r from-green-500 to-teal-600 hover:from-teal-600 hover:to-green-700 text-white"
                                                    } px-3 py-1 rounded-lg text-xs font-semibold shadow-md transition duration-300 ease-in-out flex items-center justify-center gap-1 w-full`}
                                                onClick={() => {
                                                    setSelectedDemNum(item.demNum);
                                                    setIsModalOpen(true);
                                                }}
                                                disabled={item.state === 'CANCEL'}
                                            >
                                                회원 정보 확인
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

            {isModalOpen && (
                <RentalMemberInfoModal
                    demNum={selectedDemNum}
                    onClose={() => setIsModalOpen(false)}
                />
            )}
        </div>
    );
};

export default BorrowComponent;
