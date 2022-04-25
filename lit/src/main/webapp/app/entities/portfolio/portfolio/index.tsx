import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Portfolio from './portfolio';
import PortfolioDetail from './portfolio-detail';
import PortfolioUpdate from './portfolio-update';
import PortfolioDeleteDialog from './portfolio-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={PortfolioUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={PortfolioUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={PortfolioDetail} />
      <ErrorBoundaryRoute path={match.url} component={Portfolio} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={PortfolioDeleteDialog} />
  </>
);

export default Routes;
