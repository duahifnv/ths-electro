import { useState } from "react";
import VBoxPanel from "../../../../react-envelope/components/layouts/VBoxPanel/VBoxPanel";
import { PageBase } from "../../../../react-envelope/components/pages/base/PageBase/PageBase";
import { Headline } from "../../../../react-envelope/components/ui/labels/Headline/Headline";
import css from './TariffPage.module.css';

export const TariffPage = () => {
    return (
        <PageBase>
            <Headline>Исходные данные</Headline>
            <p>Внесите Ваши данные, чтобы алгоритм мог расчитать рекомендуемые тарифные планы.</p>

            <VBoxPanel className={css.content} gap={'10px'}>
                
            </VBoxPanel>
        </PageBase>
    );
};