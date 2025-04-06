import { useState } from "react";
import { PageBase } from "../../../../react-envelope/components/pages/base/PageBase/PageBase";
import { TNSTitle } from "../../../dummies/TNSTitle/TNSTitle";
import css from './database.module.css';
import { Headline } from "../../../../react-envelope/components/ui/labels/Headline/Headline";
import { SupplyCalendar } from "../../../widgets/SupplyCalendar/SupplyCalendar";
import { TextBox } from "../../../../react-envelope/components/ui/input/text/TextBox/TextBox";
import { MONTH_NAMES } from "../../../../constants";
import ExButton from "../../../../react-envelope/components/ui/buttons/ExButton/ExButton";
import HBoxPanel from "../../../../react-envelope/components/layouts/HBoxPanel/HBoxPanel";

export const HolidaysDatabasePage = () => {
    const [holidaysData, setHolidaysData] = useState({});
    const [month, setMonth] = useState(new Date().getMonth());
    const [year, setYear] = useState(new Date().getFullYear());

    return (
        <PageBase title={<TNSTitle />} contentClassName={css.content}>
            <Headline>База данных выходных</Headline>

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

            <SupplyCalendar year={year} month={month} dailyData={holidaysData} onChange={(day, holiday, hours) => {
                setHolidaysData(prev => {
                    return {
                        ...prev,
                        [day]: {
                            holiday: holiday,
                            // hours: hours
                        }
                    }
                })
            }} className='center-self' holidays />

            <ExButton className={'accent-button'} onClick={() => {

            }}>Отправить</ExButton>
        </PageBase>
    )
};