import { useState } from 'react';
import css from './SupplyCalendar.module.css';
import ExButton from '../../../react-envelope/components/ui/buttons/ExButton/ExButton';

export const SupplyCalendar = ({ month, year, dailyData, onChange }) => {
    const [selectedDay, setSelectedDay] = useState(null);
    const [hourlyInputs, setHourlyInputs] = useState({});
  
    // Получаем количество дней в месяце
    const daysInMonth = new Date(year, month + 1, 0).getDate();
  
    // Обработчик открытия модалки для дня
    const handleDayClick = (day) => {
      setSelectedDay(day);
      setHourlyInputs(dailyData[day] || {});
    };
  
    // Обработчик изменения значения часа
    const handleHourChange = (hour, value) => {
      setHourlyInputs(prev => ({
        ...prev,
        [hour]: value ? parseFloat(value) : null
      }));
    };
  
    // Обработчик сохранения данных
    const handleSave = () => {
      if (selectedDay !== null) {
        onChange(selectedDay, hourlyInputs);
        setSelectedDay(null);
      }
    };
  
    // Получаем цвет для дня в зависимости от заполненности данных
    const getDayType = (day) => {
      // Проверяем, есть ли запись о дне в dailyData
      if (!(day in dailyData)) return 'flat'; // Не редактировался - серый
      
      const dayData = dailyData[day];
      const hours = Object.values(dayData);
      const filledHours = hours.filter(val => val !== null).length;
      
      if (filledHours === 0) return 'error';
      if (filledHours === 24) return 'success';
      return 'warning';
    };
  
    return (
      <div className={css.calendar}>
        <div className={css.calendarGrid}>
          {Array.from({ length: daysInMonth }, (_, i) => i + 1).map(day => (
            <ExButton
              key={day}
              className={css.dayButton}
              type={getDayType(day)}
              onClick={() => handleDayClick(day)}
            >
              {day}
            </ExButton>
          ))}
        </div>
  
        {/* Модалка для ввода данных по часам */}
        {selectedDay !== null && (
          <div className={css.modal}>
            <div className={css.modalContent}>
              <h3>День {selectedDay}</h3>
              <div className={css.hourInputs}>
                {Array.from({ length: 24 }, (_, i) => i).map(hour => (
                  <div key={hour} className={css.hourInput}>
                    <label>{hour}:00</label>
                    <input
                      type="number"
                      value={hourlyInputs[hour] || ''}
                      onChange={(e) => handleHourChange(hour, e.target.value)}
                    />
                  </div>
                ))}
              </div>
              <div className={css.modalButtons}>
                <button onClick={() => setSelectedDay(null)}>Отмена</button>
                <button onClick={handleSave}>Сохранить</button>
              </div>
            </div>
          </div>
        )}
      </div>
    );
  };