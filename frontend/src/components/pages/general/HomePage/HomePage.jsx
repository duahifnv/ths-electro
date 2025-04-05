import { PageBase } from "../../../../react-envelope/components/pages/base/PageBase/PageBase";
import ChatWidget from "../../../ui/ChatWidget/ChatWidget";
import { Headline } from "../../../../react-envelope/components/ui/labels/Headline/Headline";
import { TNSTitle } from "../../../dummies/TNSTitle/TNSTitle";

export const HomePage = () => {
    return (
        <PageBase title={<TNSTitle/>}>
            <ChatWidget/>
            <Headline>Доступные тарифы</Headline>
        </PageBase>
    );
};