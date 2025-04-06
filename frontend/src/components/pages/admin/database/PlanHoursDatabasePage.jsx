import { useState } from 'react';
import css from './database.module.css';
import { TNSTitle } from '../../../dummies/TNSTitle/TNSTitle';
import { PageBase } from '../../../../react-envelope/components/pages/base/PageBase/PageBase';
import { Headline } from '../../../../react-envelope/components/ui/labels/Headline/Headline';
import { TextBox } from '../../../../react-envelope/components/ui/input/text/TextBox/TextBox';
import { RadioBox } from '../../../../react-envelope/components/ui/selectors/RadioBox/RadioBox';
import ExButton from '../../../../react-envelope/components/ui/buttons/ExButton/ExButton';
import { Modal } from '../../../../react-envelope/components/wrappers/Modal/Modal';
import { MONTH_NAMES } from '../../../../constants';
import HBoxPanel from '../../../../react-envelope/components/layouts/HBoxPanel/HBoxPanel';
import VBoxPanel from '../../../../react-envelope/components/layouts/VBoxPanel/VBoxPanel';

export const PlanHoursDatabasePage = () => {
    const [year, setYear] = useState(new Date().getFullYear());
    const [month, setMonth] = useState(new Date().getMonth());
    const [powerMode, setPowerMode] = useState('1');
    const [interval, setInterval] = useState('1');
    const [contractType, setContractType] = useState('1');
    const [category, setCategory] = useState('4');

    const [hoursData, setHoursData] = useState({});

    const [modal, setModal] = useState(false);
    const [selected, select] = useState(null);

    const daysInMonth = new Date(year, month + 1, 0).getDate();

    const handleDayClick = (v) => {
        select(v);
        setModal(true);
    };

    return (
        <PageBase title={<TNSTitle />} contentClassName={css.content}>
            <Headline>База данных отчетных часов</Headline>

            <VBoxPanel className={css.body} gap={'20px'}>
                <HBoxPanel gap={'20px'} className={'stretch-self'} halign="space-between" valign="start">
                    <TextBox label={'Год'}
                        placeholder={'Введите год'}
                        value={year}
                        onChange={setYear}
                        type="number"
                        borderType={'fullr'}
                        className={`flex-1`}
                        labelProps={{ style: { backgroundColor: 'var(--bk-color)' } }} />

                    <div className={`${css.inputGroup} flex-1`}>
                        <select
                            value={month}
                            onChange={(e) => setMonth(parseInt(e.target.value))}
                        >
                            {MONTH_NAMES.map((name, index) => (
                                <option key={name} value={index}>{name}</option>
                            ))}
                        </select>
                    </div>
                </HBoxPanel>

                <RadioBox className={css.radiopanel}
                    options={[
                        { value: '1', label: 'BH' },
                        { value: '2', label: 'CH-1' },
                        { value: '3', label: 'CH-2' },
                        { value: '4', label: 'HH' },
                    ]}
                    selectedValue={powerMode}
                    onChange={setPowerMode}
                    name="power-mode"
                    label={'Уровень напряжения'}
                    labelProps={{ style: { backgroundColor: 'var(--bk-color)', color: 'var(--font-color)' } }} />

                <RadioBox className={css.radiopanel}
                    options={[
                        { value: '1', label: 'Менее 670 кВт' },
                        { value: '2', label: '670 кВт — 10 МВт' },
                        { value: '3', label: 'Более 10 МВт' }
                    ]}
                    selectedValue={interval}
                    onChange={setInterval}
                    name="power-interval"
                    label={'Максимальная мощность'}
                    labelProps={{ style: { backgroundColor: 'var(--bk-color)', color: 'var(--font-color)' } }} />

                <RadioBox className={css.radiopanel}
                    options={[
                        { value: '1', label: 'Купля-продажа электроэнергии' },
                        { value: '2', label: 'Договор электроснабжения' }
                    ]}
                    selectedValue={contractType}
                    onChange={setContractType}
                    name="contract-type"
                    label={'Вид договора'}
                    labelProps={{ style: { backgroundColor: 'var(--bk-color)', color: 'var(--font-color)' } }} />

                <RadioBox className={css.radiopanel}
                    options={[
                        { value: '4', label: 'ЦК 4' },
                        { value: '6', label: 'ЦК 6' },
                    ]}
                    selectedValue={category}
                    onChange={setCategory}
                    name="category"
                    label={'Ценовая категория'}
                    labelProps={{ style: { backgroundColor: 'var(--bk-color)', color: 'var(--font-color)' } }} />

                <div className={css.days}>
                    {Array.from({ length: daysInMonth }, (v, k) => k + 1).map((val, i) => (
                        <ExButton key={i} className={`${css.day} ${Object.keys(hoursData).includes(`${i}`) && css.counted}`}
                            onClick={() => handleDayClick(i)}>{val}</ExButton>
                    ))}
                </div>

                <ExButton className={'accent-button'} onClick={() => {

                }}>Отправить</ExButton>
            </VBoxPanel>

            <Modal isEnabled={modal}
                onCloseRequested={() => setModal(false)} height='350px'>
                <div className={css.hours}>
                    {Array.from({ length: 24 }, (_, k) => k + 1).map((val, i) => (
                        <ExButton key={i} className={`${css.hour} ${hoursData[selected]?.includes(i) && css.counted}`} onClick={() => {
                            setHoursData(prev => ({
                                ...prev,
                                [selected]: [
                                    ...(prev[selected] || []),
                                    i
                                ]
                            }));
                            // setModal(false);
                        }}>{val - 1}:00</ExButton>
                    ))}
                </div>
            </Modal>
        </PageBase>
    );
};