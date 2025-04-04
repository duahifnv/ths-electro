import { useState } from 'react';
import css from './PriceRangeSlider.module.css';
import ExTextBox from '../../../../react-envelope/components/ui/input/text/ExTextBox/ExTextBox';
import ExButton from '../../../../react-envelope/components/ui/buttons/ExButton/ExButton';
import { Slider } from '../Slider/Slider';

export const PriceRangeSlider = ({
    min = 0,
    max = 100000,
    onChange,
    enableMinThumb = true,
    enableMaxThumb = true,
}) => {
    const [minValue, setMinValue] = useState(min);
    const [maxValue, setMaxValue] = useState(max);

    const handleInputChange = (value, type) => {
        const numValue = parseInt(value) || min;
        type === 'min' ? setMinValue(Math.max(min, Math.min(numValue, maxValue - 1))) : setMaxValue(Math.min(max, Math.max(numValue, minValue + 1)));
    };

    const handleApply = () => {
        onChange?.({ min: minValue, max: maxValue });
    };

    const handleReset = () => {
        setMinValue(min);
        setMaxValue(max);
    };

    return (
        <div className={css.container}>
            <div className={css.header}>Price Filter</div>
            <div className={css.rangeInputs}>
                {enableMinThumb && (
                    <div className={css.inputGroup}>
                        <label>From:</label>
                        <ExTextBox
                            text={minValue.toString()}
                            textChanged={(value) => handleInputChange(value, 'min')}
                            borderless
                            className={css.input}
                        />
                    </div>
                )}
                {enableMaxThumb && (
                    <div className={css.inputGroup}>
                        <label>To:</label>
                        <ExTextBox
                            text={maxValue.toString()}
                            textChanged={(value) => handleInputChange(value, 'max')}
                            borderless
                            className={css.input}
                        />
                    </div>
                )}
            </div>
            <Slider min={min} max={max} minValue={minValue} maxValue={maxValue} setMinValue={setMinValue} setMaxValue={setMaxValue} enableMinThumb={enableMinThumb} enableMaxThumb={enableMaxThumb} />
            <div className={css.actions}>
                <ExButton onClick={handleReset} type="info">Cancel</ExButton>
                <ExButton onClick={handleApply} type="success">Apply</ExButton>
            </div>
        </div>
    );
};

