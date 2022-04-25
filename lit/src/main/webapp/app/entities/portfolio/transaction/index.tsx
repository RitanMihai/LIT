import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Transaction from './transaction';
import TransactionDetail from './transaction-detail';
import TransactionUpdate from './transaction-update';
import TransactionDeleteDialog from './transaction-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={TransactionUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={TransactionUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={TransactionDetail} />
      <ErrorBoundaryRoute path={match.url} component={Transaction} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={TransactionDeleteDialog} />
  </>
);

export default Routes;
