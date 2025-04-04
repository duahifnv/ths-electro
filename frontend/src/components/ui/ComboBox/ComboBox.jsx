import React, { useState } from 'react';

function ComboBox({ options = [] }) {
  const [inputValue, setInputValue] = useState('');

  const filteredOptions = options.filter((option) =>
    option.toLowerCase().includes(inputValue.toLowerCase())
  );

  return (
    <div
      style={{
        position: 'relative',
        width: '300px',
      }}
    >
      <input
        type="text"
        value={inputValue}
        onChange={(e) => setInputValue(e.target.value)}
        placeholder="Type or select..."
        style={{
          width: '100%',
          padding: '8px',
          boxSizing: 'border-box',
          border: '1px solid #ccc',
          borderRadius: '4px',
        }}
      />

      {filteredOptions.length > 0 && (
        <div
          style={{
            position: 'absolute',
            top: '100%',
            left: 0,
            right: 0,
            zIndex: 10,
            marginTop: '5px',
            maxHeight: '100px',
            overflowY: 'auto',
            border: '1px solid #eee',
            borderRadius: '4px',
            backgroundColor: '#fff',
            boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
          }}
        >
          <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
            {filteredOptions.map((option, index) => (
              <li
                key={index}
                onClick={() => setInputValue(option)}
                style={{
                  padding: '8px',
                  cursor: 'pointer',
                  backgroundColor: '#f9f9f9',
                  borderBottom: index < filteredOptions.length - 1 ? '1px solid #eee' : 'none',
                }}
              >
                {option}
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}

export default ComboBox;