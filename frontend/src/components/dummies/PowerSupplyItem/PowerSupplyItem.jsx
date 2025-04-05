import { MONTH_NAMES } from '../../../constants';
import { Bin, Edit } from '../../../react-envelope/components/dummies/Icons';
import HBoxPanel from '../../../react-envelope/components/layouts/HBoxPanel/HBoxPanel';
import VBoxPanel from '../../../react-envelope/components/layouts/VBoxPanel/VBoxPanel';
import css from './PowerSupplyItem.module.css';

export const PowerSupplyItem = ({
    year,
    month,
    dailyData,
    onDelete,
    onEdit
}) => {
    const calculateAverage = () => {
        if (!dailyData) return 0;
        
        let total = 0;
        let count = 0;

        // Перебираем все дни в месяце
        Object.values(dailyData).forEach(day => {            
            // Перебираем все часы в дне
            Object.values(day.data).forEach(hourValue => {
                const value = parseFloat(hourValue);
                if (!isNaN(value)) {
                    total += value;
                    count++;
                }
            });
        });
        
        // Возвращаем среднее значение или 0, если данных нет
        return count > 0 ? (total / count) : 0;
    };
    return (
        <VBoxPanel className={css.item}>
            <HBoxPanel gap={'10px'}>
                <span className='bold'>{year}</span>
                <span>{MONTH_NAMES[month]}</span>
                <span>AVG: <accent>{calculateAverage()}</accent> кВт*ч</span>
            </HBoxPanel>
            <HBoxPanel className={css.header} valign='center' halign='end'>
                <Edit onClick={onEdit}/>
                <Bin onClick={onDelete}/>
            </HBoxPanel>
        </VBoxPanel>
    );
};