import { useState } from "react";
import VBoxPanel from "../../../../react-envelope/components/layouts/VBoxPanel/VBoxPanel";
import { PageBase } from "../../../../react-envelope/components/pages/base/PageBase/PageBase";
import { Headline } from "../../../../react-envelope/components/ui/labels/Headline/Headline";
import css from './TariffPage.module.css';
import ExButton from "../../../../react-envelope/components/ui/buttons/ExButton/ExButton";
import { SupplyCalendar } from "../../../widgets/SupplyCalendar/SupplyCalendar";
import { TextBox } from "../../../../react-envelope/components/ui/input/text/TextBox/TextBox";

// Основная страница
export const TariffPage = () => {
  const [month, setMonth] = useState(new Date().getMonth());
  const [year, setYear] = useState(new Date().getFullYear());
  const [maxConsumption, setMaxConsumption] = useState(null);
  const [dailyData, setDailyData] = useState({});

  // Обработчик изменения данных дня
  const handleDayDataChange = (day, hourlyData) => {
    setDailyData(prev => ({
      ...prev,
      [day]: hourlyData
    }));
  };

  // Получаем название месяца
  const monthNames = [
    'Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь',
    'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'
  ];

  return (
    <PageBase>
      <Headline>Исходные данные</Headline>
      <p>Внесите Ваши данные, чтобы алгоритм мог расчитать рекомендуемые тарифные планы.</p>

      <VBoxPanel className={css.content} gap={'20px'}>
        <div className={css.inputGroup}>
          <label>Месяц (рекомендуется выбирать летний месяц)</label>
          <select
            value={month}
            onChange={(e) => setMonth(parseInt(e.target.value))}
          >
            {monthNames.map((name, index) => (
              <option key={name} value={index}>{name}</option>
            ))}
          </select>
        </div>

        <TextBox borderType={'fullr'}
                 placeholder={`Введите потребление в кВт*ч`}
                 label={`Пиковое потребление за ${monthNames[month]}`}
                 labelBackground={'var(--bk-color)'}
                 value={maxConsumption}
                 onChange={setMaxConsumption}
                 type="number"/>

        <h3>Энергопотребление по дням</h3>
        <p>Нажмите на день, чтобы ввести почасовое потребление</p>

        <SupplyCalendar
          month={month}
          year={year}
          dailyData={dailyData}
          onChange={handleDayDataChange}
          className={css.calendar}
        />
      </VBoxPanel>
    </PageBase>
  );
};