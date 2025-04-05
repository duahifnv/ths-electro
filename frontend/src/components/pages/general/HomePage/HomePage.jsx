import { PageBase } from "../../../../react-envelope/components/pages/base/PageBase/PageBase";
import ChatWidget from "../../../ui/ChatWidget/ChatWidget";

export const HomePage = () => {
    return (
        <PageBase>
            <ChatWidget clientId={1} />
        </PageBase>
    );
};