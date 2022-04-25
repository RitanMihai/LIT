import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { Translate, translate } from 'react-jhipster';
import { NavDropdown } from './menu-components';

export const EntitiesMenu = props => (
  <NavDropdown
    icon="th-list"
    name={translate('global.menu.entities.main')}
    id="entity-menu"
    data-cy="entity"
    style={{ maxHeight: '80vh', overflow: 'auto' }}
  >
    <>{/* to avoid warnings when empty */}</>
    <MenuItem icon="asterisk" to="/capital-gain-history">
      <Translate contentKey="global.menu.entities.watcherCapitalGainHistory" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/comment">
      <Translate contentKey="global.menu.entities.socialComment" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/company">
      <Translate contentKey="global.menu.entities.watcherCompany" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/currency">
      <Translate contentKey="global.menu.entities.watcherCurrency" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/dividend">
      <Translate contentKey="global.menu.entities.portfolioDividend" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/dividend-history">
      <Translate contentKey="global.menu.entities.watcherDividendHistory" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/income-history">
      <Translate contentKey="global.menu.entities.watcherIncomeHistory" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/order">
      <Translate contentKey="global.menu.entities.portfolioOrder" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/portfolio">
      <Translate contentKey="global.menu.entities.portfolioPortfolio" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/portfolio-currency">
      <Translate contentKey="global.menu.entities.portfolioPortfolioCurrency" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/portfolio-user">
      <Translate contentKey="global.menu.entities.portfolioPortfolioUser" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/post">
      <Translate contentKey="global.menu.entities.socialPost" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/price-history">
      <Translate contentKey="global.menu.entities.watcherPriceHistory" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/reply">
      <Translate contentKey="global.menu.entities.socialReply" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/report">
      <Translate contentKey="global.menu.entities.socialReport" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/social-user">
      <Translate contentKey="global.menu.entities.socialSocialUser" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/stock">
      <Translate contentKey="global.menu.entities.watcherStock" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/stock-exchange">
      <Translate contentKey="global.menu.entities.watcherStockExchange" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/stock-info">
      <Translate contentKey="global.menu.entities.portfolioStockInfo" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/stock-split-history">
      <Translate contentKey="global.menu.entities.watcherStockSplitHistory" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/tag">
      <Translate contentKey="global.menu.entities.socialTag" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/transaction">
      <Translate contentKey="global.menu.entities.portfolioTransaction" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/user-following">
      <Translate contentKey="global.menu.entities.socialUserFollowing" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/user-reaction">
      <Translate contentKey="global.menu.entities.socialUserReaction" />
    </MenuItem>
    {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
  </NavDropdown>
);
