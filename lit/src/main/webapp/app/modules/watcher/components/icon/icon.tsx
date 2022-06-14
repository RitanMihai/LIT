import React from 'react';
import { Translate } from 'react-jhipster';

import { NavItem, NavLink, NavbarBrand, Row, Col } from 'reactstrap';
import { NavLink as Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import "./icon.scss"

export const DefaultIcon = props => (
    <div {...props} >
        <img className="icon" src="content/images/logo/fire.png" alt="default" />
    </div>
);