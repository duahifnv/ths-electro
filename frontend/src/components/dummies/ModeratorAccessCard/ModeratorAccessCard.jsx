import React, { useState } from 'react';
import css from './ModeratorAccessCard.module.css';
import HDivider from '../../../react-envelope/components/ui/dividers/HDivider/HDivider';

export const ModeratorAccessCard = ({
    id,
    lastName,
    firstName,
    middleName,
    onSave,
    onDelete,
    onEdit,
}) => {
    const [isEditing, setIsEditing] = useState(false);
    const [editedLastName, setEditedLastName] = useState(lastName);
    const [editedFirstName, setEditedFirstName] = useState(firstName);
    const [editedMiddleName, setEditedMiddleName] = useState(middleName);

    const handleSave = () => {
        onSave(id, {
            lastName: editedLastName,
            firstName: editedFirstName,
            middleName: editedMiddleName,
        });
        setIsEditing(false);
    };

    const handleCancel = () => {
        setEditedLastName(lastName);
        setEditedFirstName(firstName);
        setEditedMiddleName(middleName);
        setIsEditing(false);
    };

    return (
        <div className={`${css.card} ${isEditing ? css.editing : ''}`}>
            <div className={css.cardHeader}>
                <button
                    className={css.editButton}
                    onClick={() => onEdit(id)}
                    title="Редактировать"
                >
                    <svg className={css.icon} viewBox="0 0 24 24">
                        <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" />
                    </svg>
                </button>
                <button
                    className={css.deleteButton}
                    onClick={() => onDelete(id)}
                    title="Удалить"
                >
                    <svg className={css.icon} viewBox="0 0 24 24">
                        <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" />
                    </svg>
                </button>
            </div>

            <div className={`${css.cardContent} flex col g5`}>
                <div className={css.field}>
                    <span className={css.label}>ID</span>
                    <span className={css.value}>{id}</span>
                </div>
                <HDivider/>
                <div className={css.field}>
                    <span className={css.label}>Фамилия</span>
                    <span className={css.value}>{lastName}</span>
                </div>
                <div className={css.field}>
                    <span className={css.label}>Имя</span>
                    <span className={css.value}>{firstName}</span>
                </div>
                <div className={css.field}>
                    <span className={css.label}>Отчество</span>
                    <span className={css.value}>{middleName}</span>
                </div>
            </div>
        </div>
    );
};