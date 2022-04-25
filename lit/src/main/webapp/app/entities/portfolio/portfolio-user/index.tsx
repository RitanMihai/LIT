import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import PortfolioUser from './portfolio-user';
import PortfolioUserDetail from './portfolio-user-detail';
import PortfolioUserUpdate from './portfolio-user-update';
import PortfolioUserDeleteDialog from './portfolio-user-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={PortfolioUserUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={PortfolioUserUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={PortfolioUserDetail} />
      <ErrorBoundaryRoute path={match.url} component={PortfolioUser} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={PortfolioUserDeleteDialog} />
  </>
);

export default Routes;
