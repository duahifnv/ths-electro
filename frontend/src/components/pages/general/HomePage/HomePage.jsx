import { PageBase } from "../../../../react-envelope/components/pages/base/PageBase/PageBase";
import ChatWidget from "../../../ui/ChatWidget/ChatWidget";
import { Headline } from "../../../../react-envelope/components/ui/labels/Headline/Headline";

export const HomePage = () => {
    return (
        <PageBase>
            <ChatWidget/>
            <Headline>Доступные тарифы</Headline>
        </PageBase>
    );
};