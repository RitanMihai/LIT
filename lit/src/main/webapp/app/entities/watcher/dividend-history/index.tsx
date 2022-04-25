import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import DividendHistory from './dividend-history';
import DividendHistoryDetail from './dividend-history-detail';
import DividendHistoryUpdate from './dividend-history-update';
import DividendHistoryDeleteDialog from './dividend-history-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={DividendHistoryUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={DividendHistoryUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={DividendHistoryDetail} />
      <ErrorBoundaryRoute path={match.url} component={DividendHistory} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={DividendHistoryDeleteDialog} />
  </>
);

export default Routes;
