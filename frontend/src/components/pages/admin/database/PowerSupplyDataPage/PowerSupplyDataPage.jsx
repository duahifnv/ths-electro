import { useEffect, useState } from "react";
import { PageBase } from "../../../../../react-envelope/components/pages/base/PageBase/PageBase";
import ExButton from "../../../../../react-envelope/components/ui/buttons/ExButton/ExButton";
import { Headline } from "../../../../../react-envelope/components/ui/labels/Headline/Headline";
import { PowerSupplyItem } from "../../../../dummies/PowerSupplyItem/PowerSupplyItem";
import VBoxPanel from "../../../../../react-envelope/components/layouts/VBoxPanel/VBoxPanel";
import { Modal } from "../../../../../react-envelope/components/wrappers/Modal/Modal";
import { TextBox } from "../../../../../react-envelope/components/ui/input/text/TextBox/TextBox";
import { SupplyCalendar } from "../../../../widgets/SupplyCalendar/SupplyCalendar";
import { MONTH_NAMES } from "../../../../../constants";
import css from '../database.module.css';
import { CreateEnergyRecordModal } from "../../../../widgets/modals/CreateEnergyRecordModal/modals/CreateEnergyRecordModal";
import { TNSTitle } from "../../../../dummies/TNSTitle/TNSTitle";
import { RadioBox } from "../../../../../react-envelope/components/ui/selectors/RadioBox/RadioBox";

export const PowerSupplyDataPage = () => {
    // const [records, setRecords] = useState([]);
    // const [modalActive, setModalActive] = useState(false);
    // const [selection, setSelection] = useState(0);

    // const [editContext, setEditContext] = useState();
    // const [isEdit, setIsEdit] = useState(false);

    const [month, setMonth] = useState(new Date().getMonth());
    const [year, setYear] = useState(new Date().getFullYear());
    const [powerMode, setPowerMode] = useState('0');
    const [interval, setInterval] = useState('0');
    const [contractType, setContractType] = useState('0');
    const [tarifsData, setTarifsData] = useState({});
    

    // const resetContext = () => {
    //     setEditContext({
    //         year: new Date().getFullYear(),
    //         month: new Date().getMonth(),
    //         dailyData: {}
    //     });
    // }

    // useEffect(() => {
    //     resetContext();
    // }, []);

    // const [confirmationModalActive, setConfirmationModalActive] = useState(false);

    // const handleAdd = () => {
    //     setIsEdit(false);
    //     setModalActive(true);
    //     resetContext();
    // };

    // const handleEdit = (i) => {
    //     setEditContext(records[i]);
    //     setSelection(i);
    //     setIsEdit(true);
    //     setModalActive(true);
    // };

    // const handleDelete = (i) => {
    //     setSelection(i);
    //     setConfirmationModalActive(true);
    // };

    // const handleConfirmedDelete = () => {
    //     let newData = [...records];
    //     newData.splice(selection, 1);
    //     setRecords(newData);
    //     setConfirmationModalActive(false);
    // };

    // const handleCreate = () => {
    //     if (isEdit) {
    //         let new_records = [...records];
    //         new_records[selection] = editContext;
    //         setRecords(new_records);
    //     } else {
    //         setRecords([
    //             ...records,
    //             editContext
    //         ]);
    //     }

    //     resetContext();

    //     setModalActive(false);
    // };

    return (
        <PageBase title={<TNSTitle />} contentClassName={css.content}>
            <Headline>База данных тарифов</Headline>

            <TextBox label={'Год'}
                placeholder={'Введите год'}
                value={year}
                onChange={setYear}
                type="number"
                borderType={'fullr'}
                labelProps={{ style: { backgroundColor: 'var(--bk-color)'} }} />

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

            <SupplyCalendar year={year} month={month} dailyData={tarifsData} onChange={(day, holiday, hours) => {
                setTarifsData(prev => {
                    return {
                        ...prev,
                        [day]: {
                            // holiday: holiday,
                            hours: hours
                        }
                    }
                })
            }} className='center-self'/>

            {/* <ExButton onClick={handleAdd} className={`accent-button start-self`}>Добавить запись</ExButton> */}


            {/* <VBoxPanel gap={'10px'}>
                {records.slice().sort((a, b) => {
                    if (a.year !== b.year) {
                        return Number(a.year) - Number(b.year);
                    }
                    return Number(a.month) - Number(b.month);
                }).map((d, i) => <PowerSupplyItem key={i} year={d.year} month={d.month}
                    onDelete={() => handleDelete(i)}
                    onEdit={() => handleEdit(i)}
                    dailyData={d.dailyData} />)}
            </VBoxPanel> */}

            {/* <CreateEnergyRecordModal isEnabled={modalActive}
                onCloseRequested={() => setModalActive(false)}
                editContext={editContext}
                setEditContext={setEditContext}
                onCreate={handleCreate} />

            <Modal isEnabled={confirmationModalActive}
                onCloseRequested={() => setConfirmationModalActive(false)}
                primaryButtonText={'Подтвердить'}
                closeButtonText={'Отмена'}
                height="150px"
                bodyClassName={'flex row top-center'}
                title={'Удаление записи'}
                onPrimaryClick={() => handleConfirmedDelete()}>
                <span>Вы уверены что хотите удалить запись?</span>
            </Modal> */}
        </PageBase>
    );
};