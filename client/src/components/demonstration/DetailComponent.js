import CalendarComponent from "./CalendarComponent";
import { useState, useEffect } from "react";
import { getDetail, postRes, getResDate, getReserveCheck } from "../../api/demApi";
import ImageSliderModal from "./ImageSliderModal";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { ko } from "date-fns/locale";
import megaphone from '../../assets/megaphone.png';
import calendar from '../../assets/calendar.png';
import useMove from "../../hooks/useMove";
import ItemModal from "./itemModal";
import { useSelector } from "react-redux";

const DetailComponent = ({ demNum }) => {
    const [disabledDates, setDisabledDates] = useState([]);
    const [dem, setDem] = useState([]);
    const [fileList, setFileList] = useState({ imageList: [] });
    const [mainImageUrl, setMainImageUrl] = useState();
    const [modalOpen, setModalOpen] = useState(false);
    const [selectedImages, setSelectedImages] = useState([]);
    const { moveToPath } = useMove();
    const [showQtyModal, setShowQtyModal] = useState(false);
    const [reservationQty, setReservationQty] = useState(1);
    const [reserveCheck, setReserveCheck] = useState(false);
    const [selectedDate, setSelectedDate] = useState([]);
    const loginState = useSelector((state) => state.loginState);
    const [startDate, setStartDate] = useState(() => {
        const d = new Date();
        d.setDate(d.getDate() + 1);
        return d;
    });
    const [endDate, setEndDate] = useState(() => {
        const d = new Date();
        d.setDate(d.getDate() + 1);
        return d;
    });

    useEffect(() => {
        const loadData = async () => {
            const detailData = await getDetail(demNum);
            setDem(detailData);
            setFileList(detailData);
            const mainImageObj = detailData.imageList.find(img => img.isMain);
            setMainImageUrl(mainImageObj ? `http://localhost:8090/view/${mainImageObj.imageUrl}` : '');
            const reserveData = await getReserveCheck(demNum);
            setReserveCheck(reserveData);
        };
        loadData();
    }, [demNum]);

    useEffect(() => {
        if (selectedDate && selectedDate.length > 0) {
            const dates = selectedDate.map(d => (d instanceof Date ? d : new Date(d)));
            const minDate = new Date(Math.min(...dates.map(d => d.getTime())));
            const maxDate = new Date(Math.max(...dates.map(d => d.getTime())));
            setStartDate(minDate);
            setEndDate(maxDate);
        }
    }, [selectedDate]);

    const handleStartDateChange = (date) => {
        if (!date) return;
        const newStart = date instanceof Date ? date : new Date(date.replace(/-/g, "/"));
        setStartDate(newStart);

        if (endDate && newStart <= endDate) {
            const dates = [];
            let current = new Date(newStart);
            while (current <= endDate) {
                dates.push(toLocalDateString(new Date(current)));
                current.setDate(current.getDate() + 1);
            }
            setSelectedDate(dates);
        } else {
            setSelectedDate([toLocalDateString(newStart)]);
        }
    };

    const handleEndDateChange = (date) => {
        if (!date) return;
        const newEnd = date instanceof Date ? date : new Date(date.replace(/-/g, "/"));
        setEndDate(newEnd);

        if (startDate && startDate <= newEnd) {
            const dates = [];
            let current = new Date(startDate);
            while (current <= newEnd) {
                dates.push(toLocalDateString(new Date(current)));
                current.setDate(current.getDate() + 1);
            }
            setSelectedDate(dates);
        } else {
            setSelectedDate([toLocalDateString(newEnd)]);
        }
    };

    function toLocalDateString(date) {
        const y = date.getFullYear();
        const m = String(date.getMonth() + 1).padStart(2, '0');
        const d = String(date.getDate()).padStart(2, '0');
        return `${y}-${m}-${d}`;
    }

    const fetchDisabledDates = async (year, month) => {
        const monthStart = new Date(year, month, 1);
        const monthEnd = new Date(year, month + 1, 0);
        const formattedStart = toLocalDateString(monthStart);
        const formattedEnd = toLocalDateString(monthEnd);
        const data = await getResDate(formattedStart, formattedEnd, demNum);

        if (Array.isArray(data)) {
            const newDates = data.map(item => item.demDate);
            setDisabledDates(prev => Array.from(new Set([...prev, ...newDates])));
        }
    };

    const reservation = (updatedItemNum) => {
        if (loginState.role !== "TEACHER") {
            alert("교사만 예약 가능합니다.");
            return;
        }
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
            if (reserveCheck) {
                alert('이미 해당 물품을 예약 하셨습니다.');
                return;
            }
            try {
                await postRes(
                    toLocalDateString(startDate), toLocalDateString(endDate), demNum, updatedItemNum
                );
                alert('예약 신청 완료');
                moveToPath(`/demonstration/list`);
            } catch (error) {
                console.error('예약 실패:', error);
                alert('예약에 실패했습니다.');
            }
        };
        loadData();
    };

    return (
        <>
            <div className="max-w-screen-xl mx-auto my-10">
                <div className="min-blank">
                    <div className="mx-auto text-center">
                        <div className="newText-3xl font-bold mb-5">실증 물품</div>
                    </div>
                    <div className="w-full overflow-x-auto">
                        <div className="flex flex-col lg:flex-row gap-10 min-w-full border border-gray-300 p-2">
                            <div className="w-full lg:w-1/2 min-h-[500px]">
                                <img
                                    onClick={() => {
                                        const urlList = fileList.imageList.map(
                                            (img) => `http://localhost:8090/view/${img.imageUrl}`
                                        );
                                        setSelectedImages(urlList);
                                        setModalOpen(true);
                                    }}
                                    src={mainImageUrl}
                                    alt="default"
                                    className="min-w-[500px] min-h-[500px] object-cover rounded border border-black"
                                />
                            </div>

                            <ImageSliderModal
                                open={modalOpen}
                                onClose={() => setModalOpen(false)}
                                imageList={selectedImages}
                            />

                            {/* 설명 영역 */}
                            <div className="w-full lg:w-1/2 max-h-[500px] overflow-y-auto break-words p-6 bg-gray-50 rounded-lg">
                                {/* 카테고리 */}
                                <div className="mb-6 pb-4 border-b border-gray-300">
                                    <span className="inline-block bg-blue-600 text-white px-3 py-1 rounded-full text-lg font-bold">
                                        {dem.category}
                                    </span>
                                </div>
                                {/* 물품 정보 */}
                                <div className="space-y-4">
                                    <div className="flex flex-col">
                                        <span className="text-blue-600 text-lg font-bold mb-1">물품명</span>
                                        <span className="text-gray-800 text-xl font-semibold">{dem.demName}</span>
                                    </div>
                                    <div className="flex flex-col">
                                        <span className="text-blue-600 text-lg font-bold mb-1">제조사</span>
                                        <span className="text-gray-700 text-lg">{dem.demMfr}</span>
                                    </div>
                                    <div className="flex flex-col">
                                        <span className="text-blue-600 text-lg font-bold mb-1">기업명</span>
                                        <span className="text-gray-700 text-lg">{dem.companyName}</span>
                                    </div>
                                    <div className="flex flex-col">
                                        <span className="text-blue-600 text-lg font-bold mb-1">마감일</span>
                                        <span className="text-gray-700 text-lg">{dem.expDate}</span>
                                    </div>
                                    <div className="flex flex-col">
                                        <span className="text-blue-600 text-lg font-bold mb-1">수량</span>
                                        <div className="flex items-center gap-2">
                                            <span className={`text-lg font-bold ${dem.itemNum > 0 ? 'text-green-600' : 'text-red-600'}`}>
                                                {dem.itemNum}개
                                            </span>
                                            <span className={`text-sm px-2 py-1 rounded-full ${dem.itemNum > 0 ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                                                {dem.itemNum > 0 ? '대여 가능' : '대여 불가'}
                                            </span>
                                        </div>
                                    </div>
                                    <div className="flex flex-col">
                                        <span className="text-blue-600 text-lg font-bold mb-2">물품소개</span>
                                        <div className="bg-white p-4 rounded-md border border-gray-200 shadow-sm">
                                            <span className="text-gray-700 leading-relaxed whitespace-pre-wrap">{dem.demInfo}</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* 날짜 선택 및 예약 영역 */}
                    <div>
                        <div className="flex mt-2 w-full lg:w-1/2 whitespace-nowrap">
                            <h2 className="newText-base text-blue-600 font-bold mr-3">날짜 선택</h2>
                            <span className="text-xs mr-3 mt-1">(신청기간은 연속으로 최소 하루 부터 신청 가능합니다.(최대90일))</span>
                            <div className="flex gap-2 newText-xs">
                                <div className="w-10 h-5 rounded-full bg-gray-300 flex items-center justify-center border-2 border-black">불가</div>
                                <div className="w-10 h-5 rounded-full bg-white flex items-center justify-center border-2 border-black">가능</div>
                                <div className="w-10 h-5 rounded-full bg-emerald-100 flex items-center justify-center border-2 border-black">선택</div>
                            </div>
                        </div>

                        {/* 🔥 반응형 적용 */}
                        <div className="flex flex-col lg:flex-row gap-x-10 mt-6">
                            {/* 캘린더 */}
                            <div className="flex-1 w-full">
                                <CalendarComponent
                                    selectedDate={selectedDate}
                                    setSelectedDate={setSelectedDate}
                                    demNum={demNum}
                                    disabledDates={disabledDates}
                                    setDisabledDates={setDisabledDates}
                                />
                            </div>

                            {/* 날짜선택/예약 영역 */}
                            <div className="space-y-6 w-full lg:w-1/2 mx-auto mt-6 lg:mt-0">
                                {/* 날짜 선택 박스 */}
                                <div className="border border-black p-6 rounded h-[150px] flex flex-col justify-center space-y-4">
                                    <div className="flex items-center gap-4">
                                        <img src={calendar} className="w-8 h-8" alt="calendar" />
                                        <label className="text-lg font-semibold w-[120px]">예약 시작일:</label>
                                        <DatePicker
                                            className="border p-2 text-base flex-1 min-w-0 box-border"
                                            selected={startDate}
                                            onChange={handleStartDateChange}
                                            dateFormat="yyyy-MM-dd"
                                            placeholderText="날짜를 선택하세요"
                                            minDate={new Date()}
                                            name="startDate"
                                            locale={ko}
                                            onMonthChange={(date) => fetchDisabledDates(date.getFullYear(), date.getMonth())}
                                            excludeDates={disabledDates.map(d => new Date(d))}
                                        />
                                    </div>
                                    <div className="flex items-center gap-4">
                                        <img src={calendar} className="w-8 h-8" alt="calendar" />
                                        <label className="text-lg font-semibold w-[120px]">예약 종료일:</label>
                                        <DatePicker
                                            className="border p-2 text-base flex-1 min-w-0 box-border"
                                            selected={endDate}
                                            onChange={handleEndDateChange}
                                            dateFormat="yyyy-MM-dd"
                                            placeholderText="날짜를 선택하세요"
                                            minDate={new Date()}
                                            name="endDate"
                                            locale={ko}
                                            onMonthChange={(date) => fetchDisabledDates(date.getFullYear(), date.getMonth())}
                                            excludeDates={disabledDates.map(d => new Date(d))}
                                        />
                                    </div>
                                </div>

                                {/* 예약 버튼 */}
                                <div className="space-y-2">
                                    <button
                                        className="positive-button py-3 px-6 rounded w-full newText-base"
                                        onClick={() => setShowQtyModal(true)}
                                    >
                                        예약 신청하기
                                    </button>
                                </div>

                                {showQtyModal && (
                                    <ItemModal
                                        maxQty={dem.itemNum}
                                        value={reservationQty}
                                        onChange={(val) => setReservationQty(val)}
                                        onConfirm={() => {
                                            setShowQtyModal(false);
                                            setDem(prev => {
                                                const updatedDem = { ...prev, itemNum: prev.itemNum - reservationQty };
                                                reservation(updatedDem.itemNum);
                                                return updatedDem;
                                            });
                                        }}
                                        onClose={() => setShowQtyModal(false)}
                                    />
                                )}

                                {/* 안내 박스 */}
                                <div className="border border-black p-6 rounded flex items-center gap-4 bg-gray-50 h-[150px] mt-4 newText-base">
                                    <img src={megaphone} className="w-10 h-10" alt="megaphone" />
                                    <div className="leading-relaxed flex-1">
                                        관리자의 승인 후 예약이 확정되며, 마이페이지에서 확인하실 수 있습니다.<br />
                                        <span className="text-red-600 font-bold">* 24시간 이내 승인 (주말 제외)</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
};

export default DetailComponent;
