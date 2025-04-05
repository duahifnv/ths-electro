import { PageBase } from "../../../../react-envelope/components/pages/base/PageBase/PageBase";
import { ModeratorAccessCard } from "../../../dummies/ModeratorAccessCard/ModeratorAccessCard";
import { useState } from "react";
import css from "./ModeratorsManagementPage.module.css";
import { Headline } from "../../../../react-envelope/components/ui/labels/Headline/Headline";
import ExButton from "../../../../react-envelope/components/ui/buttons/ExButton/ExButton";
import { TextBox } from "../../../../react-envelope/components/ui/input/text/TextBox/TextBox";
import { TNSTitle } from "../../../dummies/TNSTitle/TNSTitle";

export const ModeratorsManagementPage = () => {
    const [moderators, setModerators] = useState([]);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [newId, setNewId] = useState("");
    const [newLastName, setNewLastName] = useState("");
    const [newFirstName, setNewFirstName] = useState("");
    const [newMiddleName, setNewMiddleName] = useState("");
    const [editingModerator, setEditingModerator] = useState(null);

    const handleAddModerator = () => {
        if (editingModerator) {
            // Редактирование существующего модератора
            setModerators(
                moderators.map((mod) =>
                    mod.id === editingModerator.id
                        ? {
                            ...mod,
                            lastName: newLastName,
                            firstName: newFirstName,
                            middleName: newMiddleName,
                        }
                        : mod
                )
            );
        } else {
            // Добавление нового модератора
            setModerators([
                ...moderators,
                {
                    id: newId,
                    lastName: newLastName,
                    firstName: newFirstName,
                    middleName: newMiddleName,
                },
            ]);
        }
        setNewId("");
        setNewLastName("");
        setNewFirstName("");
        setNewMiddleName("");
        setEditingModerator(null);
        setIsModalOpen(false);
    };

    const handleSaveCard = (
        id, data
    ) => {
        setModerators(
            moderators.map((mod) =>
                mod.id === id
                    ? {
                        ...mod,
                        lastName: data.lastName,
                        firstName: data.firstName,
                        middleName: data.middleName,
                    }
                    : mod
            )
        );
    };

    const handleDeleteCard = (id) => {
        setModerators(moderators.filter((mod) => mod.id !== id));
    };

    const handleEditClick = (id) => {
        const moderator = moderators.find(mod => mod.id === id);
        if (moderator) {
            setEditingModerator(moderator);
            setNewId(moderator.id);
            setNewLastName(moderator.lastName);
            setNewFirstName(moderator.firstName);
            setNewMiddleName(moderator.middleName);
            setIsModalOpen(true);
        }
    };

    return (
        <PageBase title={<TNSTitle/>}>
            <Headline>Управление модераторами</Headline>

            <ExButton
                onClick={() => {
                    setEditingModerator(null);
                    setNewId("");
                    setNewLastName("");
                    setNewFirstName("");
                    setNewMiddleName("");
                    setIsModalOpen(true);
                }}
                className={`accent-button start-self`}
            >
                Добавить модератора
            </ExButton>

            <div className={css.cardsContainer}>
                {moderators.map((moderator) => (
                    <ModeratorAccessCard
                        key={moderator.id}
                        id={moderator.id}
                        lastName={moderator.lastName}
                        firstName={moderator.firstName}
                        middleName={moderator.middleName}
                        onSave={handleSaveCard}
                        onDelete={handleDeleteCard}
                        onEdit={handleEditClick}
                    />
                ))}
            </div>

            {isModalOpen && (
                <div className={css.modalOverlay}>
                    <div className={`${css.modal} flex col g20`}>
                        <span className={`${css.title} bold`}>{editingModerator ? "Редактировать модератора" : "Добавить модератора"} </span>

                        <TextBox value={newId}
                            onChange={setNewId}
                            borderType={'fullr'}
                            label={'ID'}
                            placeholder={'Введите ID модератора'}
                            type="number" />

                        <TextBox value={newLastName}
                            onChange={setNewLastName}
                            borderType={'fullr'}
                            label={'Фамилия'}
                            placeholder={'Введите фамилию'} />

                        <TextBox value={newFirstName}
                            onChange={setNewFirstName}
                            borderType={'fullr'}
                            label={'Имя'}
                            placeholder={'Введите имя'} />

                        <TextBox value={newMiddleName}
                            onChange={setNewMiddleName}
                            borderType={'fullr'}
                            label={'Отчество'}
                            placeholder={'Введите отчество'} />

                        <div className={css.modalActions}>
                            <ExButton
                                onClick={handleAddModerator}
                                disabled={!newId || !newLastName || !newFirstName}
                                className={'accent-button'}
                            >
                                {editingModerator ? "Сохранить" : "Добавить"}
                            </ExButton>
                            <ExButton
                                onClick={() => setIsModalOpen(false)}
                                type={'error'}
                            >
                                Отмена
                            </ExButton>
                        </div>
                    </div>
                </div>
            )}
        </PageBase>
    );
};