import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import locale from './locale';
import authentication from './authentication';
import applicationProfile from './application-profile';

import administration from 'app/modules/administration/administration.reducer';
import userManagement from 'app/modules/administration/user-management/user-management.reducer';
import register from 'app/modules/account/register/register.reducer';
import activate from 'app/modules/account/activate/activate.reducer';
import password from 'app/modules/account/password/password.reducer';
import settings from 'app/modules/account/settings/settings.reducer';
import passwordReset from 'app/modules/account/password-reset/password-reset.reducer';
// prettier-ignore
import capitalGainHistory from 'app/entities/watcher/capital-gain-history/capital-gain-history.reducer';
// prettier-ignore
import comment from 'app/entities/social/comment/comment.reducer';
// prettier-ignore
import company from 'app/entities/watcher/company/company.reducer';
// prettier-ignore
import currency from 'app/entities/watcher/currency/currency.reducer';
// prettier-ignore
import dividend from 'app/entities/portfolio/dividend/dividend.reducer';
// prettier-ignore
import dividendHistory from 'app/entities/watcher/dividend-history/dividend-history.reducer';
// prettier-ignore
import incomeHistory from 'app/entities/watcher/income-history/income-history.reducer';
// prettier-ignore
import order from 'app/entities/portfolio/order/order.reducer';
// prettier-ignore
import orderDetails from 'app/entities/portfolio/order/order-details.reducer'
// prettier-ignore
import portfolio from 'app/entities/portfolio/portfolio/portfolio.reducer';
// prettier-ignore
import portfolioDetails from 'app/entities/portfolio/portfolio/portfolio-details.reducer'
// prettier-ignore
import portfolioCurrency from 'app/entities/portfolio/portfolio-currency/portfolio-currency.reducer';
// prettier-ignore
import portfolioUser from 'app/entities/portfolio/portfolio-user/portfolio-user.reducer';
// prettier-ignore
import post from 'app/entities/social/post/post.reducer';
// prettier-ignore
import priceHistory from 'app/entities/watcher/price-history/price-history.reducer';
// prettier-ignore
import reply from 'app/entities/social/reply/reply.reducer';
// prettier-ignore
import report from 'app/entities/social/report/report.reducer';
// prettier-ignore
import socialUser from 'app/entities/social/social-user/social-user.reducer';
// prettier-ignore
import stock from 'app/entities/watcher/stock/stock.reducer';
// prettier-ignore
import stockExchange from 'app/entities/watcher/stock-exchange/stock-exchange.reducer';
// prettier-ignore
import stockInfo from 'app/entities/portfolio/stock-info/stock-info.reducer';
// prettier-ignore
import stockSplitHistory from 'app/entities/watcher/stock-split-history/stock-split-history.reducer';
// prettier-ignore
import tag from 'app/entities/social/tag/tag.reducer';
// prettier-ignore
import transaction from 'app/entities/portfolio/transaction/transaction.reducer';
// prettier-ignore
import userFollowing from 'app/entities/social/user-following/user-following.reducer';
// prettier-ignore
import userReaction from 'app/entities/social/user-reaction/user-reaction.reducer';
// prettier-ignore
import prediction from 'app/entities/watcher/prediction/prediction.reducer'
// prettier-ignore
import stockGroupTypes from 'app/entities/watcher/stock/stock-group-type.reducer'
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const rootReducer = {
  authentication,
  locale,
  applicationProfile,
  administration,
  userManagement,
  register,
  activate,
  passwordReset,
  password,
  settings,
  capitalGainHistory,
  comment,
  company,
  currency,
  dividend,
  dividendHistory,
  incomeHistory,
  order,
  orderDetails,
  portfolio,
  portfolioDetails,
  portfolioCurrency,
  portfolioUser,
  post,
  priceHistory,
  reply,
  report,
  socialUser,
  stock,
  stockExchange,
  stockInfo,
  stockGroupTypes,
  stockSplitHistory,
  tag,
  transaction,
  userFollowing,
  userReaction,
  prediction,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
  loadingBar,
};

export default rootReducer;
