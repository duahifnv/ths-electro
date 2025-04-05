import { MONTH_NAMES } from '../../../constants';
import { Bin, Edit } from '../../../react-envelope/components/dummies/Icons';
import HBoxPanel from '../../../react-envelope/components/layouts/HBoxPanel/HBoxPanel';
import VBoxPanel from '../../../react-envelope/components/layouts/VBoxPanel/VBoxPanel';
import css from './PowerSupplyItem.module.css';

export const PowerSupplyItem = ({
    year,
    month,
    onDelete,
    onEdit
}) => {
    return (
        <VBoxPanel className={css.item}>
            <HBoxPanel gap={'10px'}>
                <span className='bold'>{year}</span>
                <span>{MONTH_NAMES[month]}</span>
                <span>AVG: <accent>{'???'}</accent> кВт*ч</span>
            </HBoxPanel>
            <HBoxPanel className={css.header} valign='center' halign='end'>
                <Edit onClick={onEdit}/>
                <Bin onClick={onDelete}/>
            </HBoxPanel>
        </VBoxPanel>
    );
};