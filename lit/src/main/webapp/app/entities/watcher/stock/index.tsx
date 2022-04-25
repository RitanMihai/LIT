import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Stock from './stock';
import StockDetail from './stock-detail';
import StockUpdate from './stock-update';
import StockDeleteDialog from './stock-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={StockUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={StockUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={StockDetail} />
      <ErrorBoundaryRoute path={match.url} component={Stock} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={StockDeleteDialog} />
  </>
);

export default Routes;
