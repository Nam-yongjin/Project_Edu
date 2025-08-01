import { useState } from "react";
import Calendar from "react-calendar";
import 'react-calendar/dist/Calendar.css';
const CalendarComponent = () => {
  const [selectedDates, setSelectedDates] = useState([]); // selectedDates을 배열로 설정
  const today = new Date(); // 날짜 객체 생성
  today.setHours(0, 0, 0, 0); // 오늘 날짜의 자정으로 초기화
  // hours, minutes, seconds, milliseconds

  const disabledDates = ['2025-08-10', '2025-08-15']; // 캘린더에서 disabled할 날짜 배열

  // 문자열로 변환해서 비교나 include함수 사용 위함
  const toLocalDateString = (date) => {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const d = String(date.getDate()).padStart(2, '0');
    return `${y}-${m}-${d}`;
  };

  // 현재 날짜보다 이전 날짜거나,
  // disabledDates에 포함된 날짜일경우
  // disabled 처리 위한 함수
  const isDateDisabled = (date) => {
    const dateStr = toLocalDateString(date);
    return disabledDates.includes(dateStr) || date < today;
  };


  // 요일별, 텍스트 글자 색상 변환 위한 함수
  const getDayClass = (date) => {
    const day = date.getDay();
    if (day === 0) return 'text-red-600'; // 일요일
    if (day === 6) return 'text-blue-600'; // 토요일
    return 'text-black'; // 그 외
  };

// 선택 날짜가 포함되어 있지 않다면, 추가
// 선택 날짜가 포함되어 있다면 삭제.
  const handleReserve = (date) => {
    const dateStr = toLocalDateString(date);
    if (!selectedDates.includes(dateStr)) {
      setSelectedDates([...selectedDates, dateStr]);
    } else {
      setSelectedDates(selectedDates.filter((d) => d !== dateStr));
    }
  };


  // 선택 되어 있는 날짜 여부 반환 함수
  const isSelected = (date) => {
    const dateStr = toLocalDateString(date);
    return selectedDates.includes(dateStr);
  };

  return (
    <>
      <style>{`
        .react-calendar { 
          width: 600px !important;
          max-width: 100%;
        }
        /* .react_calendar: 달력 전체 컨테이너에 대한 스타일 */
        /* react-calendar 같은 외부 라이브러리는 내부에 자체 css를 지정해놓기 때문에*/
        /* 우선순위가 낮으면 무시당함. 그러므로 !important로 css 강제 지정 */
        .react-calendar__tile {
          border: 1px solid #ccc !important;
          position: relative;
          height: 5rem;
          padding: 0.5rem;
          box-sizing: border-box;
        }
        
        /* .react-calendar__tile: 달력의 날짜 셀 하나를 의미. */

        .react-calendar__tile.selected-tile {
          border: 2px solid #0f5132 !important;
          background-color: #d1e7dd !important;
        }

        /* .selected-tile: 선택된 날짜에만 붙는 클래스 (tileClassName에서 조건부로 부여). */

        .react-calendar__tile abbr {
          position: absolute;
          top: 5px;
          left: 5px;
          font-weight: bold;
        }

        /*  타일 안의 <abbr> 태그는 날짜 숫자 (예: 29)를 의미함.*/
      `}</style>

      <Calendar
        tileClassName={({ date, view }) => { // 해당 날짜가 선택 되어 잇다면, selected-title css 적용
            // date는 현재 랜더링 중인 날짜 객체, view는 현재 달력 뷰 모드(여기선 month일 경우 클릭 시, 적용되게 해둠)
          if (view === 'month') {
            return isSelected(date) ? 'selected-tile' : '';
          }
          return '';
        }}

        value={null} // 현재 달력에서 날짜 한 개를 선택하지 않겠다는 의미
        formatDay={(locale, date) => date.getDate()} // 날짜 타일에서 일 표시 안하고 숫자만 표시
        // date.getDate는 해당 날짜 객체에서 일만 반환(숫자로)
        tileDisabled={({ date }) => isDateDisabled(date)} // 날짜 타일 disabled

        // 달력 칸마다 커스터마이징 할 수 있는 콜백 함수
        tileContent={({ date, view }) => { 
          if (view !== 'month') return null;

          const disabled = isDateDisabled(date);

          return (
            <>
              <abbr className={getDayClass(date)}>{date.getDate()}</abbr>
              {disabled ? (
                <div className="absolute bottom-1 left-1/2 -translate-x-1/2 text-xs text-black font-semibold select-none">
                  예약불가
                </div>
              ) : (
                <button
                  className="absolute bottom-1 left-1/2 -translate-x-1/2 text-xs px-2 py-1 rounded bg-blue-600 text-white hover:bg-blue-700"
                  onClick={(e) => {
                    e.stopPropagation();
                    handleReserve(date);
                  }}
                >
                  예약
                </button>
              )}
            </>
          );
        }}
      />
    </>
  );
};

export default CalendarComponent;