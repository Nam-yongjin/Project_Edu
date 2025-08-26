import React, { useEffect, useState } from "react";
import { getBorrow, getBorrowSearch, delDem } from "../../api/demApi";
import PageComponent from "../common/PageComponent";
import SearchComponent from "../../components/demonstration/SearchComponent";
import useMove from "../../hooks/useMove";
import RentalMemberInfoModal from "../../components/demonstration/RentalMemberInfoModal";
import { useSelector } from "react-redux";
import defaultImage from '../../assets/default.jpg';

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
        { value: "demName", label: "물품명" },
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

    // 체크박스 관련 상태
    const [selectedItems, setSelectedItems] = useState([]);
    const [isAllSelected, setIsAllSelected] = useState(false);

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

    // 데이터가 변경될 때마다 선택된 항목들을 초기화
    useEffect(() => {
        setSelectedItems([]);
        setIsAllSelected(false);
    }, [listData]);

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

    // 전체 선택/해제
    const handleSelectAll = (checked) => {
        setIsAllSelected(checked);
        if (checked) {
            const waitItems = listData.content
                .filter(item => item.state === "WAIT")
                .map(item => item.demNum);
            setSelectedItems(waitItems);
        } else {
            setSelectedItems([]);
        }
    };

    // 개별 항목 선택/해제
    const handleItemSelect = (demNum, checked) => {
        let newSelectedItems;
        if (checked) {
            newSelectedItems = [...selectedItems, demNum];
        } else {
            newSelectedItems = selectedItems.filter(item => item !== demNum);
        }
        setSelectedItems(newSelectedItems);

        // 전체 선택 체크박스 상태 업데이트
        const waitItems = listData.content.filter(item => item.state === "WAIT");
        const allWaitItemsSelected = waitItems.every(item => newSelectedItems.includes(item.demNum));
        setIsAllSelected(allWaitItemsSelected && waitItems.length > 0);
    };

    // 선택된 항목들 일괄 삭제
    const handleCheckedDelete = () => {
        if (selectedItems.length === 0) return;
        delDem(selectedItems);
        alert("정상적으로 삭제되었습니다.");
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

    // 대기 상태인 항목들의 개수
    const waitItemsCount = listData.content.filter(item => item.state === "WAIT").length;

    return (
        <div className="max-w-screen-xl mx-auto my-10">
            <div className="min-blank">
                <div className="mx-auto text-center">
                    <div className="newText-3xl font-bold mb-5">실증 물품 등록 확인</div>
                    <div className="py-2 flex justify-center">
                        <SearchComponent
                            search={search}
                            setSearch={setSearch}
                            type={type}
                            setType={setType}
                            onSearchClick={onSearchClick}
                            searchOptions={searchOptions}
                        />
                    </div>
                </div>
                <p className="text-gray-700 my-3 newText-base px-4 py-2 rounded-md inline-block">
                    전체 <span className="font-bold text-blue-600">{pageData.totalElements}</span>건의 신청내역이 있습니다.
                </p>
                <div className="page-shadow overflow-x-auto">
                    <table className="w-full">
                        <thead className="bg-gray-100 text-gray-700 newText-base border border-gray-300">
                            <tr className="newText-base whitespace-nowrap">
                                <th className="w-[5%]">
                                    {waitItemsCount > 0 && (
                                        <input
                                            type="checkbox"
                                            checked={isAllSelected}
                                            onChange={(e) => handleSelectAll(e.target.checked)}
                                            className="w-4 h-4"
                                        />
                                    )}
                                </th>
                                {/* 
                                <th className="w-[8%]">이미지</th>*/}
                                <th className="w-[12%]">물품명</th>
                                <th className="w-[12%]">제조사</th>
                                <th className="w-[12%]">개수</th>

                                {[{ label: "반납예정일", value: "expDate" }, { label: "등록일", value: "regDate" }].map(
                                    ({ label, value }) => (
                                        <th
                                            key={value}
                                            onClick={() => handleSortChange(value)}
                                            className="cursor-pointer w-[12%]"
                                        >
                                            <div className="flex items-center justify-center space-x-1">
                                                <span>{label}</span>
                                                <div className="flex flex-col">
                                                    <span className={`text-[10px] leading-none ${sortBy === value && sort === "asc" ? "text-black" : "text-gray-300"}`}>
                                                        ▲
                                                    </span>
                                                    <span className={`text-[10px] leading-none ${sortBy === value && sort === "desc" ? "text-black" : "text-gray-300"}`}>
                                                        ▼
                                                    </span>
                                                </div>
                                            </div>
                                        </th>
                                    )
                                )}
                                <th className="w-[10%]">
                                    <div className="mb-1">신청상태</div>
                                    <select
                                        value={statusFilter}
                                        className="input-focus"
                                        onChange={(e) => {
                                            setStatusFilter(e.target.value);
                                            setCurrent(0);
                                        }}
                                    >
                                        <option value="">전체</option>
                                        <option value="REJECT">거부</option>
                                        <option value="ACCEPT">수락</option>
                                        <option value="WAIT">대기</option>
                                        <option value="CANCEL">취소</option>
                                        <option value="EXPIRED">만료</option>
                                    </select>
                                </th>
                                <th className="w-[10%]"></th>
                            </tr>
                        </thead>

                        <tbody className="text-gray-600 border border-gray-300">
                            {listData.content.length === 0 ? (
                                <tr>
                                    <td colSpan={9} className="text-center">
                                        <p className="text-gray-500 newText-3xl mt-20 min-h-[300px]">등록한 물품이 없습니다.</p>
                                    </td>
                                </tr>
                            ) : (
                                listData.content.map((item) => {
                                    const mainImage = item.imageList?.find((img) => img.isMain) || item.imageList?.[0];
                                    const itemState = item.state;
                                    const isWaitState = itemState === "WAIT";
                                    
                                    return (
                                        <tr
                                            key={item.demNum}
                                            className={`hover:bg-gray-50 newText-sm text-center whitespace-nowrap border border-gray-300 ${itemState === "CANCEL" ? "bg-gray-100 text-gray-400" : "hover:bg-gray-50"}`}
                                        >
                                            <td>
                                                {isWaitState && (
                                                    <input
                                                        type="checkbox"
                                                        checked={selectedItems.includes(item.demNum)}
                                                        onChange={(e) => handleItemSelect(item.demNum, e.target.checked)}
                                                        className="w-4 h-4"
                                                    />
                                                )}
                                            </td>
                                            {/* 
                                            <td>
                                                {mainImage ? (
                                                    <img
                                                        src={`http://localhost:8090/view/${mainImage.imageUrl}`}
                                                        alt={item.demName}
                                                        onClick={() => moveToPath(`../detail/${item.demNum}`)}
                                                        className="w-20 h-20 rounded-md hover:scale-105 transition-transform cursor-pointer ml-3"
                                                    />
                                                ) : (
                                                    <img
                                                        src={defaultImage}
                                                        alt="default"
                                                        className="w-20 h-20 rounded-md hover:scale-105 transition-transform cursor-pointer ml-3"
                                                    />
                                                )}
                                            </td>*/}
                                            <td className="truncate max-w-[100px]" title={item.demName}>{item.demName}</td>
                                            <td className="truncate max-w-[100px]" title={item.demMfr}>{item.demMfr}</td>

                                            <td>{item.itemNum}</td>
                                            <td>
                                                {item.expDate ? new Date(item.expDate).toLocaleDateString() : "-"}
                                            </td>
                                            <td>
                                                {item.regDate ? new Date(item.regDate).toLocaleDateString() : "-"}
                                            </td>
                                            <td>{getStateLabel(itemState)}</td>
                                            <td className="py-2 px-2 text-center">
                                                <button
                                                    disabled={itemState !== "WAIT"}
                                                    className={`block w-full max-w-full rounded ${itemState === "WAIT" ? "positive-button cursor-pointer" : "disable-button"}`}
                                                    onClick={() => moveToPath(`/demonstration/update/${item.demNum}`)}
                                                >
                                                    물품 수정
                                                </button>

                                                <button
                                                    disabled={itemState !== "WAIT"}
                                                    className={`block w-full max-w-full mt-1 rounded  ${itemState === "WAIT" ? "negative-button cursor-pointer" : "disable-button"}`}
                                                    onClick={() => onDeleteDem([item.demNum])}
                                                >
                                                    물품 삭제
                                                </button>

                                                <button
                                                    disabled={itemState === "REJECT" || itemState === "EXPIRED" || itemState === "CANCEL"}
                                                    className={`block w-full max-w-full mt-1 rounded  ${itemState === "WAIT" || itemState === "ACCEPT" ? "normal-button cursor-pointer" : "disable-button"}`}
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
            </div>
             {/* 우측 하단 예약 취소 버튼 */}
                <div className="flex justify-end mt-4">
                    <button
                        onClick={handleCheckedDelete}
                        disabled={selectedItems.length === 0}
                        className={`px-4 py-2 rounded mr-10 ${selectedItems.length > 0 ? "negative-button" : "disable-button"}`}
                    >
                        등록 취소 ({selectedItems.length})
                    </button>
                </div>
                
            <div className="flex justify-between items-center my-6">
                <div className="flex justify-center flex-1">
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