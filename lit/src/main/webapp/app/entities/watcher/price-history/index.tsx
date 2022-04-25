import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import PriceHistory from './price-history';
import PriceHistoryDetail from './price-history-detail';
import PriceHistoryUpdate from './price-history-update';
import PriceHistoryDeleteDialog from './price-history-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={PriceHistoryUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={PriceHistoryUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={PriceHistoryDetail} />
      <ErrorBoundaryRoute path={match.url} component={PriceHistory} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={PriceHistoryDeleteDialog} />
  </>
);

export default Routes;
