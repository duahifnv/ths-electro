import { MONTH_NAMES } from '../../../../../constants';
import { TextBox } from '../../../../../react-envelope/components/ui/input/text/TextBox/TextBox';
import { Modal } from '../../../../../react-envelope/components/wrappers/Modal/Modal';
import { SupplyCalendar } from '../../../SupplyCalendar/SupplyCalendar';
import css from './CreateEnergyRecordModal.module.css';

export const CreateEnergyRecordModal = ({
    isEnabled,
    onCloseRequested,
    editContext,
    setEditContext,
    onCreate
}) => {
    return (
        <Modal isEnabled={isEnabled}
            onCloseRequested={onCloseRequested}
            primaryButtonText={'Сохранить'}
            onPrimaryClick={onCreate}
            closeButtonText={'Отмена'}
            title={'Тариф'}
            height="500px"
            bodyClassName={'flex col g10'}>
            <TextBox label={'Год'}
                placeholder={'Введите год'}
                value={editContext?.year}
                onChange={(e) => setEditContext(prev => ({
                    ...prev,
                    year: e
                }))}
                type="number"
                borderType={'fullr'} />
            <select
                className={css.monthSelect}
                value={editContext?.month}
                onChange={(e) => setEditContext(prev => ({
                    ...prev,
                    month: parseInt(e.target.value)
                }))}
            >
                {MONTH_NAMES.map((name, index) => (
                    <option key={name} value={index}>{name}</option>
                ))}
            </select>
            <SupplyCalendar year={editContext?.year} month={editContext?.month} dailyData={editContext?.dailyData}
                onChange={(e) => setEditContext(prev => ({
                    ...prev,
                    dailyData: e
                }))} />
        </Modal>
    );
};