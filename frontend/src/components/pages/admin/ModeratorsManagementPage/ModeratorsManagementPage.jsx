import { PageBase } from "../../../../react-envelope/components/pages/base/PageBase/PageBase";
import { ModeratorAccessCard } from "../../../dummies/ModeratorAccessCard/ModeratorAccessCard";
import { useState } from "react";
import css from "./ModeratorsManagementPage.module.css";
import { Headline } from "../../../../react-envelope/components/ui/labels/Headline/Headline";

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
        <PageBase>
            <Headline>Управление модераторами</Headline>
            <div className={css.container}>

                <button
                    onClick={() => {
                        setEditingModerator(null);
                        setNewId("");
                        setNewLastName("");
                        setNewFirstName("");
                        setNewMiddleName("");
                        setIsModalOpen(true);
                    }}
                    className={css.addButton}
                >
                    Добавить модератора
                </button>

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
                        <div className={css.modal}>
                            <h2>
                                {editingModerator ? "Редактировать модератора" : "Добавить модератора"}
                            </h2>

                            <div className={css.modalField}>
                                <label>ID:</label>
                                <input
                                    type="text"
                                    value={newId}
                                    onChange={(e) => setNewId(e.target.value)}
                                    disabled={!!editingModerator}
                                />
                            </div>

                            <div className={css.modalField}>
                                <label>Фамилия:</label>
                                <input
                                    type="text"
                                    value={newLastName}
                                    onChange={(e) => setNewLastName(e.target.value)}
                                />
                            </div>

                            <div className={css.modalField}>
                                <label>Имя:</label>
                                <input
                                    type="text"
                                    value={newFirstName}
                                    onChange={(e) => setNewFirstName(e.target.value)}
                                />
                            </div>

                            <div className={css.modalField}>
                                <label>Отчество:</label>
                                <input
                                    type="text"
                                    value={newMiddleName}
                                    onChange={(e) => setNewMiddleName(e.target.value)}
                                />
                            </div>

                            <div className={css.modalActions}>
                                <button
                                    onClick={handleAddModerator}
                                    disabled={!newId || !newLastName || !newFirstName}
                                    className={css.modalSaveButton}
                                >
                                    {editingModerator ? "Сохранить" : "Добавить"}
                                </button>
                                <button
                                    onClick={() => setIsModalOpen(false)}
                                    className={css.modalCancelButton}
                                >
                                    Отмена
                                </button>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </PageBase>
    );
};