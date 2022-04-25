import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import SocialUser from './social-user';
import SocialUserDetail from './social-user-detail';
import SocialUserUpdate from './social-user-update';
import SocialUserDeleteDialog from './social-user-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={SocialUserUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={SocialUserUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={SocialUserDetail} />
      <ErrorBoundaryRoute path={match.url} component={SocialUser} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={SocialUserDeleteDialog} />
  </>
);

export default Routes;
