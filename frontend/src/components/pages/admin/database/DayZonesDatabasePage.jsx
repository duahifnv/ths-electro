import { useEffect, useState } from "react";
import { PageBase } from "../../../../react-envelope/components/pages/base/PageBase/PageBase";
import css from './database.module.css';
import { Headline } from "../../../../react-envelope/components/ui/labels/Headline/Headline";
import { TNSTitle } from "../../../dummies/TNSTitle/TNSTitle";
import { TextBox } from "../../../../react-envelope/components/ui/input/text/TextBox/TextBox";
import { MONTH_NAMES } from "../../../../constants";
import StateToggleButton from "../../../../react-envelope/components/ui/buttons/StateToggleButton/StateToggleButton";
import { Moon, Sun, Sunrise } from "../../../../react-envelope/components/dummies/Icons";
import { Pair } from "../../../../react-envelope/components/layouts/Pair/Pair";
import ExButton from "../../../../react-envelope/components/ui/buttons/ExButton/ExButton";
import HBoxPanel from "../../../../react-envelope/components/layouts/HBoxPanel/HBoxPanel";
import VBoxPanel from "../../../../react-envelope/components/layouts/VBoxPanel/VBoxPanel";

export const DayZonesDatabasePage = () => {
    const [month, setMonth] = useState(new Date().getMonth());
    const [dayzones, setDayzones] = useState({});

    useEffect(() => {
        for (var i = 0; i < 24; i++) {
            setDayzones(prev => ({
                ...prev,
                [i]: '0'
            }));
        }
    }, []);

    return (
        <PageBase title={<TNSTitle />} contentClassName={css.content}>
            <Headline>База данных дневных зон</Headline>

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

            <HBoxPanel halign="center" className={'rel'}>
                <VBoxPanel className={`center-self abs ${css.legend}`}>
                    <Pair left={<Moon className='icon-m'/>} right={<span>Ночь</span>}/>
                    <Pair left={<Sunrise className='icon-m'/>} right={<span>Полупик</span>}/>
                    <Pair left={<Sun className='icon-m'/>} right={<span>Пик</span>}/>
                </VBoxPanel>

                <div className={`${css.dayzones}`}>
                    {Array.from({ length: 24 }, (_, i) => i + 1).map(hour => (
                        <div className={css.dayzoneWrapper}>
                            <StateToggleButton states={[
                                { id: '0', display: <Moon className={css.zoneLevel1}/> },
                                { id: '1', display: <Sunrise className={css.zoneLevel2}/> },
                                { id: '2', display: <Sun className={css.zoneLevel3}/> },
                            ]} key={hour} className={css.dayzone} onChange={(id) => setDayzones({
                                ...dayzones,
                                [hour]: id
                            })}/>
                            <span className={css.dayzoneLabel}>{hour}</span>
                        </div>
                    ))}
                </div>
            </HBoxPanel>

            <ExButton className={'accent-button'} onClick={() => {
                
            }}>Отправить</ExButton>
        </PageBase>
    );
};