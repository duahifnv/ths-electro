import { useEffect, useState } from "react";
import { PageBase } from "../../../../react-envelope/components/pages/base/PageBase/PageBase";
import ExButton from "../../../../react-envelope/components/ui/buttons/ExButton/ExButton";
import { Headline } from "../../../../react-envelope/components/ui/labels/Headline/Headline";
import { PowerSupplyItem } from "../../../dummies/PowerSupplyItem/PowerSupplyItem";
import VBoxPanel from "../../../../react-envelope/components/layouts/VBoxPanel/VBoxPanel";
import { Modal } from "../../../../react-envelope/components/wrappers/Modal/Modal";
import { TextBox } from "../../../../react-envelope/components/ui/input/text/TextBox/TextBox";
import { SupplyCalendar } from "../../../widgets/SupplyCalendar/SupplyCalendar";
import { MONTH_NAMES } from "../../../../constants";
import css from './PowerSupplyDataPage.module.css';
import { CreateEnergyRecordModal } from "../../../widgets/modals/CreateEnergyRecordModal/modals/CreateEnergyRecordModal";

export const PowerSupplyDataPage = () => {
    const [records, setRecords] = useState([{
        year: '2026',
        month: '4',
        dailyData: {
            '1': {
                holiday: true,
                data: {
                    '5': '1250'
                }
            }
        }
    }]);
    const [modalActive, setModalActive] = useState(false);
    const [selection, setSelection] = useState(0);

    const [editContext, setEditContext] = useState();
    const [isEdit, setIsEdit] = useState(false);

    const resetContext = () => {
        setEditContext({
            year: new Date().getFullYear(),
            month: new Date().getMonth(),
            dailyData: {}
        });
    }

    useEffect(() => {
        resetContext();
    }, []);

    const [confirmationModalActive, setConfirmationModalActive] = useState(false);

    const handleAdd = () => {
        setIsEdit(false);
        setModalActive(true);
        resetContext();
    };

    const handleEdit = (i) => {
        setEditContext(records[i]);
        setSelection(i);
        setIsEdit(true);
        setModalActive(true);
    };

    const handleDelete = (i) => {
        setSelection(i);
        setConfirmationModalActive(true);
    };

    const handleConfirmedDelete = () => {
        let newData = [...records];
        newData.splice(selection, 1);
        setRecords(newData);
        setConfirmationModalActive(false);
    };

    const handleCreate = () => {
        if (isEdit) {
            let new_records = [...records];
            new_records[selection] = editContext;
            setRecords(new_records);
        } else {
            setRecords([
                ...records,
                editContext
            ]);
        }

        resetContext();

        setModalActive(false);
    };

    return (
        <PageBase>
            <Headline>База данных тарифов</Headline>

            <ExButton onClick={handleAdd} className={`accent-button start-self`}>Добавить запись</ExButton>


            <VBoxPanel gap={'10px'}>
                {records.slice().sort((a, b) => {
                    if (a.year !== b.year) {
                        return Number(a.year) - Number(b.year);
                    }
                    return Number(a.month) - Number(b.month);
                }).map((d, i) => <PowerSupplyItem key={i} year={d.year} month={d.month}
                    onDelete={() => handleDelete(i)}
                    onEdit={() => handleEdit(i)} />)}
            </VBoxPanel>

            <CreateEnergyRecordModal isEnabled={modalActive}
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
            </Modal>
        </PageBase>
    );
};