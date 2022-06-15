import * as React from 'react';
import "./feed.scss"


export default function RecipeReviewCard() {
    const [expanded, setExpanded] = React.useState(false);

    const handleExpandClick = () => {
        setExpanded(!expanded);
    };
}

export const LitIcon = props => (
    <img className="reaction_icon" src="content/images/logo/fire.png" alt="Logo" />
);

