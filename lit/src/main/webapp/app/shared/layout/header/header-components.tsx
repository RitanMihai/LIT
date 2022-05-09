import React from 'react';
import { Translate } from 'react-jhipster';

import { NavItem, NavLink, NavbarBrand, Row, Col } from 'reactstrap';
import { NavLink as Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

const paddingTop = '20px';

export const BrandIcon = props => (
  <div {...props} className="brand-icon">
    <img src="content/images/logo/fire.png" alt="Logo" />
  </div>
);

export const Brand = () => (
  <NavLink tag={Link} to="/" className="brand-logo text-center">
    <BrandIcon />
    <span className="brand-title">
      <Translate contentKey="global.title">Lit</Translate>
    </span>
  </NavLink>
);

export const Home = () => (
  <NavItem>
    <NavLink tag={Link} to="/" className="text-center" style = {{paddingTop}}>
          <FontAwesomeIcon icon="home" size="2x"/>  
          <Translate contentKey="global.menu.home">Home</Translate>
    </NavLink>
  </NavItem>
);

export const Portfolio = () => (
  <NavItem>
    <NavLink tag={Link} to="/portfolio" className="text-center" style = {{paddingTop}}>
          <FontAwesomeIcon icon="wallet" size="2x" />
          <Translate contentKey="global.menu.entities.portfolioPortfolio">Portfolio</Translate>
    </NavLink>
  </NavItem>
);

export const Watcher = () => (
  <NavItem>
    <NavLink tag={Link} to="/portfolio" className="text-center" style = {{paddingTop}}>
          <FontAwesomeIcon icon="compass" size="2x" />
          Watcher
    </NavLink>
  </NavItem>
);

