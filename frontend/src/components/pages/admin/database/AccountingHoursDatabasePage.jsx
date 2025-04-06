import { PageBase } from '../../../../react-envelope/components/pages/base/PageBase/PageBase';
import { Headline } from '../../../../react-envelope/components/ui/labels/Headline/Headline';
import { TNSTitle } from '../../../dummies/TNSTitle/TNSTitle';
import css from './database.module.css';

export const AccountingHoursDatabasePage = () => {
    return (
        <PageBase title={<TNSTitle />} contentClassName={css.content}>
            <Headline>База данных отчетных часов</Headline>

            
        </PageBase>
    );
};