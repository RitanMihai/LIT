import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import IncomeHistory from './income-history';
import IncomeHistoryDetail from './income-history-detail';
import IncomeHistoryUpdate from './income-history-update';
import IncomeHistoryDeleteDialog from './income-history-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={IncomeHistoryUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={IncomeHistoryUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={IncomeHistoryDetail} />
      <ErrorBoundaryRoute path={match.url} component={IncomeHistory} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={IncomeHistoryDeleteDialog} />
  </>
);

export default Routes;
