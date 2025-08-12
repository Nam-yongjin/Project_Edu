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

            if (reserveCheck) { // 이미 해당 상품을 예약한 회원 일경우, return
                alert('이미 해당 상품을 예약 하셨습니다.');
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
            {/* 최상위 div에 mx-auto 추가하여 화면 중앙 배치 */}
            <div className="max-w-screen-lg mb-2 mx-auto">
                {/* 이미지와 텍스트 영역 flex 컨테이너에 justify-center 추가로 가로 중앙 정렬 */}
                <div className="flex items-start gap-4 justify-center">
                    <img
                        onClick={() => {
                            const urlList = fileList.imageList.map(img => `http://localhost:8090/view/${img.imageUrl}`);
                            setSelectedImages(urlList);
                            setModalOpen(true);
                        }}
                        src={mainImageUrl}
                        alt="default"
                        className="w-40 h-40 object-cover mb-10 w-[500px] h-[300px]"
                    />

                    <ImageSliderModal
                        open={modalOpen}
                        onClose={() => setModalOpen(false)}
                        imageList={selectedImages}
                    />

                    {/* 텍스트 영역에 ml-[100px] 제거하여 중앙정렬 방해 안함 */}
                    <div className="space-y-1 mt-3">
                        <span className="text-blue-600">장비명:</span> {dem.demName}
                        <br />
                        <span className="text-blue-600">제조사:</span> {dem.demMfr}
                        <br />
                        <span className="text-blue-600">물품소개:</span> {dem.demInfo}
                        <br />
                        <span className="text-blue-600">수량: </span> {dem.itemNum}
                    </div>
                </div>
                <div>
                    <div className="flex gap-2 mt-2 text-[10px]">
                        <h2 className="text-xl text-blue-600 font-semibold">날짜 선택</h2> (신청기간은 연속으로 최소 하루 부터 신청 가능합니다.(최대90일))
                        <div className="ml-[60px] flex gap-2">
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
                        <div className="flex-1 min-w-[300px]">
                            <CalendarComponent
                                selectedDate={selectedDate}
                                setSelectedDate={setSelectedDate}
                                demNum={demNum}
                                disabledDates={disabledDates}
                                setDisabledDates={setDisabledDates}
                            />
                        </div>

                        {/* 날짜 선택 및 버튼 등 */}
                        <div className="space-y-6 w-full max-w-md">
                            <div className="border border-black p-4 rounded space-y-4 w-full">
                                <div className="flex items-center gap-3">
                                    <img src={calendar} className="w-6 h-6" alt="calendar" />
                                    {/* <a href="https://www.flaticon.com/free-icons/calendar" title="calendar icons">Calendar icons created by Stockio - Flaticon</a>*/}
                                    <label className="text-base font-semibold w-[100px]">예약 시작일:</label>
                                    <DatePicker
                                        className="border p-1 text-sm flex-1 min-w-0 box-border"
                                        selected={startDate}
                                        onChange={handleStartDateChange}
                                        dateFormat="yyyy-MM-dd"
                                        placeholderText="날짜를 선택하세요"
                                        minDate={new Date()}
                                        name="startDate"
                                        locale={ko}
                                        onMonthChange={(date) => {
                                            fetchDisabledDates(date.getFullYear(), date.getMonth());
                                        }}
                                        excludeDates={disabledDates.map(d => new Date(d))}
                                    />
                                </div>

                                <div className="flex items-center gap-3">
                                    <img src={calendar} className="w-6 h-6" alt="calendar" />
                        {/* <a href="https://www.flaticon.com/free-icons/calendar" title="calendar icons">Calendar icons created by Stockio - Flaticon</a>*/}
                                    <label className="text-base font-semibold w-[100px]">예약 종료일:</label>
                                    <DatePicker
                                        className="border p-1 text-sm flex-1 min-w-0 box-border"
                                        selected={endDate}
                                        onChange={handleEndDateChange}
                                        dateFormat="yyyy-MM-dd"
                                        placeholderText="날짜를 선택하세요"
                                        minDate={new Date()}
                                        name="endDate"
                                        locale={ko}
                                        onMonthChange={(date) => {
                                            fetchDisabledDates(date.getFullYear(), date.getMonth());
                                        }}
                                        excludeDates={disabledDates.map(d => new Date(d))}
                                    />
                                </div>
                            </div>

                            <div className="space-y-2">
                                <button
                                    className="bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white py-2 px-4 rounded w-full mb-[190px]"
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

                            <div className="border border-black p-4 rounded flex items-start gap-3 bg-gray-50">
                                <img src={megaphone} className="w-8 h-8 mt-1" alt="megaphone" />
                                {/*<a href="https://www.flaticon.com/free-icons/advertising" title="advertising icons">Advertising icons created by Tanah Basah - Flaticon</a> */}
                                <div className="text-sm leading-relaxed">
                                    관리자의 승인 후 예약이 확정되며, 마이페이지에서 확인하실 수 있습니다.<br />
                                    <span className="text-red-600 font-semibold">* 24시간 이내 승인 (주말 제외)</span>
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
