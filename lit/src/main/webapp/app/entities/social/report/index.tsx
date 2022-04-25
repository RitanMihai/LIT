import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Report from './report';
import ReportDetail from './report-detail';
import ReportUpdate from './report-update';
import ReportDeleteDialog from './report-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ReportUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ReportUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ReportDetail} />
      <ErrorBoundaryRoute path={match.url} component={Report} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ReportDeleteDialog} />
  </>
);

export default Routes;
