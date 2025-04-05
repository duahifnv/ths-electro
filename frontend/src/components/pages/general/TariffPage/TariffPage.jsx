import { useState } from "react";
import VBoxPanel from "../../../../react-envelope/components/layouts/VBoxPanel/VBoxPanel";
import { PageBase } from "../../../../react-envelope/components/pages/base/PageBase/PageBase";
import { Headline } from "../../../../react-envelope/components/ui/labels/Headline/Headline";
import css from './TariffPage.module.css';
import ExButton from "../../../../react-envelope/components/ui/buttons/ExButton/ExButton";
import { SupplyCalendar } from "../../../widgets/SupplyCalendar/SupplyCalendar";
import { TextBox } from "../../../../react-envelope/components/ui/input/text/TextBox/TextBox";
import { MONTH_NAMES } from "../../../../constants";

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

    const handleCalculate = () => {

    };

    return (
        <PageBase>
            <Headline>Исходные данные</Headline>
            <p>Внесите Ваши данные за определенный месяц, чтобы алгоритм мог расчитать рекомендуемые тарифные планы.</p>

            <VBoxPanel className={css.content} gap={'20px'}>
                <span className={css.subtitle}>Месяц (рекомендуется выбирать <accent>летний</accent> месяц)</span>

                <div className={css.inputGroup}>
                    <select
                        value={month}
                        onChange={(e) => setMonth(parseInt(e.target.value))}
                    >
                        {MONTH_NAMES.map((name, index) => (
                            <option key={name} value={index}>{name}</option>
                        ))}
                    </select>
                </div>

                <TextBox borderType={'fullr'}
                    placeholder={`Введите потребление в кВт*ч`}
                    label={`Пиковое потребление за ${MONTH_NAMES[month]}`}
                    labelBackground={'var(--bk-color)'}
                    value={maxConsumption}
                    onChange={setMaxConsumption}
                    type="number" />

                <span className={css.subtitle}>Энергопотребление по дням</span>
                <p>Нажмите на день, чтобы ввести почасовое потребление</p>

                <SupplyCalendar
                    month={month}
                    year={year}
                    dailyData={dailyData}
                    onChange={handleDayDataChange}
                    className={css.calendar}
                />

                <ExButton className={'accent-button'} onClick={handleCalculate}>Расчитать</ExButton>
            </VBoxPanel>
        </PageBase>
    );
};