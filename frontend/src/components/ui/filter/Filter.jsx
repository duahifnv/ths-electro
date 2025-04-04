import { useState } from 'react';
import ExTextBox from '../../../react-envelope/components/ui/input/text/ExTextBox/ExTextBox';
import ExCheckbox from '../checkbox/ExCheckbox';
import { PriceRangeSlider } from '../sliders/PriceRangeSlider/PriceRangeSlider';


const Filter = ({ filterConfigs, onChange }) => {
    const handleFilterChange = (key, value) => {
        onChange(key, value);
    };

    return (
        <div>
            {filterConfigs.map((filterConfig, index) => {
                const { type, label, key, value, options } = filterConfig;

                switch (type) {
                    case 'text':
                        return (
                            <div key={index}>
                                {label && <label>{label}</label>}
                                <ExTextBox
                                    text={value}
                                    textChanged={(newValue) => handleFilterChange(key, newValue)}
                                />
                            </div>
                        );

                    case 'checkbox':
                        return (
                            <div key={index}>
                                {label && <label>{label}</label>}
                                <ExCheckbox
                                    statesCount={3}
                                    onChange={(newState) => handleFilterChange(key, newState === 'checked')}
                                />
                            </div>
                        );

                    case 'range':
                        return (
                            <div key={index}>
                                {label && <label>{label}</label>}
                                <PriceRangeSlider
                                    min={options?.min || 0}
                                    max={options?.max || 100000}
                                    onChange={(newRange) => handleFilterChange(key, newRange)}
                                />
                            </div>
                        );

                    default:
                        return null;
                }
            })}
        </div>
    );
};

export default Filter;
