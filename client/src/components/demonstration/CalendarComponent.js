import { useState, useEffect, useRef } from "react";
import Calendar from "react-calendar";
import { getResDate } from "../../api/demApi";
import 'react-calendar/dist/Calendar.css';

const CalendarComponent = ({
  selectedDate,
  setSelectedDate,
  demNum,
  disabledDates,
  setDisabledDates,
  exceptDate,
  onMonthChange
}) => {
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  const [calendarDate, setCalendarDate] = useState({
    year: new Date().getFullYear(),
    month: new Date().getMonth()
  });
  const loadedMonths = useRef(new Set());

  useEffect(() => {
    console.log(disabledDates);
    const loadData = async () => {
      const key = `${calendarDate.year}-${String(calendarDate.month + 1).padStart(2, '0')}`;
      if (loadedMonths.current.has(key)) return;
      loadedMonths.current.add(key);

      const { monthStart, monthEnd } = getMonthDates(calendarDate.year, calendarDate.month);
      const data = await getResDate(monthStart, monthEnd, demNum);

      if (Array.isArray(data)) {
        const newDates = data.map(item => new Date(item.demDate));
        const exceptDatesArray = Array.isArray(exceptDate)
          ? exceptDate.map(item => new Date(item.demDate))
          : [];
        const filteredDates = newDates.filter(
          date => !exceptDatesArray.some(exceptDate => date.getTime() === exceptDate.getTime())
        );
        setDisabledDates(prev =>
          Array.from(new Set([...prev, ...filteredDates]))
        );
      }
    };

    loadData();
  }, [calendarDate]);

  function getMonthDates(year, month) {
    const monthStart = new Date(year, month, 1);
    const monthEnd = new Date(year, month + 1, 0);
    return {
      monthStart: monthStart.toISOString().split('T')[0],
      monthEnd: monthEnd.toISOString().split('T')[0]
    };
  }

  const isDateDisabled = (date) => {
    date.setHours(0, 0, 0, 0);
    return disabledDates.some(d => d.getTime() === date.getTime()) || date <= today;
  };

  const getDayClass = (date) => {
    const day = date.getDay();
    if (day === 0) return 'text-red-600';
    if (day === 6) return 'text-blue-600';
    return 'text-black';
  };

  const toLocalDateString = (date) => {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const d = String(date.getDate()).padStart(2, '0');
    return `${y}-${m}-${d}`;
  }

  const handleReserve = (date) => {
    if (isDateDisabled(date)) return;

    const dateStr = toLocalDateString(date);

    if (!selectedDate.includes(dateStr)) {
      setSelectedDate([...selectedDate, dateStr]);
    } else {
      setSelectedDate(selectedDate.filter((d) => d !== dateStr));
    }
  };

  const isSelected = (date) => {
    if (isDateDisabled(date)) return false;
    const dateStr = toLocalDateString(date);
    return selectedDate.includes(dateStr);
  };

  return (
    <>
      <style>{`
        .react-calendar { 
          width: 600px !important;
          max-width: 100%;
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
        onActiveStartDateChange={({ activeStartDate, view }) => {
          if (view === 'month') {
            setCalendarDate({
              year: activeStartDate.getFullYear(),
              month: activeStartDate.getMonth()
            });
            if (onMonthChange) onMonthChange(activeStartDate.getFullYear(), activeStartDate.getMonth());
          }
        }}
        tileClassName={({ date, view }) => {
          if (view === 'month') {
            return isSelected(date) ? 'selected-tile' : '';
          }
          return '';
        }}
        value={null}
        formatDay={(locale, date) => date.getDate()}
        tileDisabled={({ date }) => isDateDisabled(date)}
        tileContent={({ date, view }) => {
          if (view !== 'month') return null;
          const disabled = isDateDisabled(date);

          return (
            <>
              <abbr className={getDayClass(date)}>{date.getDate()}</abbr>
              {disabled ? (
                <div className="absolute bottom-1 left-1/2 -translate-x-1/2 text-[10px] text-black newText-xs whitespace-nowrap select-none">
                  예약불가
                </div>
              ) : (
                <button
                  className="absolute bottom-1 left-1/2 -translate-x-1/2 newText-xs px-2 py-1 w-[50px] text-center rounded positive-button"
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
