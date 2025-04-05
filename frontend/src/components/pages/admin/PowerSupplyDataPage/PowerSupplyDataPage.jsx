import { useState } from "react";
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
    const [records, setRecords] = useState([]);
    const [modalActive, setModalActive] = useState(false);
    const [tempDeleteSelection, setTempDeletionSelection] = useState(0);

    const [editContext, setEditContext] = useState({
        year: new Date().getFullYear(),
        month: new Date().getMonth(),
        dailyData: {}
    });

    const [confirmationModalActive, setConfirmationModalActive] = useState(false);
    const [data, setData] = useState([{
        year: '2026',
        month: '4',
        dailyData: {
            '10': {
                holiday: false,
                data: {
                    '5': '1250'
                }
            }
        }
    }]);

    const handleAdd = () => {
        setModalActive(true);
    };

    const handleEdit = (i) => {
        setModalActive(true);
    };

    const handleDelete = (i) => {
        setTempDeletionSelection(i);
        setConfirmationModalActive(true);
    };

    const handleConfirmedDelete = () => {
        let newData = [...data];
        newData.splice(tempDeleteSelection, 1);
        setData(newData);
        setConfirmationModalActive(false);
    };

    return (
        <PageBase>
            <Headline>База данных тарифов</Headline>

            <ExButton onClick={handleAdd} className={`accent-button start-self`}>Добавить запись</ExButton>


            <VBoxPanel gap={'10px'}>
                {data.map((d, i) => <PowerSupplyItem key={i} year={d.year} month={d.month}
                    onDelete={() => handleDelete(i)}
                    onEdit={() => handleEdit(i)} />)}
            </VBoxPanel>

            <CreateEnergyRecordModal isEnabled={modalActive}
                                     onCloseRequested={() => setModalActive(false)}
                                     editContext={editContext}
                                     setEditContext={setEditContext}/>

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