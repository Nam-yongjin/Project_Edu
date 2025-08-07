import { useEffect, useState } from "react";
import SearchComponent from "../../components/demonstration/SearchComponent";
import { getRental, getRentalSearch } from "../../api/demApi";
import PageComponent from "../common/PageComponent";
import { useNavigate } from "react-router-dom";

const RentalComponent = () => {
    const initState = {
        content: [],
        totalPages: 0,
        currentPage: 0,
    };

    const [search, setSearch] = useState();
    const [type, setType] = useState("companyName");
    const [pageData, setPageData] = useState(initState);
    const [current, setCurrent] = useState(0);
    const [listData, setListData] = useState({ content: [] });
    const [sortBy, setSortBy] = useState("applyAt");
    const [sort, setSort] = useState("asc");
    const [statusFilter, setStatusFilter] = useState("");
    const navigate = useNavigate();
    const [selectedItems, setSelectedItems] = useState(new Set());
    const fetchData = () => {
        if (search && search.trim() !== "") {
            getRentalSearch(search, type, current, sortBy, sort).then((data) => {
                setListData(data);
                setPageData(data);
            });
        } else {
            getRental(current, sort, sortBy).then((data) => {
                setListData(data);
                setPageData(data);
            });
        }
    };

    useEffect(() => {
        fetchData(); // 최초 로딩, 페이지 변경, 정렬 변경에 따라
    }, [current, sort, sortBy,statusFilter]);

    const onSearchClick = () => {
        fetchData(); // 검색 실행
        console.log(listData);
    };

    const handleSortChange = (column) => {
        if (sortBy === column) {
            setSort((prev) => (prev === "asc" ? "desc" : "asc"));
        } else {
            setSortBy(column);
            setSort("asc"); // 기본 오름차순
        }
    };

    const moveToPath = (path) => {
        navigate(path);
    };

    // 전체선택 체크박스 핸들러
    const handleSelectAll = (e) => {
        if (e.target.checked) {
            // 현재 페이지 모든 demNum을 선택
            const allIds = listData.content.map((item) => item.demNum);
            setSelectedItems(new Set(allIds));
        } else {
            setSelectedItems(new Set());
        }
    };

    // 개별 체크박스 핸들러
    const handleSelectOne = (e, demNum) => {
        const newSet = new Set(selectedItems);
        if (e.target.checked) {
            newSet.add(demNum);
        } else {
            newSet.delete(demNum);
        }
        setSelectedItems(newSet);
    };

    // 예약 취소 버튼 클릭 이벤트 (예시 alert, 실제 구현 시 API 호출 필요)
   const handleCancelReservation = () => {
    if (selectedItems.size === 0) {
        alert("하나 이상 선택해주세요.");
        return;
    }

    const invalidItems = listData.content.filter(
        (item) => selectedItems.has(item.demNum) && item.state !== "WAIT"
    );

    if (invalidItems.length > 0) {
        const states = invalidItems.map((item) => `${item.demNum}: ${item.state}`).join("\n");
        alert(`${states}상태 이므로 예약 취소가 불가능 합니다.`);
        return;
    }

    // 모든 선택 항목이 "WAIT"일 때만 실행
    alert(`예약 취소 요청: ${[...selectedItems].join(", ")}`);
    // TODO: 실제 취소 API 호출
};


    // 상태컬럼 우측 버튼들 클릭 예시 함수 (실제 로직으로 대체 필요)
    const handleActionClick = (demNum, action) => {
    const item = listData.content.find((item) => item.demNum === demNum);
    if (!item) return;

    if (action === "예약변경") {
        if (item.state !== "WAIT") {
            alert(`${item.state}상태 이므로 예약 변경이 불가능합니다.`);
            return;
        }
        alert(`상품 ${demNum}에 대해 '${action}' 동작 실행`);
        // TODO: 예약 변경 로직
    } else if (action === "대여연장") {
        // 여긴 예외처리 안 함 (예시이므로 자유롭게 확장 가능)
        alert(`상품 ${demNum}에 대해 '${action}' 동작 실행`);
    } else if (action === "반납") {
        alert(`상품 ${demNum}에 대해 '${action}' 동작 실행`);
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
            />
            <div className="overflow-x-auto mt-6">
                <table className="min-w-full bg-white rounded-lg shadow-md">
                    <thead>
                        <tr className="bg-gray-100 text-gray-700 uppercase text-sm leading-normal">
                            {/* 전체선택 체크박스 th */}
                            <th className="py-3 px-4 text-center">
                                <input
                                    type="checkbox"
                                    onChange={handleSelectAll}
                                    checked={
                                        listData.content.length > 0 &&
                                        selectedItems.size === listData.content.length
                                    }
                                />
                            </th>
                            <th className="py-3 px-4 text-left rounded-tl-lg">대표 이미지</th>
                            <th className="py-3 px-4 text-left">상품명</th>
                            <th className="py-3 px-4 text-left">기업명</th>
                            <th className="py-3 px-4 text-left">대여개수</th>

                            {["대여시작일", "대여종료일", "신청일"].map((col) => (
                                <th
                                    key={col}
                                    onClick={() => handleSortChange(col)}
                                    className="cursor-pointer text-center select-none py-3 px-4"
                                >
                                    <div className="flex items-center justify-center space-x-1">
                                        <span>{col}</span>
                                        <div className="flex flex-col">
                                            <span
                                                className={`text-xs leading-none ${sortBy === col && sort === "asc"
                                                    ? "text-black"
                                                    : "text-gray-300"
                                                    }`}
                                            >
                                                ▲
                                            </span>
                                            <span
                                                className={`text-xs leading-none ${sortBy === col && sort === "desc"
                                                    ? "text-black"
                                                    : "text-gray-300"
                                                    }`}
                                            >
                                                ▼
                                            </span>
                                        </div>
                                    </div>
                                </th>
                            ))}
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
                                </select>
                            </th>
                            {/* 버튼 3개 컬럼 */}
                            <th className="py-3 px-4 text-center"></th>
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
                                    {/* 체크박스 */}
                                    <td className="py-3 px-4 text-center">
                                        <input
                                            type="checkbox"
                                            checked={selectedItems.has(item.demNum)}
                                            onChange={(e) => handleSelectOne(e, item.demNum)}
                                        />
                                    </td>
                                    <td className="py-3 px-4">
                                        {mainImage ? (
                                            <img
                                                onClick={() => moveToPath(`../detail/${item.demNum}`)}
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
                                    <td className="py-3 px-4">{item.companyName}</td>
                                    <td className="py-3 px-4">{item.bitemNum}</td>
                                    <td className="py-3 px-4 text-center">{item.startDate}</td>
                                    <td className="py-3 px-4 text-center">{item.endDate}</td>
                                    <td className="py-3 px-4 text-center">{item.applyAt}</td>
                                    <td className="py-3 px-4 text-center">{item.state}</td>

                                    {/* 버튼 3개 */}
                                    <td className="py-3 px-4 text-center flex flex-col space-y-1 items-center">
                                        <button
                                            onClick={() => handleActionClick(item.demNum, "예약변경")}
                                            className="bg-yellow-400 hover:bg-yellow-500 text-white px-2 py-1 rounded text-xs w-full"
                                        >
                                            예약변경
                                        </button>
                                        <button
                                            onClick={() => handleActionClick(item.demNum, "대여연장")}
                                            className="bg-green-500 hover:bg-green-600 text-white px-2 py-1 rounded text-xs w-full"
                                        >
                                            대여연장
                                        </button>
                                        <button
                                            onClick={() => handleActionClick(item.demNum, "반납")}
                                            className="bg-red-500 hover:bg-red-600 text-white px-2 py-1 rounded text-xs w-full"
                                        >
                                            반납
                                        </button>
                                    </td>
                                </tr>
                            );
                        })}
                    </tbody>
                </table>
            </div>

            {/* 우측 하단 예약 취소 버튼 */}
            <div className="flex justify-end mt-4">
                <button
                    onClick={handleCancelReservation}
                    className={"px-4 py-2 rounded text-white bg-gray-400"}
                >
                    예약 취소
                </button>
            </div>

            <div className="flex justify-center mt-6">
                <PageComponent
                    totalPages={pageData.totalPages}
                    current={current}
                    setCurrent={setCurrent}
                />
            </div>
        </div>
    );
};

export default RentalComponent;