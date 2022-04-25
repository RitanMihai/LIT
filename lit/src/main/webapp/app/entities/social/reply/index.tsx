import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Reply from './reply';
import ReplyDetail from './reply-detail';
import ReplyUpdate from './reply-update';
import ReplyDeleteDialog from './reply-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ReplyUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ReplyUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ReplyDetail} />
      <ErrorBoundaryRoute path={match.url} component={Reply} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ReplyDeleteDialog} />
  </>
);

export default Routes;
