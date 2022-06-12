import React from 'react';
import { Switch } from 'react-router-dom';
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';
import WatchStock from './watch-stock';
import WatcherHome from './watcher-home';
import WatchSectors from './watch-sectors';
import WatchIndustries from './watch-industries';

const Watcher = ({ match }) => (
    <>
        <Switch>
            <ErrorBoundaryRoute exact path={`${match.url}/:name`} component={WatchStock} />
            <ErrorBoundaryRoute exact path={`${match.url}/sectors/:sector`} component={WatchSectors} />
            <ErrorBoundaryRoute exact path={`${match.url}/industries/:industry`} component={WatchIndustries} />
            <ErrorBoundaryRoute path={match.url} component={WatcherHome} />
        </Switch>
    </>
);

export default Watcher;
