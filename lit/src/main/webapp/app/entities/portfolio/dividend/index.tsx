import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Dividend from './dividend';
import DividendDetail from './dividend-detail';
import DividendUpdate from './dividend-update';
import DividendDeleteDialog from './dividend-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={DividendUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={DividendUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={DividendDetail} />
      <ErrorBoundaryRoute path={match.url} component={Dividend} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={DividendDeleteDialog} />
  </>
);

export default Routes;
