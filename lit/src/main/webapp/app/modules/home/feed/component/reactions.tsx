
import { IconButton, Tooltip, Button } from '@mui/material';
import { UserReactionType } from 'app/shared/model/enumerations/user-reaction-type.model';
import * as React from 'react';

export const ReactionsCounterList = [
    { label: "lit", node: <div>🔥</div>, by: '1' },
    { label: "love", node: <div>❤️</div>, by: ' 1' },
    { label: "amusing", node: <div>🤣</div>, by: '1' },
]
export const ReactionsTypes = [
    { label: "lit", node: <div>🔥</div> },
    { label: "love", node: <div>❤️</div> },
    { label: "amusing", node: <div>🤣</div> },
    { label: "amazing", node: <div>😮</div> },
    { label: "anger", node: <div>😠</div> },
    { label: "fear", node: <div>😱</div> },
    { label: "sad", node: <div>😔</div> },
    { label: "boring", node: <div>🥱</div> }
]

export const GiveEmoji = (props) => {
    const renderSwitch = () => {
        switch (props.emoji) {
            case "LIT": return (<Tooltip title="Your reaction"><IconButton color="primary">🔥</IconButton></Tooltip>);
            case "LOVE": return (<Tooltip title="Your reaction"><IconButton color="primary">❤️</IconButton></Tooltip>);
            case "AMUSING": return (<Tooltip title="Your reaction"><IconButton color="primary">🤣</IconButton></Tooltip>);
            case "AMAZING": return (<Tooltip title="Your reaction"><IconButton color="primary">😮</IconButton></Tooltip>);
            case "ANGER": return (<Tooltip title="Your reaction"><IconButton color="primary">😠</IconButton></Tooltip>);
            case "FEAR": return (<Tooltip title="Your reaction"><IconButton color="primary">😱</IconButton></Tooltip>);
            case "SAD": return (<Tooltip title="Your reaction"><IconButton color="primary">😔</IconButton></Tooltip>);
            case "BORING": return (<Tooltip title="Your reaction"><IconButton color="primary">🥱</IconButton></Tooltip>);
            default: <div></div>
        }
    }

    return (
        <div>
            {renderSwitch()}
        </div>
    );
}