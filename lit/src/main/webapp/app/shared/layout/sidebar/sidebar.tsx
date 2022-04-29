import React, { useState } from 'react';
import './sidebar.scss';

import { Translate, Storage } from 'react-jhipster';
import { Navbar, Nav, NavbarToggler, Collapse, Form, FormGroup, Button, Input, InputGroup, Row, Col } from 'reactstrap';
import LoadingBar from 'react-redux-loading-bar';

import { AdminMenu, EntitiesMenu, AccountMenu, LocaleMenu } from '../menus';
import { useAppDispatch } from 'app/config/store';
import { setLocale } from 'app/shared/reducers/locale';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Home, Brand, Portfolio } from '../header/header-components';

export interface IHeaderProps {
    isAuthenticated: boolean;
    isAdmin: boolean;
    ribbonEnv: string;
    isInProduction: boolean;
    isOpenAPIEnabled: boolean;
    currentLocale: string;
  }
  
const SideBar =(props: IHeaderProps) => {
    const [menuOpen, setMenuOpen] = useState(false);

    const dispatch = useAppDispatch();
  
    const handleLocaleChange = event => {
      const langKey = event.target.value;
      Storage.session.set('locale', langKey);
      dispatch(setLocale(langKey));
    };
  
    const renderDevRibbon = () =>
      props.isInProduction === false ? (
        <div className="ribbon dev">
          <a href="">
            <Translate contentKey={`global.ribbon.${props.ribbonEnv}`} />
          </a>
        </div>
      ) : null;
  
    const toggleMenu = () => setMenuOpen(!menuOpen);
  
    /* jhipster-needle-add-element-to-menu - JHipster will add new menu items here */

return (
    <div className='sidebar'>
        <LoadingBar className="loading-bar" />
        <Navbar data-cy="navbar" dark>
            <Nav id="header-tabs" className="ms-auto" navbar>
                <Home/>
                <Portfolio/>
            </Nav>
        </Navbar>
    </div>
);
}

export default SideBar;