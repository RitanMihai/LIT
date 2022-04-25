import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import UserFollowing from './user-following';
import UserFollowingDetail from './user-following-detail';
import UserFollowingUpdate from './user-following-update';
import UserFollowingDeleteDialog from './user-following-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={UserFollowingUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={UserFollowingUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={UserFollowingDetail} />
      <ErrorBoundaryRoute path={match.url} component={UserFollowing} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={UserFollowingDeleteDialog} />
  </>
);

export default Routes;
