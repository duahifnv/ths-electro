import { useState, useEffect } from 'react';
import css from './ExRadio.module.css';
import { RadioSelectedIcon, RadioUnselectedIcon } from '../../dummies/Icons';

export const ExRadio = ({
    className,
    type = 'info',
    selected = false,
    onChange
}) => {
    const [isSelected, setIsSelected] = useState(selected);
    const [isMounted, setIsMounted] = useState(false);
    const [isAnimating, setIsAnimating] = useState(false);

    useEffect(() => {
        setIsMounted(true);
    }, []);

    useEffect(() => {
        setIsSelected(selected);
    }, [selected]);

    const styles = {
        'info': css.info,
        'success': css.success,
        'warning': css.warning,
        'error': css.error,
        'tip': css.tip
    };

    const handleClick = () => {
        setIsAnimating(true);
        const newState = !isSelected;
        setIsSelected(newState);
        onChange && onChange(newState);

        setTimeout(() => setIsAnimating(false), 200);
    };

    const iconProps = {
        className: `${css.icon} ${isAnimating ? css.animating : ''}`,
        fill: type === 'info' ? 'var(--accent-font-color)' : 'currentColor'
    };

    return (
        <div
            className={`${className} ${css.radio} ${styles[type]} ${isAnimating ? css.animating : ''} ${isMounted ? css.mounted : ''}`}
            onClick={handleClick}
        >
            {isSelected ? <RadioSelectedIcon {...iconProps} /> : <RadioUnselectedIcon {...iconProps} />}
        </div>
    );
}

export default ExRadio;