import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import PortfolioCurrency from './portfolio-currency';
import PortfolioCurrencyDetail from './portfolio-currency-detail';
import PortfolioCurrencyUpdate from './portfolio-currency-update';
import PortfolioCurrencyDeleteDialog from './portfolio-currency-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={PortfolioCurrencyUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={PortfolioCurrencyUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={PortfolioCurrencyDetail} />
      <ErrorBoundaryRoute path={match.url} component={PortfolioCurrency} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={PortfolioCurrencyDeleteDialog} />
  </>
);

export default Routes;
