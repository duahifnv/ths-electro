import { useState } from 'react';
import css from './SupplyCalendar.module.css';
import ExButton from '../../../react-envelope/components/ui/buttons/ExButton/ExButton';
import { TextBox } from '../../../react-envelope/components/ui/input/text/TextBox/TextBox';

export const SupplyCalendar = ({
    month,
    year,
    dailyData,
    onChange,
    className,
    holidays = false
}) => {
    const [selectedDay, setSelectedDay] = useState(null);
    const [hourlyInputs, setHourlyInputs] = useState({});
    const [isHoliday, setIsHoliday] = useState(false);

    const monthNames = [
        'Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь',
        'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'
    ];

    const daysInMonth = new Date(year, month + 1, 0).getDate();

    const handleDayClick = (day) => {
        setSelectedDay(day);
        setHourlyInputs(dailyData[day]?.data || {});
        setIsHoliday(dailyData[day]?.holiday || false);
    };

    const handleHourChange = (hour, value) => {
        setHourlyInputs(prev => ({
            ...prev,
            [hour]: value ? parseFloat(value) : null
        }));
    };

    const handleSave = () => {
        if (selectedDay !== null) {
            onChange(selectedDay, isHoliday, hourlyInputs);
            setSelectedDay(null);
        }
    };

    const getDayType = (day) => {
        if (!(day in dailyData)) return 'flat';

        const hours = dailyData[day]?.data || {};
        const filledHours = Object.values(hours).filter(val => val !== null).length;

        if (filledHours === 0) return 'error';
        if (filledHours === 24) return 'success';
        return 'warning';
    };

    const getDayHoliday = (day) => {
        return dailyData[day]?.holiday || false;
    };

    return (
        <div className={`${css.calendar} ${className}`}>
            <span className={css.month}>{monthNames[month]}</span>

            <div className={css.calendarGrid}>
                {Array.from({ length: daysInMonth }, (_, i) => i + 1).map(day => (
                    <ExButton
                        key={day}
                        className={`${css.dayButton}`}
                        type={getDayType(day)}
                        onClick={() => handleDayClick(day)}
                    >
                        {getDayHoliday(day) && <div className={css.holiday}></div>}
                        {day}
                    </ExButton>
                ))}
            </div>

            {/* Модалка для ввода данных по часам */}
            {selectedDay !== null && (
                <div className={css.modal}>
                    <div className={css.modalContent}>
                        <span className={css.dayTitle}>День {selectedDay}</span>
                        {holidays && <div className={css.holidayCheckbox}>
                            <label>
                                <input
                                    type="checkbox"
                                    checked={isHoliday}
                                    onChange={(e) => setIsHoliday(e.target.checked)}
                                />
                                Выходной день
                            </label>
                        </div>}
                        <div className={css.hourInputs}>
                            {Array.from({ length: 24 }, (_, i) => i).map(hour => (
                                <TextBox key={hour}
                                    value={hourlyInputs[hour]}
                                    onChange={(e) => handleHourChange(hour, e)}
                                    type='number'
                                    label={`${hour}:00 - ${hour + 1}:00`}
                                    borderType={'full'} />
                            ))}
                        </div>
                        <div className={css.modalButtons}>
                            <ExButton type={'flat'} onClick={() => setSelectedDay(null)}>Отмена</ExButton>
                            <ExButton className={'accent-button'} onClick={handleSave}>Сохранить</ExButton>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};