import React from 'react';
import { Switch } from 'react-router-dom';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import CapitalGainHistory from './watcher/capital-gain-history';
import Comment from './social/comment';
import Company from './watcher/company';
import Currency from './watcher/currency';
import Dividend from './portfolio/dividend';
import DividendHistory from './watcher/dividend-history';
import IncomeHistory from './watcher/income-history';
import Order from './portfolio/order';
import Portfolio from './portfolio/portfolio';
import PortfolioCurrency from './portfolio/portfolio-currency';
import PortfolioUser from './portfolio/portfolio-user';
import Post from './social/post';
import PriceHistory from './watcher/price-history';
import Reply from './social/reply';
import Report from './social/report';
import SocialUser from './social/social-user';
import Stock from './watcher/stock';
import StockExchange from './watcher/stock-exchange';
import StockInfo from './portfolio/stock-info';
import StockSplitHistory from './watcher/stock-split-history';
import Tag from './social/tag';
import Transaction from './portfolio/transaction';
import UserFollowing from './social/user-following';
import UserReaction from './social/user-reaction';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}capital-gain-history`} component={CapitalGainHistory} />
      <ErrorBoundaryRoute path={`${match.url}comment`} component={Comment} />
      <ErrorBoundaryRoute path={`${match.url}company`} component={Company} />
      <ErrorBoundaryRoute path={`${match.url}currency`} component={Currency} />
      <ErrorBoundaryRoute path={`${match.url}dividend`} component={Dividend} />
      <ErrorBoundaryRoute path={`${match.url}dividend-history`} component={DividendHistory} />
      <ErrorBoundaryRoute path={`${match.url}income-history`} component={IncomeHistory} />
      <ErrorBoundaryRoute path={`${match.url}order`} component={Order} />
      <ErrorBoundaryRoute path={`${match.url}portfolio`} component={Portfolio} />
      <ErrorBoundaryRoute path={`${match.url}portfolio-currency`} component={PortfolioCurrency} />
      <ErrorBoundaryRoute path={`${match.url}portfolio-user`} component={PortfolioUser} />
      <ErrorBoundaryRoute path={`${match.url}post`} component={Post} />
      <ErrorBoundaryRoute path={`${match.url}price-history`} component={PriceHistory} />
      <ErrorBoundaryRoute path={`${match.url}reply`} component={Reply} />
      <ErrorBoundaryRoute path={`${match.url}report`} component={Report} />
      <ErrorBoundaryRoute path={`${match.url}social-user`} component={SocialUser} />
      <ErrorBoundaryRoute path={`${match.url}stock`} component={Stock} />
      <ErrorBoundaryRoute path={`${match.url}stock-exchange`} component={StockExchange} />
      <ErrorBoundaryRoute path={`${match.url}stock-info`} component={StockInfo} />
      <ErrorBoundaryRoute path={`${match.url}stock-split-history`} component={StockSplitHistory} />
      <ErrorBoundaryRoute path={`${match.url}tag`} component={Tag} />
      <ErrorBoundaryRoute path={`${match.url}transaction`} component={Transaction} />
      <ErrorBoundaryRoute path={`${match.url}user-following`} component={UserFollowing} />
      <ErrorBoundaryRoute path={`${match.url}user-reaction`} component={UserReaction} />
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;
