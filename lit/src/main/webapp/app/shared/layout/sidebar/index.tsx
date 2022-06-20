import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import { Home, Brand, Portfolio, Watcher } from '../../../shared/layout/header/header-components';

const Routes = ({ match }) => (
    <>
        <Switch>
            <ErrorBoundaryRoute exact path={`${match.url}/portfolios-manager`} component={Portfolio} />
            <ErrorBoundaryRoute exact path={`${match.url}/watcher`} component={Watcher} />
        </Switch>
    </>
);

export default Routes;
