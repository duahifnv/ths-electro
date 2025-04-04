import css from './UserSettingsPage.module.css';
import { UserGeneralPanel } from '../../../../react-envelope/components/widgets/user/UserGeneralPanel/UserGeneralPanel';
import { PageBase } from '../../../../react-envelope/components/pages/base/PageBase/PageBase';
import { IconHeader } from '../../../../react-envelope/components/pages/base/IconHeader';
import { Settings } from '../../../../react-envelope/components/dummies/Icons';

export const UserSettingsPage = () => {
    return (
        <PageBase header={<IconHeader text={'Настройки'} icon={<Settings/>}/>}>
            <h1>Основная информация</h1>
            <UserGeneralPanel edit/>
        </PageBase>
    );
};