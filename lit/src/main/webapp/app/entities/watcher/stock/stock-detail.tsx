import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, openFile, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './stock.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const StockDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const stockEntity = useAppSelector(state => state.stock.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="stockDetailsHeading">
          <Translate contentKey="litApp.watcherStock.detail.title">Stock</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{stockEntity.id}</dd>
          <dt>
            <span id="ticker">
              <Translate contentKey="litApp.watcherStock.ticker">Ticker</Translate>
            </span>
          </dt>
          <dd>{stockEntity.ticker}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="litApp.watcherStock.name">Name</Translate>
            </span>
          </dt>
          <dd>{stockEntity.name}</dd>
          <dt>
            <span id="image">
              <Translate contentKey="litApp.watcherStock.image">Image</Translate>
            </span>
          </dt>
          <dd>
            {stockEntity.image ? (
              <div>
                {stockEntity.imageContentType ? (
                  <a onClick={openFile(stockEntity.imageContentType, stockEntity.image)}>
                    <img src={`data:${stockEntity.imageContentType};base64,${stockEntity.image}`} style={{ maxHeight: '30px' }} />
                  </a>
                ) : null}
                <span>
                  {stockEntity.imageContentType}, {byteSize(stockEntity.image)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="marketCap">
              <Translate contentKey="litApp.watcherStock.marketCap">Market Cap</Translate>
            </span>
          </dt>
          <dd>{stockEntity.marketCap}</dd>
          <dt>
            <span id="volume">
              <Translate contentKey="litApp.watcherStock.volume">Volume</Translate>
            </span>
          </dt>
          <dd>{stockEntity.volume}</dd>
          <dt>
            <span id="peRation">
              <Translate contentKey="litApp.watcherStock.peRation">Pe Ration</Translate>
            </span>
          </dt>
          <dd>{stockEntity.peRation}</dd>
          <dt>
            <span id="ipoDate">
              <Translate contentKey="litApp.watcherStock.ipoDate">Ipo Date</Translate>
            </span>
          </dt>
          <dd>{stockEntity.ipoDate ? <TextFormat value={stockEntity.ipoDate} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="isin">
              <Translate contentKey="litApp.watcherStock.isin">Isin</Translate>
            </span>
          </dt>
          <dd>{stockEntity.isin}</dd>
          <dt>
            <span id="isDelisted">
              <Translate contentKey="litApp.watcherStock.isDelisted">Is Delisted</Translate>
            </span>
          </dt>
          <dd>{stockEntity.isDelisted ? 'true' : 'false'}</dd>
          <dt>
            <span id="hasDividend">
              <Translate contentKey="litApp.watcherStock.hasDividend">Has Dividend</Translate>
            </span>
          </dt>
          <dd>{stockEntity.hasDividend ? 'true' : 'false'}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="litApp.watcherStock.type">Type</Translate>
            </span>
          </dt>
          <dd>{stockEntity.type}</dd>
          <dt>
            <span id="dividendYield">
              <Translate contentKey="litApp.watcherStock.dividendYield">Dividend Yield</Translate>
            </span>
          </dt>
          <dd>{stockEntity.dividendYield}</dd>
          <dt>
            <Translate contentKey="litApp.watcherStock.stockExchange">Stock Exchange</Translate>
          </dt>
          <dd>{stockEntity.stockExchange ? stockEntity.stockExchange.id : ''}</dd>
          <dt>
            <Translate contentKey="litApp.watcherStock.company">Company</Translate>
          </dt>
          <dd>{stockEntity.company ? stockEntity.company.id : ''}</dd>
          <dt>
            <Translate contentKey="litApp.watcherStock.currency">Currency</Translate>
          </dt>
          <dd>{stockEntity.currency ? stockEntity.currency.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/stock" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/stock/${stockEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default StockDetail;
