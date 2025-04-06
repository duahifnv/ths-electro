import { PageBase } from "../../../../react-envelope/components/pages/base/PageBase/PageBase";
import { ModeratorAccessCard } from "../../../dummies/ModeratorAccessCard/ModeratorAccessCard";
import { useState, useEffect } from "react";
import css from "./ModeratorsManagementPage.module.css";
import { Headline } from "../../../../react-envelope/components/ui/labels/Headline/Headline";
import ExButton from "../../../../react-envelope/components/ui/buttons/ExButton/ExButton";
import { TextBox } from "../../../../react-envelope/components/ui/input/text/TextBox/TextBox";
import { TNSTitle } from "../../../dummies/TNSTitle/TNSTitle";
import { 
    getHelper, 
    updateHelper, 
    deleteHelper, 
    getAllHelpers, 
    createHelper 
} from "../../../../api/helper";

export const ModeratorsManagementPage = () => {
    const [moderators, setModerators] = useState([]);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [newId, setNewId] = useState("");
    const [newLastName, setNewLastName] = useState("");
    const [newFirstName, setNewFirstName] = useState("");
    const [newMiddleName, setNewMiddleName] = useState("");
    const [editingModerator, setEditingModerator] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);

    // Загрузка списка модераторов при монтировании компонента
    useEffect(() => {
        const fetchModerators = async () => {
            setIsLoading(true);
            try {
                const response = await getAllHelpers();
                setModerators(response.data);
            } catch (err) {
                setError(err.message || "Ошибка при загрузке модераторов");
            } finally {
                setIsLoading(false);
            }
        };
        
        fetchModerators();
    }, []);

    const handleAddModerator = async () => {
        try {
            setIsLoading(true);
            const moderatorData = {
                tgId: newId,
                lastname: newLastName,
                firstname: newFirstName,
                ...(newMiddleName && { middlename: newMiddleName })
            };

            if (editingModerator) {
                // Редактирование существующего модератора
                await updateHelper(editingModerator.id, moderatorData);
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
                await createHelper(moderatorData);
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
        } catch (err) {
            setError(err.message || "Ошибка при сохранении модератора");
        } finally {
            setIsLoading(false);
        }
    };

    const handleSaveCard = async (id, data) => {
        try {
            setIsLoading(true);
            await updateHelper(id, {
                tgId: id,
                lastname: data.lastName,
                firstname: data.firstName,
                ...(data.middleName && { middlename: data.middleName })
            });
            
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
        } catch (err) {
            setError(err.message || "Ошибка при обновлении модератора");
        } finally {
            setIsLoading(false);
        }
    };

    const handleDeleteCard = async (id) => {
        try {
            setIsLoading(true);
            await deleteHelper(id);
            setModerators(moderators.filter((mod) => mod.id !== id));
        } catch (err) {
            setError(err.message || "Ошибка при удалении модератора");
        } finally {
            setIsLoading(false);
        }
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

    if (isLoading) return <div>Загрузка...</div>;
    if (error) return <div>Ошибка: {error}</div>;

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

                        <TextBox 
                            value={newId}
                            onChange={setNewId}
                            borderType={'fullr'}
                            label={'ID'}
                            placeholder={'Введите ID модератора'}
                            type="number"
                            disabled={!!editingModerator}
                        />

                        <TextBox 
                            value={newLastName}
                            onChange={setNewLastName}
                            borderType={'fullr'}
                            label={'Фамилия'}
                            placeholder={'Введите фамилию'} 
                        />

                        <TextBox 
                            value={newFirstName}
                            onChange={setNewFirstName}
                            borderType={'fullr'}
                            label={'Имя'}
                            placeholder={'Введите имя'} 
                        />

                        <TextBox 
                            value={newMiddleName}
                            onChange={setNewMiddleName}
                            borderType={'fullr'}
                            label={'Отчество'}
                            placeholder={'Введите отчество'} 
                        />

                        <div className={css.modalActions}>
                            <ExButton
                                onClick={handleAddModerator}
                                disabled={!newId || !newLastName || !newFirstName || isLoading}
                                className={'accent-button'}
                            >
                                {editingModerator ? "Сохранить" : "Добавить"}
                            </ExButton>
                            <ExButton
                                onClick={() => setIsModalOpen(false)}
                                type={'error'}
                                disabled={isLoading}
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