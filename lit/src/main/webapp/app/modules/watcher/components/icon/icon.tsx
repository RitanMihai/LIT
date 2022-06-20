import React from 'react';
import { Translate } from 'react-jhipster';

import { NavItem, NavLink, NavbarBrand, Row, Col } from 'reactstrap';
import { NavLink as Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import "./icon.scss"
import { Avatar } from '@mui/material';

export const DefaultIcon = ({ letter }) => (
    <Avatar sx={{
        bgcolor: 'orange',
        marginRight: '15px',
        width: '50px',
        height: ' 50px',
        borderRadius: '20%',
    }} aria-label="recipe">
        {letter}
    </Avatar>
);