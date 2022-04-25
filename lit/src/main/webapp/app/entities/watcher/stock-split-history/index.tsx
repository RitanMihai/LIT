import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import StockSplitHistory from './stock-split-history';
import StockSplitHistoryDetail from './stock-split-history-detail';
import StockSplitHistoryUpdate from './stock-split-history-update';
import StockSplitHistoryDeleteDialog from './stock-split-history-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={StockSplitHistoryUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={StockSplitHistoryUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={StockSplitHistoryDetail} />
      <ErrorBoundaryRoute path={match.url} component={StockSplitHistory} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={StockSplitHistoryDeleteDialog} />
  </>
);

export default Routes;
