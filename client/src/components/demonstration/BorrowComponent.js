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
            <div className="min-blank">
                <div className="newText-3xl font-bold ">실증 물품 등록 확인</div>
                <div className="py-2">
                    <SearchComponent
                        search={search}
                        setSearch={setSearch}
                        type={type}
                        setType={setType}
                        onSearchClick={onSearchClick}
                        searchOptions={searchOptions}
                    />

                    <div className="overflow-x-auto">
                        <p className="text-gray-600 mt-1 my-2">
                            전체 {pageData.totalElements}건의 신청 내역이 있습니다.</p>
                        <table className="w-full">
                            <thead className="bg-gray-100 text-gray-700 newText-base">
                                <tr className="newText-base whitespace-nowrap">
                                    <th className="w-[8%]">이미지</th>
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

                            <tbody className="text-gray-600">
                                {listData.content.length === 0 ? (
                                    <tr>
                                        <td colSpan={8} className="text-center">
                                            <p className="text-gray-500 newText-3xl mt-20">등록한 물품이 없습니다.</p>
                                        </td>
                                    </tr>
                                ) : (
                                    listData.content.map((item) => {
                                        const mainImage = item.imageList?.find((img) => img.isMain) || item.imageList?.[0]; // CANCEL 상태도 이미지 보이게
                                        const itemState = item.state;
                                        return (
                                            <tr
                                                key={item.demNum}
                                                className={`hover:bg-gray-50 newText-sm text-center whitespace-nowrap ${itemState === "CANCEL" ? "bg-gray-100 text-gray-400" : "hover:bg-gray-50"}`}
                                            >
                                                <td>
                                                    {mainImage ? (
                                                        <img
                                                            src={`http://localhost:8090/view/${mainImage.imageUrl}`}
                                                            alt={item.demName}
                                                            onClick={() => moveToPath(`../detail/${item.demNum}`)}
                                                            className="min-w-20 min-h-20 rounded-md hover:scale-105 transition-transform cursor-pointer"
                                                        />
                                                    ) : (
                                                        <img
                                                            src={defaultImage}
                                                            alt="default"
                                                            className="w-20 h-20 rounded-md hover:scale-105 transition-transform cursor-pointer"
                                                        />
                                                    )}
                                                </td>
                                                <td>{item.demName}</td>
                                                <td>{item.demMfr}</td>
                                                <td>{item.itemNum}</td>
                                                <td>
                                                    {item.expDate ? new Date(item.expDate).toLocaleDateString() : "-"}
                                                </td>
                                                <td>
                                                    {item.regDate ? new Date(item.regDate).toLocaleDateString() : "-"}
                                                </td>
                                                <td>{getStateLabel(itemState)}</td>
                                                <td className="py-3 px-4 text-center flex flex-col gap-1 items-stretch">
                                                    <button
                                                        disabled={itemState !== "WAIT"}
                                                        className={`rounded newText-sm ${itemState === "WAIT" ? "positive-button cursor-pointer" : "disable-button"}`}
                                                        onClick={() => moveToPath(`/demonstration/update/${item.demNum}`)}
                                                    >
                                                        물품 수정
                                                    </button>

                                                    <button
                                                        disabled={itemState !== "WAIT"}
                                                        className={`rounded newText-sm ${itemState === "WAIT" ? "nagative-button cursor-pointer" : "disable-button"}`}
                                                        onClick={() => onDeleteDem(item.demNum)}
                                                    >
                                                        물품 삭제
                                                    </button>
                                                    <button
                                                        disabled={itemState === "REJECT" || itemState === "EXPIRED" || itemState === "CANCEL"}
                                                        className={`rounded newText-sm ${itemState === "WAIT" || itemState === "ACCEPT" ? "normal-button cursor-pointer" : "disable-button"}`}
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
            </div>
            <div className="flex justify-center my-6">
                <PageComponent
                    totalPages={pageData.totalPages}
                    current={current}
                    setCurrent={setCurrent}
                />
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
