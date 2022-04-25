import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import CapitalGainHistory from './capital-gain-history';
import CapitalGainHistoryDetail from './capital-gain-history-detail';
import CapitalGainHistoryUpdate from './capital-gain-history-update';
import CapitalGainHistoryDeleteDialog from './capital-gain-history-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={CapitalGainHistoryUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={CapitalGainHistoryUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={CapitalGainHistoryDetail} />
      <ErrorBoundaryRoute path={match.url} component={CapitalGainHistory} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={CapitalGainHistoryDeleteDialog} />
  </>
);

export default Routes;
