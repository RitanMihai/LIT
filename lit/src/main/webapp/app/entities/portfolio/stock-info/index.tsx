import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import StockInfo from './stock-info';
import StockInfoDetail from './stock-info-detail';
import StockInfoUpdate from './stock-info-update';
import StockInfoDeleteDialog from './stock-info-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={StockInfoUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={StockInfoUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={StockInfoDetail} />
      <ErrorBoundaryRoute path={match.url} component={StockInfo} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={StockInfoDeleteDialog} />
  </>
);

export default Routes;
