import { useState } from "react";
import Calendar from "react-calendar";
import 'react-calendar/dist/Calendar.css';

const CustomCalendar = () => {
  const [selectedDates, setSelectedDates] = useState([]);
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  const disabledDates = ['2025-08-10', '2025-08-15'];

  const toLocalDateString = (date) => {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const d = String(date.getDate()).padStart(2, '0');
    return `${y}-${m}-${d}`;
  };

  const isDateDisabled = (date) => {
    const dateStr = toLocalDateString(date);
    return disabledDates.includes(dateStr) || date < today;
  };

  const getDayClass = (date) => {
    const day = date.getDay();
    if (day === 0) return 'text-red-600'; // 일요일
    if (day === 6) return 'text-blue-600'; // 토요일
    return 'text-black';
  };

  const handleReserve = (date) => {
    const dateStr = toLocalDateString(date);
    if (!selectedDates.includes(dateStr)) {
      setSelectedDates([...selectedDates, dateStr]);
    } else {
      setSelectedDates(selectedDates.filter((d) => d !== dateStr));
    }
  };

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
          margin: 0 auto;
        }

        .react-calendar__tile {
          border: 1px solid #ccc !important;
          position: relative;
          height: 5rem;
          padding: 0.5rem;
          box-sizing: border-box;
        }

        .react-calendar__tile.selected-tile {
          border: 2px solid #0f5132 !important;
          background-color: #d1e7dd !important;
        }

        .react-calendar__tile abbr {
          position: absolute;
          top: 5px;
          left: 5px;
          font-weight: bold;
        }
      `}</style>

      <Calendar
        tileClassName={({ date, view }) => {
          if (view === 'month') {
            return isSelected(date) ? 'selected-tile' : '';
          }
          return '';
        }}
        value={null}
        minDate={today}
        formatDay={(locale, date) => date.getDate()}
        tileDisabled={({ date }) => isDateDisabled(date)}
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

export default CustomCalendar;
