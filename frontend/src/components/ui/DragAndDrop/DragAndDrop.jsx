import React, { useState, useCallback } from 'react';
import PropTypes from 'prop-types';
import css from './DragAndDrop.module.css';

const DragAndDrop = ({
  acceptedFileTypes,
  maxFiles,
  onFilesChange,
  darkMode = false
}) => {
  const [files, setFiles] = useState([]);
  const [isOver, setIsOver] = useState(false);
  const [error, setError] = useState(null);

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
      setError(`Maximum files: ${maxFiles}`);
      return false;
    }

    if (acceptedFileTypes && acceptedFileTypes.length > 0) {
      const invalidFiles = newFiles.filter(
        file => !acceptedFileTypes.some(type => file.type.includes(type))
      );

      if (invalidFiles.length > 0) {
        setError(`Invalid file type. Only ${acceptedFileTypes.join(', ')} allowed`);
        return false;
      }
    }

    setError(null);
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
    setError(null);
  }, [files, onFilesChange]);

  const getFileIcon = (type) => {
    if (type.includes('spreadsheet') || type.includes('excel')) return '📊';
    return '📄';
  };

  const containerClass = `${css.container} ${darkMode ? css.dark : ''}`;
  const dropZoneClass = `${css.dropZone} ${isOver ? css.dropZoneOver : ''} ${darkMode ? css.darkDropZone : ''}`;
  const fileItemClass = `${css.fileItem} ${darkMode ? css.darkFileItem : ''}`;
  const errorClass = `${css.errorMessage} ${darkMode ? css.darkError : ''}`;

  return (
    <div className={containerClass}>
      <div
        className={dropZoneClass}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
      >
        {files.length === 0 ? (
          <div className={css.placeholder}>
            {isOver ? 'Drop files here' : 'Drag files or click to select'}
          </div>
        ) : (
          <div className={css.fileList}>
            {files.map((file, index) => (
              <div
                key={`${file.name}-${index}`}
                className={fileItemClass}
                onClick={() => removeFile(index)}
              >
                <span className={css.fileIcon}>{getFileIcon(file.type)}</span>
                <span className={css.fileName}>{file.name}</span>
                <span className={css.fileSize}>
                  {(file.size / 1024).toFixed(1)} KB
                </span>
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
          accept={acceptedFileTypes?.join(',')}
        />
        <label htmlFor="fileInput" className={css.fileInputLabel}>
          Select Files
        </label>
      </div>

      {error && <div className={errorClass}>{error}</div>}

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