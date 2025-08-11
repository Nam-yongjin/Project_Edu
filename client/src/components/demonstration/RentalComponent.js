import { useEffect, useState } from "react";
import SearchComponent from "../../components/demonstration/SearchComponent";
import { getRental, getRentalSearch, deleteRental, getResExceptDate, updateRental, addRequest } from "../../api/demApi";
import PageComponent from "../common/PageComponent";
import useMove from "../../hooks/useMove";
import CalendarComponent from "./CalendarComponent";
import ItemModal from "./itemModal";
const RentalComponent = () => {
    const initState = {
        content: [],
        totalPages: 0,
        currentPage: 0,
    };

    const searchOptions = [
        { value: "demName", label: "상품명" },
        { value: "companyName", label: "기업명" },
    ];
    const [search, setSearch] = useState(); // 검색어
    const [type, setType] = useState("companyName"); // 검색 타입
    const [pageData, setPageData] = useState(initState); // 페이지 데이터
    const [current, setCurrent] = useState(0); // 현재 페이지
    const [listData, setListData] = useState({ content: [] }); // 받아올 content 데이터
    const [sortBy, setSortBy] = useState("applyAt"); // 정렬 칼럼명
    const [sort, setSort] = useState("asc"); // 정렬 방식
    const [statusFilter, setStatusFilter] = useState("total"); // state에 따라 필터링(ex wait,accept)
    const { moveToPath } = useMove(); // 원하는 곳으로 이동할 변수
    const [selectedItems, setSelectedItems] = useState(new Set()); // 체크박스 선택 항목(중복 방지를 위해 set사용)
    const [showModifyModal, setShowModifyModal] = useState(false); // 캘린더 모달 변수
    const [selectedDemNum, setSelectedDemNum] = useState(); // 캘린더에 넘겨줄 demNum 
    const [selectedDate, setSelectedDate] = useState([]); // 선택된 날짜를 가져오는 변수
    const [exceptDate, setExceptDate] = useState([]); // 회원이 예약한 물품에 대해 예약날짜를 가져오는 변수
    const [disabledDates, setDisabledDates] = useState([]); // 캘린더에서 disabled할 날짜 배열
    const [showQtyModal, setShowQtyModal] = useState(false); // 아이템 모달
    const [reservationQty, setReservationQty] = useState(1); // 수량 설정
    const currentItem = listData.content?.find(
        (item) => item.demNum === selectedDemNum && item.state === "WAIT"
    );
    const maxQty = currentItem?.itemNum || 0;
    const [startDate, setStartDate] = useState(() => { // startDate 초기값 저장
        const d = new Date();
        d.setDate(d.getDate() + 1);
        return d;
    });
    const [endDate, setEndDate] = useState(() => { // endDate 초기값 저장
        const d = new Date();
        d.setDate(d.getDate() + 1);
        return d;
    });
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


    useEffect(() => {
        if (selectedDate && selectedDate.length > 0) { // selectedDates를 통해 datepicker 날짜 조정
            const dates = selectedDate.map(d => (d instanceof Date ? d : new Date(d))); // date객체 형태가 아닐경우 변환(날짜 비교를 위해)
            // selectedDate가 변경 시에 startDate, endDate를 변경 시킴
            // 가장 빠른 날짜 (min)
            const minDate = new Date(Math.min(...dates.map(d => d.getTime())));

            // 가장 늦은 날짜 (max)
            const maxDate = new Date(Math.max(...dates.map(d => d.getTime())));

            setStartDate(minDate);
            setEndDate(maxDate);
        }
    }, [selectedDate]);

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
            .some(item => item.state === "WAIT"); // 상품번호를 가지고 잇고 wait가 아닌 데이터에 대해 취소불가능

        if (invalidItems) {
            alert("예약 취소가 불가능한 상태입니다.");
            return;
        }
        deleteRental(Array.from(selectedItems));

        alert("예약이 취소 되었습니다.");
        window.location.reload();
    };

    const handleActionClick = async (demNum, action) => {
        // 같은 demNum을 가진 모든 항목을 찾음
        // reject랑 cancel은 버튼 자체가 존재 하지 않으므로 고려 x
        const items = listData.content.filter((item) => item.demNum === demNum);
        if (items.length === 0) return;

        if (action === "예약변경") {
            // demNum에 해당하는 항목 중 WAIT 상태가 하나라도 있는지 확인
            const hasWait = items.some(item => item.state === "WAIT");
            if (!hasWait) {
                alert(`대기 상태에서만 예약 변경이 가능합니다.`);
                return;
            }

            try {
                setSelectedDemNum(demNum);
                const exceptDate = await getResExceptDate(demNum);
                setExceptDate(exceptDate);
                setShowModifyModal(true);
            } catch (err) {
                console.error("예약 정보 조회 실패", err);
            }
        }
        else if (action === "대여연장") {
            const hasAccept = items.some(item => item.state === "ACCEPT");
            if (!hasAccept) {
                alert(`승인 상태에서만 대여 연장 신청이 가능합니다.`);
                return;
            }
            const type = "EXTEND";
            addRequest(demNum, type);
            alert(`연장 신청 완료`);
            window.location.reload();
        }
        else if (action === "반납") {
            const hasAccept = items.some(item => item.state === "ACCEPT");
            if (!hasAccept) {
                alert(`승인 상태에서만 반납 신청이 가능합니다.`);
                return;
            }
            const type = "RENTAL";
            addRequest(demNum, type);
            alert(`반납 신청 완료`);
            window.location.reload();
        }

    };


    const reservationUpdate = (updatedItemNum) => {
        const loadData = async () => {
            if (!selectedDate || selectedDate.length === 0) {
                alert('날짜를 선택해주세요!');
                return;
            }

            if (selectedDate.length > 90) {
                alert('최대 90일까지만 선택할 수 있습니다!');
                return;
            }

            let sortedDates = [...selectedDate]
                .map(d => new Date(d)) // 문자열일 경우 Date로 변환
                .sort((a, b) => a.getTime() - b.getTime()); // getTime으로 정렬
            sortedDates = sortedDates.map(d => (d instanceof Date ? d : new Date(d))); // 데이트 타입이 아닐경우 데이트 타입으로 파싱

            for (let i = 0; i < sortedDates.length - 1; i++) {
                const diffTime = sortedDates[i + 1].getTime() - sortedDates[i].getTime(); // 두 날을 뺀 값 (ms)단위
                const diffDays = diffTime / (1000 * 60 * 60 * 24); // ms단위 이므로 하루 단위로 변경

                if (diffDays !== 1) {
                    alert('연속된 날짜만 선택 가능합니다!');
                    return;
                }
            }

            if (selectedDate.some(date => disabledDates.includes(date))) {
                alert('선택한 날짜 중에 예약 중인 날짜가 있습니다.');
                return;
            }

            try {
                await updateRental(
                    toLocalDateString(startDate), toLocalDateString(endDate), selectedDemNum, updatedItemNum);
                alert('예약 신청 완료');
                window.location.reload();
            } catch (error) {
                console.error('예약 실패:', error);
                alert('예약에 실패했습니다.');
            }
        };

        loadData();
    };

    function toLocalDateString(date) {
        const y = date.getFullYear();
        const m = String(date.getMonth() + 1).padStart(2, '0');
        const d = String(date.getDate()).padStart(2, '0');
        return `${y}-${m}-${d}`;
    }

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

                            {[
                                { label: "시작일", value: "startDate" },
                                { label: "마감일", value: "endDate" },
                                { label: "등록일", value: "applyAt" },
                            ].map(({ label, value }) => (
                                <th
                                    key={value}
                                    onClick={() => handleSortChange(value)}
                                    className="cursor-pointer text-center select-none py-3 px-4"
                                >
                                    <div className="flex items-center justify-center space-x-1">
                                        <span>{label}</span>
                                        <div className="flex flex-col">
                                            <span
                                                className={`text-xs leading-none ${sortBy === value && sort === "asc" ? "text-black" : "text-gray-300"
                                                    }`}
                                            >
                                                ▲
                                            </span>
                                            <span
                                                className={`text-xs leading-none ${sortBy === value && sort === "desc" ? "text-black" : "text-gray-300"
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
                            <th className="py-3 px-4 text-left"></th>
                        </tr>
                    </thead>
                    <tbody className="text-gray-600 text-sm">
                        {listData.content.map((item) => {
                            const mainImage = item.imageList?.find((img) => img.isMain === true);
                            const isCancelled = item.state === "CANCEL";
                            const isRejected = item.state === "REJECT";
                            // 진행중 요청만 필터링 (WAIT 상태)
                            const pendingRequests =
                                item.requestType !== null && item.reqState !== null
                                    ? [{ type: item.requestType, state: item.reqState }].filter(
                                        (req) => req.state === "WAIT"
                                    )
                                    : [];

                            return (
                                <tr
                                    key={`${item.demNum}_${item.startDate}_${item.endDate}_${item.applyAt}`}
                                    className={`border-b border-gray-200 hover:bg-gray-50 cursor-default ${isCancelled ? "bg-gray-100 text-gray-400" : ""
                                        }`}
                                >
                                    {/* 체크박스 */}
                                    <td className="py-3 px-4 text-center">
                                        {item.state === "WAIT" ? (
                                            <input
                                                type="checkbox"
                                                checked={selectedItems.has(item.demNum)}
                                                onChange={(e) => handleSelectOne(e, item.demNum)}
                                            />
                                        ) : (
                                            <span></span>
                                        )}
                                    </td>

                                    {/* 이미지 */}
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

                                    {/* 기본 정보 */}
                                    <td className="py-3 px-4">{item.demName}</td>
                                    <td className="py-3 px-4">{item.companyName}</td>
                                    <td className="py-3 px-4">{item.bitemNum}</td>
                                    <td className="py-3 px-4 text-center">{item.startDate}</td>
                                    <td className="py-3 px-4 text-center">{item.endDate}</td>
                                    <td className="py-3 px-4 text-center">{item.applyAt}</td>

                                    {/* 상태 표시 칸 (item.state만) */}
                                    <td className="py-3 px-4 text-center align-top">
                                        <div>{item.state}</div>
                                    </td>

                                    {/* 진행중 요청 별도 칸 (반납신청, 연장신청 등) */}
                                    <td className="py-3 px-4 text-center align-top text-xs text-blue-600">
                                        {pendingRequests.length > 0 ? (
                                            <ul className="list-disc list-inside">
                                                {pendingRequests.map((req, idx) => {
                                                    if (req.type === "RENTAL") return <li key={idx}>반납신청 진행중</li>;
                                                    if (req.type === "EXTEND") return <li key={idx}>기한연장 진행중</li>;
                                                    return null;
                                                })}
                                            </ul>
                                        ) : (
                                            <span>-</span>
                                        )}
                                    </td>

                                    {/* 버튼 3개 칸 */}
                                    <td className="py-3 px-4 text-center flex flex-col space-y-1 items-center">
                                        {/* 예약변경 버튼: WAIT 상태에서만 활성 */}
                                        <button
                                            disabled={item.state !== "WAIT"}
                                            onClick={() => handleActionClick(item.demNum, "예약변경")}
                                            className={`px-2 py-1 rounded text-xs w-full ${item.state === "WAIT"
                                                ? "bg-yellow-400 hover:bg-yellow-500 text-white cursor-pointer"
                                                : "bg-gray-300 text-gray-500 cursor-not-allowed"
                                                }`}
                                        >
                                            예약변경
                                        </button>

                                        {/* 대여연장 버튼: ACCEPT 상태에서만 활성 */}
                                        <button
                                            disabled={item.state !== "ACCEPT" || item.reqState === "WAIT"}
                                            onClick={() => handleActionClick(item.demNum, "대여연장")}
                                            className={`px-2 py-1 rounded text-xs w-full ${item.state === "ACCEPT" && item.reqState !== "WAIT"
                                                    ? "bg-green-500 hover:bg-green-600 text-white cursor-pointer"
                                                    : "bg-gray-300 text-gray-500 cursor-not-allowed"
                                                }`}
                                        >
                                            대여연장
                                        </button>

                                        <button
                                            disabled={item.state !== "ACCEPT" || item.reqState === "WAIT"}
                                            onClick={() => handleActionClick(item.demNum, "반납")}
                                            className={`px-2 py-1 rounded text-xs w-full ${item.state === "ACCEPT" && item.reqState !== "WAIT"
                                                    ? "bg-red-500 hover:bg-red-600 text-white cursor-pointer"
                                                    : "bg-gray-300 text-gray-500 cursor-not-allowed"
                                                }`}
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

            {showModifyModal && (
                <div className="fixed top-0 left-0 w-full h-full bg-black bg-opacity-50 z-50 flex justify-center items-center">
                    <div className="bg-white p-6 rounded shadow-md w-[500px]">
                        <h2 className="text-xl mb-4 font-bold">예약 날짜 변경</h2>

                        <CalendarComponent
                            selectedDate={selectedDate}
                            setSelectedDate={setSelectedDate}
                            demNum={selectedDemNum}
                            disabledDates={disabledDates}
                            setDisabledDates={setDisabledDates}
                            exceptDate={exceptDate}
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
                                onClick={() => {
                                    setShowQtyModal(true);
                                }}
                            >
                                변경
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {showQtyModal && (
                <ItemModal
                    maxQty={maxQty}
                    value={reservationQty}
                    onChange={(val) => setReservationQty(val)}
                    onConfirm={() => {
                        const selectedItem = listData.content.find(item => item.demNum === selectedDemNum);
                        const updatedItemNum = maxQty - reservationQty + (selectedItem?.bitemNum ?? 0);
                        reservationUpdate(updatedItemNum);
                        setShowQtyModal(false);
                    }}
                    onClose={() => setShowQtyModal(false)}
                />
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
