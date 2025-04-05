import React, { useState, useCallback, useRef } from 'react';
import PropTypes from 'prop-types';
import css from './DragAndDrop.module.css';
import toast from 'react-hot-toast';
import { Bin } from '../../../react-envelope/components/dummies/Icons';
import HBoxPanel from '../../../react-envelope/components/layouts/HBoxPanel/HBoxPanel';
import VBoxPanel from '../../../react-envelope/components/layouts/VBoxPanel/VBoxPanel';

const DragAndDrop = ({
    acceptedFileTypes,
    maxFiles,
    onFilesChange
}) => {
    const [files, setFiles] = useState([]);
    const [isOver, setIsOver] = useState(false);
    const fileRef = useRef(0);

    const handleDragOver = useCallback((e) => {
        e.preventDefault();
        setIsOver(true);
    }, []);

    const handleDragLeave = useCallback(() => {
        setIsOver(false);
    }, []);

    const validateFiles = useCallback((fileList) => {
        const newFiles = Array.from(fileList);

        if (maxFiles && files.length + newFiles.length > maxFiles) {
            toast.error(`Достигнут максимум файлов: ${maxFiles}`);
            return false;
        }

        if (acceptedFileTypes && acceptedFileTypes.length > 0) {
            const invalidFiles = newFiles.filter(
                file => !acceptedFileTypes.some(type => file.type.includes(type))
            );

            if (invalidFiles.length > 0) {
                toast.error(`Неверный тип файла. Только типы ${acceptedFileTypes.join(', ')} разрешены.`);
                return false;
            }
        }

        return true;
    }, [acceptedFileTypes, files.length, maxFiles]);

    const handleDrop = useCallback((e) => {
        e.preventDefault();
        setIsOver(false);

        if (validateFiles(e.dataTransfer.files)) {
            const newFiles = Array.from(e.dataTransfer.files);
            const updatedFiles = [...files, ...newFiles];
            setFiles(updatedFiles);
            onFilesChange && onFilesChange(updatedFiles);
        }
    }, [files, onFilesChange, validateFiles]);

    const handleFileInput = useCallback((e) => {
        if (validateFiles(e.target.files)) {
            const newFiles = Array.from(e.target.files);
            const updatedFiles = [...files, ...newFiles];
            setFiles(updatedFiles);
            onFilesChange && onFilesChange(updatedFiles);
        }
        e.target.value = '';
    }, [files, onFilesChange, validateFiles]);

    const removeFile = useCallback((index) => {
        const newFiles = [...files];
        newFiles.splice(index, 1);
        setFiles(newFiles);
        onFilesChange && onFilesChange(newFiles);
    }, [files, onFilesChange]);

    const getFileIcon = (type) => {
        if (type.includes('spreadsheet') || type.includes('excel')) return '📊';
        return '📄';
    };

    const handleDropZoneClick = useCallback(() => {
        fileRef.current.click(); // Программно кликаем по input
    }, []);

    return (
        <div className={css.container}>
            <div
                className={css.dropZone}
                onDragOver={handleDragOver}
                onDragLeave={handleDragLeave}
                onDrop={handleDrop}
                onClick={handleDropZoneClick}
            >
                {files.length === 0 ? (
                    <div className={css.placeholder}>
                        {isOver ? 'Перетащите сюда файлы' : 'Перетащите сюда файлы или нажмите'}
                    </div>
                ) : (
                    <div className={css.fileList}>
                        {files.map((file, index) => (
                            <div
                                key={`${file.name}-${index}`}
                                className={css.fileItem}
                                onClick={() => removeFile(index)}
                            >
                                <span className={css.fileIcon}>{getFileIcon(file.type)}</span>
                                <VBoxPanel halign='start'>
                                    <span className={css.fileName}>{file.name}</span>
                                    <span className={css.fileSize}>
                                        {(file.size / 1024).toFixed(1)} KB
                                    </span>
                                </VBoxPanel>
                                <svg onClick={(e) => {
                                    removeFile(index);
                                    e.stopPropagation();
                                }} className={`${css.delete} pointer icon-s`} viewBox="0 0 24 24">
                                    <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" />
                                </svg>
                            </div>
                        ))}
                    </div>
                )}

                <input
                    type="file"
                    id="fileInput"
                    className={css.fileInput}
                    onChange={handleFileInput}
                    multiple
                    ref={fileRef}
                    accept={acceptedFileTypes?.join(',')}
                />
                <label htmlFor="fileInput" className={css.fileInputLabel}>Выбрать файлы</label>
            </div>

            {acceptedFileTypes && (
                <div className={css.fileTypesInfo}>
                    Allowed types: {acceptedFileTypes.join(', ')}
                </div>
            )}
        </div>
    );
};

DragAndDrop.propTypes = {
    acceptedFileTypes: PropTypes.arrayOf(PropTypes.string),
    maxFiles: PropTypes.number,
    onFilesChange: PropTypes.func,
    darkMode: PropTypes.bool,
};

DragAndDrop.defaultProps = {
    acceptedFileTypes: null,
    maxFiles: null,
    onFilesChange: null,
    darkMode: false,
};

export default DragAndDrop;