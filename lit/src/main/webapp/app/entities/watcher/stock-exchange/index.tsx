import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import StockExchange from './stock-exchange';
import StockExchangeDetail from './stock-exchange-detail';
import StockExchangeUpdate from './stock-exchange-update';
import StockExchangeDeleteDialog from './stock-exchange-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={StockExchangeUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={StockExchangeUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={StockExchangeDetail} />
      <ErrorBoundaryRoute path={match.url} component={StockExchange} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={StockExchangeDeleteDialog} />
  </>
);

export default Routes;
