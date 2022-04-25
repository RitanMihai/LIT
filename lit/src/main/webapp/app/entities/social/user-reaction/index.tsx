import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import UserReaction from './user-reaction';
import UserReactionDetail from './user-reaction-detail';
import UserReactionUpdate from './user-reaction-update';
import UserReactionDeleteDialog from './user-reaction-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={UserReactionUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={UserReactionUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={UserReactionDetail} />
      <ErrorBoundaryRoute path={match.url} component={UserReaction} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={UserReactionDeleteDialog} />
  </>
);

export default Routes;
