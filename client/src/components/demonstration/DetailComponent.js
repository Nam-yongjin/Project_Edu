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
    const [disabledDates, setDisabledDates] = useState([]); // 캘린더에서 disabled할 날짜 배열
    const [dem, setDem] = useState([]); // 서버에서 받아올 여러가지 값들
    const [fileList, setFileList] = useState({ imageList: [] }); // 서버에서 받아올 이미지 리스트
    const [mainImageUrl, setMainImageUrl] = useState(); // 대표 이미지
    const [modalOpen, setModalOpen] = useState(false); // 이미지 창 띄우기 위한 모달 state 변수
    const [selectedImages, setSelectedImages] = useState([]); // 모달창에 이미지 전달을 위한 state 변수
    const { moveToPath } = useMove(); // useMove에서 가져온 모듈들
    const [showQtyModal, setShowQtyModal] = useState(false);
    const [reservationQty, setReservationQty] = useState(1);
    const [reserveCheck, setReserveCheck] = useState(false);
    const [selectedDate, setSelectedDate] = useState([]); // 캘린더에서 선택한 날짜를 저장하는 state 변수
    const loginState = useSelector((state) => state.loginState);
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

    useEffect(() => {
        const loadData = async () => { // 상세페이지의 정보 및 이미지 불러오는 코드
            const detailData = await getDetail(demNum);
            setDem(detailData);
            setFileList(detailData);
            const mainImageObj = detailData.imageList.find(img => img.isMain);
            setMainImageUrl(mainImageObj ? `http://localhost:8090/view/${mainImageObj.imageUrl}` : '');
            detailData.imageList.map(img => img.isMain);
            const reserveData = await getReserveCheck(demNum);
            setReserveCheck(reserveData);
        }
        loadData();
    }, [demNum]);

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
            // 기존과 중복 제거
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

            if (reserveCheck) { // 이미 해당 물품을 예약한 회원 일경우, return
                alert('이미 해당 물품을 예약 하셨습니다.');
                return;
            }

            try {
                await postRes(
                    toLocalDateString(startDate), toLocalDateString(endDate), demNum, updatedItemNum);
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
                    <div className="newText-3xl font-bold ">실증 물품 대여 예약</div>
                    <div className="w-full overflow-x-auto">
                        <div className="flex gap-10 min-w-full border-2 border-black p-2">
                            {/* 이미지 영역 */}
                            <div className="w-1/2 min-h-[500px]">
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
                                    className="min-w-[500px] min-h-[500px] object-cover rounded  border border-black"
                                />
                            </div>

                            <ImageSliderModal
                                open={modalOpen}
                                onClose={() => setModalOpen(false)}
                                imageList={selectedImages}
                            />

                            {/* 설명 영역 */}
                            <div className="w-1/2 max-h-[500px] overflow-y-auto break-words p-6 bg-gray-50 rounded-lg">
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
                                        <span className="text-blue-600 text-lg font-bold mb-1">수량</span>
                                        <div className="flex items-center gap-2">
                                            <span className={`text-lg font-bold ${dem.itemNum > 0 ? 'text-green-600' : 'text-red-600'}`}>
                                                {dem.itemNum}개
                                            </span>
                                            <span className={`text-sm px-2 py-1 rounded-full ${dem.itemNum > 0 ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                                                }`}>
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

                    <div>
                        <div className="flex mt-2 w-1/2 whitespace-nowrap">
                            <h2 className="newText-base text-blue-600 font-bold mr-3">날짜 선택</h2> <span className="text-xs mr-3 mt-1">(신청기간은 연속으로 최소 하루 부터 신청 가능합니다.(최대90일))</span>
                            <div className="flex gap-2 newText-xs">
                                <div className="w-10 h-5 rounded-full bg-gray-300 flex items-center justify-center border-2 border-black border-solid">
                                    불가
                                </div>
                                <div className="w-10 h-5 rounded-full bg-white flex items-center justify-center border-2 border-black border-solid">
                                    가능
                                </div>
                                <div className="w-10 h-5 rounded-full bg-emerald-100 flex items-center justify-center border-2 border-black border-solid">
                                    선택
                                </div>
                            </div>
                        </div>
                        <div className="flex flex-wrap lg:flex-nowrap gap-x-10 mt-6">
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

                            <div className="space-y-6 w-1/2 mx-auto">
                                {/* 날짜 선택 박스 */}
                                <div className="border border-black p-6 rounded h-[150px] flex flex-col justify-center space-y-4">
                                    <div className="flex items-center gap-4">
                                        <img src={calendar} className="w-8 h-8" alt="calendar" />
                                        {/* <a href="https://www.flaticon.com/free-icons/calendar" title="calendar icons">Calendar icons created by Stockio - Flaticon</a>*/}
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
                                        {/* <a href="https://www.flaticon.com/free-icons/calendar" title="calendar icons">Calendar icons created by Stockio - Flaticon</a>*/}
                                        <label className="newText-base font-bold w-[120px]">예약 종료일:</label>
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
                                        maxQty={dem.itemNum} // 현재 남은 재고 수량 전달
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
                                    {/*<a href="https://www.flaticon.com/free-icons/advertising" title="advertising icons">Advertising icons created by Tanah Basah - Flaticon</a> */}
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
}
export default DetailComponent;
