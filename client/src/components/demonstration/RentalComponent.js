import { useEffect, useState } from "react";
import SearchComponent from "../../components/demonstration/SearchComponent";
import { getRental, getRentalSearch, deleteRental, getResDate } from "../../api/demApi";
import PageComponent from "../common/PageComponent";
import { useNavigate } from "react-router-dom";
import CalendarComponent from "./CalendarComponent";
const RentalComponent = () => {
    const initState = {
        content: [],
        totalPages: 0,
        currentPage: 0,
    };

    const [search, setSearch] = useState(); // 검색어
    const [type, setType] = useState("companyName"); // 검색 타입
    const [pageData, setPageData] = useState(initState); // 페이지 데이터
    const [current, setCurrent] = useState(0); // 현재 페이지
    const [listData, setListData] = useState({ content: [] }); // 받아올 content 데이터
    const [sortBy, setSortBy] = useState("applyAt"); // 정렬 칼럼명
    const [sort, setSort] = useState("asc"); // 정렬 방식
    const [statusFilter, setStatusFilter] = useState("total"); // state에 따라 필터링(ex wait,accept)
    const navigate = useNavigate(); // 원하는 곳으로 이동할 변수
    const [selectedItems, setSelectedItems] = useState(new Set()); // 체크박스 선택 항목(중복 방지를 위해 set사용)
    const [showModifyModal, setShowModifyModal] = useState(false);
    const [selectedDemNum, setSelectedDemNum] = useState();
    const [disabledDates, setDisabledDates] = useState([]);
    const [selectedDates, setSelectedDates] = useState([]);
    const fetchData = () => {
        if (search && search.trim() !== "") {
            getRentalSearch(search, type, current, sortBy, sort, statusFilter).then((data) => {
                setListData(data);
                setPageData(data);
            });
        } else {
            getRental(current, sort, sortBy, statusFilter).then((data) => {
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

    const handleSortChange = (column) => {
        if (sortBy === column) {
            setSort((prev) => (prev === "asc" ? "desc" : "asc"));
        } else {
            setSortBy(column);
            setSort("asc");
        }
    };

    const moveToPath = (path) => {
        navigate(path);
    };

    // 전체선택 체크박스 핸들러
    const handleSelectAll = (e) => {
        if (e.target.checked) {
            const allIds = listData.content
                .filter(item => item.state !== "CANCEL") // cancel 제외
                .map((item) => item.demNum);
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

    const handleCancelReservation = () => {
        if (selectedItems.size === 0) {
            alert("선택된 항목이 없습니다.");
            return;
        }

        const invalidItems = !listData.content
            .filter(item => selectedItems.has(item.demNum))
            .some(item => item.state === "WAIT");

        if (invalidItems) {
            alert("예약 취소가 불가능한 상태입니다.");
            return;
        }

        deleteRental(Array.from(selectedItems));

        alert("예약이 취소 되었습니다.");
        window.location.reload();
    };

    const handleActionClick = async (demNum, action) => {
        const item = listData.content.find((item) => item.demNum === demNum);
        if (!item) return;

        if (action === "예약변경") {
            if (item.state !== "WAIT") {
                alert(`대기 상태에서만 예약 변경이 가능합니다.`);
                return;
            }

            try {
                setSelectedDemNum(demNum);
                setSelectedDates([]); // 초기화

                const resDisabled = await getResDate(
                    demNum
                );
                console.log(resDisabled);
                setDisabledDates(resDisabled);
               // setShowModifyModal(true);
            } catch (err) {
                console.error("예약 정보 조회 실패", err);
            }
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
                                        listData.content.filter(item => item.state !== "CANCEL").length > 0 &&
                                        selectedItems.size === listData.content.filter(item => item.state !== "CANCEL").length
                                    }
                                />
                            </th>
                            <th className="py-3 px-4 text-left rounded-tl-lg">대표 이미지</th>
                            <th className="py-3 px-4 text-left">상품명</th>
                            <th className="py-3 px-4 text-left">기업명</th>
                            <th className="py-3 px-4 text-left">대여개수</th>

                            {["startDate", "endDate", "applyAt"].map((col) => (
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
                                    <option value="cancel">cancel</option>
                                </select>
                            </th>
                            {/* 버튼 3개 컬럼 */}
                            <th className="py-3 px-4 text-center"></th>
                        </tr>
                    </thead>
                    <tbody className="text-gray-600 text-sm">
                        {listData.content.map((item) => {
                            const mainImage = item.imageList?.find((img) => img.isMain === true);
                            const isCancelled = item.state === "CANCEL";

                            return (

                                <tr key={`${item.demNum}_${item.startDate}_${item.endDate}_${item.applyAt}`}
                                    className={`border-b border-gray-200 hover:bg-gray-50 cursor-default ${isCancelled ? "bg-gray-100 text-gray-400" : ""
                                        }`}
                                >
                                    {/* 체크박스 */}
                                    <td className="py-3 px-4 text-center">
                                        {!isCancelled ? (
                                            <input
                                                type="checkbox"
                                                checked={selectedItems.has(item.demNum)}
                                                onChange={(e) => handleSelectOne(e, item.demNum)}
                                            />
                                        ) : (
                                            <span>취소됨</span>
                                        )}
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
                                        {!isCancelled ? (
                                            <>
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
                                            </>
                                        ) : (
                                            <span className="text-gray-500 mt-[30px]">취소됨</span>
                                        )}
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

            {showModifyModal && (
                <div className="fixed top-0 left-0 w-full h-full bg-black bg-opacity-50 z-50 flex justify-center items-center">
                    <div className="bg-white p-6 rounded shadow-md w-[500px]">
                        <h2 className="text-xl mb-4 font-bold">예약 날짜 변경</h2>

                        <CalendarComponent
                            selectedDate={selectedDates}
                            setSelectedDate={setSelectedDates}
                            reservationQty={1} // 고정 or 전달된 값 사용
                            disabledDates={disabledDates}
                        />

                        <div className="mt-6 flex justify-end gap-3">
                            <button
                                className="bg-gray-300 px-4 py-2 rounded"
                                onClick={() => setShowModifyModal(false)}
                            >
                                취소
                            </button>
                            <button
                                className="bg-blue-500 text-white px-4 py-2 rounded"
                                onClick={async () => {
                                    if (selectedDates.length < 1) {
                                        alert("변경할 날짜를 선택하세요.");
                                        return;
                                    }
                                    const sortedDates = [...selectedDates].sort();
                                    const startDate = sortedDates[0];
                                    const endDate = sortedDates[sortedDates.length - 1];

                                    try {
                                        //await postRes(startDate, endDate, selectedDemNum, 1); // 변경된 값으로 업데이트
                                        alert("예약이 변경되었습니다.");
                                        setShowModifyModal(false);
                                        // 데이터 다시 로딩
                                        //fetchData(currentPage, selected);  // currentPage: 현재 페이지 번호  selected: 선택된 날짜 (또는 항목)
                                    } catch (err) {
                                        alert("예약 변경에 실패했습니다.");
                                        console.error(err);
                                    }
                                }}
                            >
                                변경
                            </button>
                        </div>
                    </div>
                </div>
            )}

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
