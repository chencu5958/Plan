import React, {useEffect, useState} from 'react';
import {Transition} from 'react-transition-group';

const reduceAnimations = window.matchMedia(`(prefers-reduced-motion: reduce)`) === true;
const defaultDuration = 250;

const LoadIn = ({children, duration}) => {
    if (!duration) duration = defaultDuration;

    const defaultStyle = reduceAnimations ? {
        transition: `opacity ${duration}ms ease-in-out`,
        opacity: 0
    } : {
        transition: `opacity ${duration}ms ease-in-out, transform ${duration}ms ease-in-out`,
        opacity: 0,
        transform: "scale(0.99)"
    }

    const transitionStyles = reduceAnimations ? {
        entering: {
            opacity: 1,
        },
        entered: {
            opacity: 1,
        },
        exited: {
            opacity: 0,
        },
        exiting: {
            opacity: 0,
        }
    } : {
        entering: {
            opacity: 1,
            transform: "scale(1)"
        },
        entered: {
            opacity: 1,
            transform: "scale(1)"
        },
        exiting: {
            opacity: 0,
            transform: "scale(0.99)"
        },
        exited: {
            opacity: 0,
            transform: "scale(0.99)"
        },
    };

    const [visible, setVisible] = useState(false);

    useEffect(() => {
        setTimeout(() => setVisible(true), 0);
        return () => {
            setVisible(false);
        }
    }, [setVisible])

    return (
        <Transition in={visible} timeout={duration}>
            {state => (
                <div style={{
                    ...defaultStyle,
                    ...transitionStyles[state]
                }}>
                    {children}
                </div>
            )}
        </Transition>
    );
}

export default LoadIn