import css from './TNSTitle.module.css';
import tns from '../../../assets/tns.png';
import HBoxPanel from '../../../react-envelope/components/layouts/HBoxPanel/HBoxPanel';
import { useNavigate } from 'react-router-dom';

export const TNSTitle = () => {
    const navigate = useNavigate();
    return (
        <HBoxPanel onClick={() => navigate('/')} className={`${css.title} pointer`} valign='center' halign='center'>
            <img src={tns} alt="Logo"/>
            <span className='bold'>ТНС ЭНЕРГО</span>
        </HBoxPanel>
    );
};