import { useState } from "react";
import VBoxPanel from "../../../../react-envelope/components/layouts/VBoxPanel/VBoxPanel";
import { PageBase } from "../../../../react-envelope/components/pages/base/PageBase/PageBase";
import { Headline } from "../../../../react-envelope/components/ui/labels/Headline/Headline";
import css from './TariffPage.module.css';
import ExButton from "../../../../react-envelope/components/ui/buttons/ExButton/ExButton";
import { SupplyCalendar } from "../../../widgets/SupplyCalendar/SupplyCalendar";
import { TextBox } from "../../../../react-envelope/components/ui/input/text/TextBox/TextBox";
import { MONTH_NAMES } from "../../../../constants";
import { RadioBox } from "../../../../react-envelope/components/ui/selectors/RadioBox/RadioBox";
import { TNSTitle } from "../../../dummies/TNSTitle/TNSTitle";
import CheckBox from "../../../../react-envelope/components/ui/input/CheckBox/CheckBox";
import { Switch } from "../../../../react-envelope/components/ui/selectors/Switch/Switch";
import { DragAndDrop } from "../../../ui/DragAndDrop/DragAndDrop";

// Основная страница
export const TariffPage = () => {
    const [month, setMonth] = useState(new Date().getMonth());
    const [powerMode, setPowerMode] = useState('0');
    const [interval, setInterval] = useState('0');
    const [contractType, setContractType] = useState('0');
    const [category, setCategory] = useState('3');

    const [mode, setMode] = useState(0);

    const [maxPower, setMaxPower] = useState('');
    const [sumPower, setSumPower] = useState('');

    const [files, setFiles] = useState([]);

    const [year, setYear] = useState(new Date().getFullYear());
    const [dailyData, setDailyData] = useState({});


    // Обработчик изменения данных дня
    const handleDayDataChange = (day, holiday, hours) => {
        setDailyData(prev => ({
            ...prev,
            [day]: {
                holiday: holiday,
                data: hours
            }
        }));
    };

    const handleCalculate = () => {

    };

    return (
        <PageBase title={<TNSTitle />}>
            <Headline>Исходные данные</Headline>
            <VBoxPanel className={css.content} gap={'20px'}>
                <p>Внесите Ваши данные за определенный месяц, чтобы алгоритм мог расчитать рекомендуемые тарифные планы.</p>

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

                <RadioBox className={css.radiopanel}
                    options={[
                        { value: '0', label: 'BH' },
                        { value: '1', label: 'CH-1' },
                        { value: '2', label: 'CH-2' },
                        { value: '3', label: 'HH' },
                    ]}
                    selectedValue={powerMode}
                    onChange={setPowerMode}
                    name="power-mode"
                    label={'Уровень напряжения'}
                    labelProps={{ style: { backgroundColor: 'var(--bk-color)', color: 'var(--font-color)' } }} />

                <RadioBox className={css.radiopanel}
                    options={[
                        { value: '0', label: 'Менее 670 кВт' },
                        { value: '1', label: '670 кВт — 10 МВт' },
                        { value: '2', label: 'Более 10 МВт' }
                    ]}
                    selectedValue={interval}
                    onChange={setInterval}
                    name="power-interval"
                    label={'Максимальная мощность'}
                    labelProps={{ style: { backgroundColor: 'var(--bk-color)', color: 'var(--font-color)' } }} />

                <RadioBox className={css.radiopanel}
                    options={[
                        { value: '0', label: 'Купля-продажа электроэнергии' },
                        { value: '1', label: 'Договор электроснабжения' }
                    ]}
                    selectedValue={contractType}
                    onChange={setContractType}
                    name="contract-type"
                    label={'Вид договора'}
                    labelProps={{ style: { backgroundColor: 'var(--bk-color)', color: 'var(--font-color)' } }} />

                <RadioBox className={css.radiopanel}
                    options={[
                        { value: '3', label: 'ЦК 3' },
                        { value: '4', label: 'ЦК 4' },
                        { value: '5', label: 'ЦК 5' },
                        { value: '6', label: 'ЦК 6' },
                    ]}
                    selectedValue={category}
                    onChange={setCategory}
                    name="category"
                    label={'Ценовая категория'}
                    labelProps={{ style: { backgroundColor: 'var(--bk-color)', color: 'var(--font-color)' } }} />

                <Switch className={`${css.switch} flex row`} value={mode} onSelect={setMode}>
                    <span>Упрощенное за расчетный период</span>
                    <span>Ручное почасовое</span>
                    <span>Файл почасового рассчета</span>
                </Switch>

                {mode == 0 && <>
                    {/* пиковая, суммарная */}

                    <TextBox value={maxPower}
                        onChange={setMaxPower}
                        label={'Пиковая мощность'}
                        placeholder={'Введите максимальную мощность'}
                        borderType={'fullr'}
                        labelProps={{ style: { backgroundColor: 'var(--bk-color)' } }}
                        type="number" />
                    <TextBox value={sumPower}
                        onChange={setSumPower}
                        label={'Суммарная мощность'}
                        placeholder={'Введите суммарную мощность'}
                        borderType={'fullr'}
                        labelProps={{ style: { backgroundColor: 'var(--bk-color)' } }}
                        type="number" />
                </>}

                {mode == 1 && <>
                    <span className={css.subtitle}>Энергопотребление по дням</span>
                    <p>Нажмите на день, чтобы ввести почасовое потребление</p>

                    <SupplyCalendar
                        month={month}
                        year={year}
                        dailyData={dailyData}
                        onChange={handleDayDataChange}
                        className={css.calendar}
                    />
                </>}

                {mode == 2 && <>
                    {/* Файл */}

                    <DragAndDrop onFilesChange={setFiles} />
                </>}

                <ExButton className={'accent-button'} onClick={handleCalculate}>Расчитать</ExButton>

            </VBoxPanel>
        </PageBase>
    );
};