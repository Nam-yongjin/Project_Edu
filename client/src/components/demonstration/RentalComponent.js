import { useEffect, useState } from "react";
import SearchComponent from "../../components/demonstration/SearchComponent";
import { getRental, getRentalSearch, deleteRental, getResExceptDate, updateRental, addRequest } from "../../api/demApi";
import PageComponent from "../common/PageComponent";
import useMove from "../../hooks/useMove";
import CalendarComponent from "./CalendarComponent";
import ItemModal from "./itemModal";
import { useSelector } from "react-redux";
import defaultImage from '../../assets/default.jpg';

const RentalComponent = () => {
    const isTeacher = useSelector((state) => state.loginState?.role === "TEACHER");
    const isAdmin = useSelector((state) => state.loginState?.role === "ADMIN");
    
    // 권한 체크 useEffect
    useEffect(() => {
        if (!isTeacher && !isAdmin) {
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
    const [isExtendModalOpen, setIsExtendModalOpen] = useState(false); // 날짜 연장용 모달창 상태 변수
    const [extendDate, setExtendDate] = useState("");  // 모달용 날짜 상태 변수명 변경
    const [disabledExtendDate, setdisabledExtendDate] = useState([]);

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
                console.log(data);
                setListData(data);
                setPageData(data);
            });
        }
    };

    useEffect(() => {
        fetchData();
        // 페이지 변경 시 선택 상태 초기화
        setSelectedItems(new Set());
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

    // WAIT 상태인 항목들만 필터링
    const waitItems = listData.content.filter(item => item.state === "WAIT");

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

    // 전체 선택/해제 핸들러 수정
    const handleSelectAll = (e) => {
        if (e.target.checked) {
            // WAIT 상태인 항목만 전체 선택
            const allWaitDemNums = new Set(waitItems.map(item => item.demNum));
            setSelectedItems(allWaitDemNums);
        } else {
            // 전체 해제
            setSelectedItems(new Set());
        }
    };

    // 전체 선택 체크박스 상태 계산 수정
    const isAllSelected = waitItems.length > 0 && 
                         waitItems.every(item => selectedItems.has(item.demNum));
    
    // 일부 선택 상태 (indeterminate 상태)
    const isIndeterminate = selectedItems.size > 0 && !isAllSelected;

const handleCancelReservation = () => {
    if (selectedItems.size === 0) {
        alert("선택된 항목이 없습니다.");
        return;
    }

    // 선택된 항목 중 WAIT 상태인 것만 필터링
    const selectedItemsData = listData.content.filter(
        item => selectedItems.has(item.demNum) && item.state.toUpperCase() === "WAIT"
    );

    if (selectedItemsData.length === 0) {
        alert("예약 취소가 불가능한 상태입니다. 대기 상태의 항목만 취소할 수 있습니다.");
        return;
    }

    if (window.confirm(`선택된 ${selectedItemsData.length}개의 예약을 취소하시겠습니까?`)) {
        // WAIT 상태인 demNum만 취소
        deleteRental(selectedItemsData.map(item => item.demNum));
        alert("예약이 취소 되었습니다.");
        window.location.reload();
    }
};



    const handleActionClick = async (demNum, action, date) => {
        // 같은 demNum을 가진 모든 항목을 찾음
        const items = listData.content.filter((item) => item.demNum === demNum);
        if (items.length === 0) return;

        if (action === "예약변경") {
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
            addRequest(demNum, type, date);
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
                .map(d => new Date(d))
                .sort((a, b) => a.getTime() - b.getTime());
            sortedDates = sortedDates.map(d => (d instanceof Date ? d : new Date(d)));

            for (let i = 0; i < sortedDates.length - 1; i++) {
                const diffTime = sortedDates[i + 1].getTime() - sortedDates[i].getTime();
                const diffDays = diffTime / (1000 * 60 * 60 * 24);

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

    const handleExtendButtonClick = (demNum, endDate) => {
        setdisabledExtendDate(endDate);
        setSelectedDemNum(demNum);
        setIsExtendModalOpen(true);
    };

    const handleExtendConfirm = (date) => {
        handleActionClick(selectedDemNum, "대여연장", date);
        setIsExtendModalOpen(false);
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
        <div className="max-w-screen-xl mx-auto my-10">
            <div className="min-blank">
                <div className="mx-auto text-center">
                    <div className="text-3xl font-bold mb-5">실증 물품 등록 관리</div>
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
                <p className="text-gray-700 my-3 text-base px-4 py-2 rounded-md inline-block">
                    전체 <span className="font-bold text-blue-600">{pageData.totalElements}</span>건의 대여내역이 있습니다.
                </p>
                <div className="overflow-x-auto mt-6 page-shadow">
                    <table className="w-full min-w-[1000px]">
                        <thead className="bg-gray-100 text-gray-700 text-base border border-gray-300">
                            <tr className="text-base whitespace-nowrap">
                                <th className="w-[5%] px-2 py-3">
                                    {waitItems.length > 0 && (
                                        <input
                                            type="checkbox"
                                            checked={isAllSelected}
                                            ref={(input) => {
                                                if (input) {
                                                    input.indeterminate = isIndeterminate;
                                                }
                                            }}
                                            onChange={handleSelectAll}
                                            className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                                        />
                                    )}
                                </th>
                                <th className="w-[8%]">대표 이미지</th>
                                <th className="w-[8%]">물품명</th>
                                <th className="w-[10%]">기업명</th>
                                <th className="w-[10%]">신청갯수</th>

                                {[{ label: "시작일", value: "startDate" }, { label: "마감일", value: "endDate" }, { label: "등록일", value: "applyAt" }].map(
                                    ({ label, value }) => (
                                        <th key={value} onClick={() => handleSortChange(value)} className="cursor-pointer w-[8%]">
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
                                    <div className="mb-1">신청 상태</div>
                                    <select
                                        value={statusFilter}
                                        className="input-focus"
                                        onChange={(e) => {
                                            setStatusFilter(e.target.value);
                                            setCurrent(0);
                                        }}>
                                        <option value="">전체</option>
                                        <option value="REJECT">거부</option>
                                        <option value="ACCEPT">수락</option>
                                        <option value="WAIT">대기</option>
                                        <option value="CANCEL">취소</option>
                                        <option value="EXPIRED">만료</option>
                                    </select>
                                </th>
                                <th className="w-[8%]">반납/연장 신청</th>
                                <th className="w-[8%]">예약 수정</th>
                            </tr>
                        </thead>

                        <tbody className="text-gray-600">
                            {listData.content.length === 0 ? (
                                <tr>
                                    <td colSpan={11} className="text-center">
                                        <p className="text-gray-500 text-3xl mt-20 min-h-[300px]">대여 내역이 없습니다.</p>
                                    </td>
                                </tr>
                            ) : (
                                listData.content.map((item) => {
                                    const mainImage = item.imageList?.find((img) => img.isMain === true);
                                    const memberState = item.state;
                                    return (
                                        <tr key={`${item.demNum}_${item.startDate}_${item.endDate}_${item.applyAt}_${item.state}`} className={`hover:bg-gray-50 text-sm text-center whitespace-nowrap <table className="w-full border-collapse border border-gray-300"> ${memberState === "CANCEL" ? "bg-gray-100 text-gray-400" : "hover:bg-gray-50"}`}>
                                            {/* 체크박스 칸 */}
                                            <td className="py-2 px-2 text-center">
                                                {memberState === "WAIT" ? (
                                                    <input
                                                        type="checkbox"
                                                        checked={selectedItems.has(item.demNum)}
                                                        onChange={(e) => handleSelectOne(e, item.demNum)}
                                                        className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                                                    />
                                                ) : (
                                                    <></>
                                                )}
                                            </td>

                                            {/* 이미지 칸 */}
                                            <td className="py-2 px-2 whitespace-nowrap text-center ">
                                                {mainImage ? (
                                                    <img
                                                        onClick={() => moveToPath(`../detail/${item.demNum}`)}
                                                        src={`http://localhost:8090/view/${mainImage.imageUrl}`}
                                                        alt={item.demName}
                                                        className="w-20 h-20 rounded-md hover:scale-105 transition-transform cursor-pointer"
                                                    />
                                                ) : (
                                                    <img
                                                        src={defaultImage}
                                                        alt="default"
                                                        className="w-20 h-20 rounded-md hover:scale-105 transition-transform cursor-pointer"
                                                    />
                                                )}
                                            </td>

                                            {/* 기본 정보 */}
                                            <td title={item.demName} className="truncate max-w-[100px]">{item.demName || "-"}</td>
                                            <td title={item.companyName} className="truncate max-w-[100px]">{item.companyName || "-"}</td>
                                            <td>{item.bitemNum || "-"}</td>
                                            <td>{item.startDate || "-"}</td>
                                            <td>{item.endDate || "-"}</td>
                                            <td>{item.applyAt || "-"}</td>

                                            {/* 상태 표시 칸 */}
                                            <td>
                                                <div>{getStateLabel(item.state)}</div>
                                            </td>

                                            {/* 진행중 요청 칸 */}
                                            <td className="text-base text-blue-600">
                                                {Array.isArray(item.reqState) && Array.isArray(item.requestType) ? (
                                                    item.reqState
                                                        .map((state, idx) => ({ state, type: item.requestType[idx] }))
                                                        .filter(r => r.state === "WAIT")
                                                        .length > 0 ? (
                                                        <ul className="list-disc list-inside">
                                                            {item.reqState
                                                                .map((state, idx) => ({ state, type: item.requestType[idx] }))
                                                                .filter(r => r.state === "WAIT")
                                                                .map((r, idx) => {
                                                                    if (r.type === "RENTAL") return <li key={idx}>반납신청 진행중</li>;
                                                                    if (r.type === "EXTEND") return <li key={idx}>기한연장 진행중</li>;
                                                                    return null;
                                                                })}
                                                        </ul>
                                                    ) : (
                                                        <span>-</span>
                                                    )
                                                ) : (
                                                    <span>-</span>
                                                )}
                                            </td>

                                            {/* 버튼 칸 */}
                                            <td className="my-1 px-2">
                                                <div className="flex flex-col space-y-1 items-center">
                                                    {(() => {
                                                        const reqStates = Array.isArray(item.reqState) ? item.reqState : [];
                                                        const hasWaitState = reqStates.some(state => String(state).toUpperCase() === "WAIT");
                                                        const itemState = String(item.state).toUpperCase();

                                                        return (
                                                            <>
                                                                <button
                                                                    disabled={itemState !== "WAIT"}
                                                                    onClick={() => handleActionClick(item.demNum, "예약변경")}
                                                                    className={`px-2 py-1 rounded text-xs w-full ${itemState === "WAIT"
                                                                        ? "positive-button"
                                                                        : "disable-button"
                                                                        }`}
                                                                >
                                                                    예약변경
                                                                </button>

                                                                <button
                                                                    disabled={itemState !== "ACCEPT" || hasWaitState}
                                                                    onClick={() => handleExtendButtonClick(item.demNum, item.endDate)}
                                                                    className={`px-2 py-1 rounded text-xs w-full ${itemState === "ACCEPT" && !hasWaitState
                                                                        ? "green-button"
                                                                        : "disable-button"
                                                                        }`}
                                                                >
                                                                    대여연장
                                                                </button>

                                                                <button
                                                                    disabled={itemState !== "ACCEPT" || hasWaitState}
                                                                    onClick={() => handleActionClick(item.demNum, "반납")}
                                                                    className={`px-2 py-1 rounded text-xs w-full ${itemState === "ACCEPT" && !hasWaitState
                                                                        ? "nagative-button"
                                                                        : "disable-button"
                                                                        }`}
                                                                >
                                                                    반납
                                                                </button>
                                                            </>
                                                        );
                                                    })()}
                                                </div>
                                            </td>
                                        </tr>
                                    );
                                })
                            )}
                        </tbody>
                    </table>
                </div>

                {isExtendModalOpen && (
                    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
                        <div className="bg-white rounded-lg p-6 max-w-md w-full min-blank">
                            <h2 className="text-3xl font-bold mb-1">대여 연장 신청</h2>
                            <label className="block mb-2">연장할 날짜</label>
                            <input
                                type="date"
                                className="border rounded p-2 w-full mb-4"
                                value={extendDate}
                                min={disabledExtendDate}
                                onChange={(e) => setExtendDate(e.target.value)}
                            />

                            <div className="flex justify-end gap-2">
                                <button
                                    className="px-4 py-2 bg-gray-300 rounded"
                                    onClick={() => setIsExtendModalOpen(false)}
                                >
                                    취소
                                </button>
                                <button
                                    className="px-4 py-2 positive-button"
                                    onClick={() => {
                                        if (!extendDate) {
                                            alert("날짜를 선택해주세요.");
                                            return;
                                        }
                                        handleExtendConfirm(extendDate);
                                    }}
                                >
                                    확인
                                </button>
                            </div>
                        </div>
                    </div>
                )}

                {/* 우측 하단 예약 취소 버튼 */}
                <div className="flex justify-end mt-4">
                    <button
                        onClick={handleCancelReservation}
                        disabled={selectedItems.size === 0}
                        className={`px-4 py-2 rounded ${selectedItems.size > 0 ? "normal-button" : "disable-button"}`}
                    >
                        예약 취소 ({selectedItems.size})
                    </button>
                </div>

                {showModifyModal && (
                    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
                        <div className="bg-white rounded-lg p-6 max-w-md w-full min-blank">
                            <h2 className="text-3xl font-bold mb-1">예약 날짜 변경</h2>
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
                                    className="normal-button px-4 py-2 rounded"
                                    onClick={() => setShowModifyModal(false)}
                                >
                                    취소
                                </button>
                                <button
                                    className="positive-button px-4 py-2 rounded"
                                    onClick={() => {
                                        if (!selectedDate) {
                                            alert("날짜를 선택해주세요.");
                                            return;
                                        }
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

                <div className="flex justify-center my-6">
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

export default RentalComponent;