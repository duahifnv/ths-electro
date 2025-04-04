import { useState, useEffect, useRef } from 'react';
import css from './Slider.module.css'
import { ArrowLeftSquare, ArrowRightSquare } from '../../../dummies/Icons';

export const Slider = ({ min, max, minValue, maxValue, setMinValue, setMaxValue, enableMinThumb, enableMaxThumb }) => {
    const [activeThumb, setActiveThumb] = useState(null);
    const sliderRef = useRef(null);

    const handleThumbMouseDown = (thumb) => {
        setActiveThumb(thumb);
    };

    const handleMouseMove = (e) => {
        if (!activeThumb || !sliderRef.current) return;

        const rect = sliderRef.current.getBoundingClientRect();
        const position = Math.min(1, Math.max(0, (e.clientX - rect.left) / rect.width));
        const newValue = Math.round(min + position * (max - min));

        if (activeThumb === 'min' && enableMinThumb) {
            setMinValue(Math.min(newValue, maxValue - 1));
        } else if (activeThumb === 'max' && enableMaxThumb) {
            setMaxValue(Math.max(newValue, minValue + 1));
        }
    };

    const handleMouseUp = () => {
        setActiveThumb(null);
    };

    useEffect(() => {
        if (activeThumb) {
            document.addEventListener('mousemove', handleMouseMove);
            document.addEventListener('mouseup', handleMouseUp);
        } else {
            document.removeEventListener('mousemove', handleMouseMove);
            document.removeEventListener('mouseup', handleMouseUp);
        }
        return () => {
            document.removeEventListener('mousemove', handleMouseMove);
            document.removeEventListener('mouseup', handleMouseUp);
        };
    }, [activeThumb]);

    const minPercent = ((minValue - min) / (max - min)) * 100;
    const maxPercent = ((maxValue - min) / (max - min)) * 100;

    return (
        <div ref={sliderRef} className={css.slider}>
            <div className={css.sliderTrack} />
            <div className={css.sliderRange} style={{ left: `${minPercent}%`, width: `${maxPercent - minPercent}%` }} />
            {enableMinThumb && (
                <div
                    className={css.thumb}
                    style={{ left: `${minPercent}%` }}
                    onMouseDown={() => handleThumbMouseDown('min')}
                >
                    <ArrowLeftSquare className={css.thumbIcon} />
                </div>
            )}
            {enableMaxThumb && (
                <div
                    className={css.thumb}
                    style={{ left: `${maxPercent}%` }}
                    onMouseDown={() => handleThumbMouseDown('max')}
                >
                    <ArrowRightSquare className={css.thumbIcon} />
                </div>
            )}
        </div>
    );
};