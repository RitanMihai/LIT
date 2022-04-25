import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, openFile, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './stock-info.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const StockInfoDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const stockInfoEntity = useAppSelector(state => state.stockInfo.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="stockInfoDetailsHeading">
          <Translate contentKey="litApp.portfolioStockInfo.detail.title">StockInfo</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{stockInfoEntity.id}</dd>
          <dt>
            <span id="ticker">
              <Translate contentKey="litApp.portfolioStockInfo.ticker">Ticker</Translate>
            </span>
          </dt>
          <dd>{stockInfoEntity.ticker}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="litApp.portfolioStockInfo.name">Name</Translate>
            </span>
          </dt>
          <dd>{stockInfoEntity.name}</dd>
          <dt>
            <span id="image">
              <Translate contentKey="litApp.portfolioStockInfo.image">Image</Translate>
            </span>
          </dt>
          <dd>
            {stockInfoEntity.image ? (
              <div>
                {stockInfoEntity.imageContentType ? (
                  <a onClick={openFile(stockInfoEntity.imageContentType, stockInfoEntity.image)}>
                    <img src={`data:${stockInfoEntity.imageContentType};base64,${stockInfoEntity.image}`} style={{ maxHeight: '30px' }} />
                  </a>
                ) : null}
                <span>
                  {stockInfoEntity.imageContentType}, {byteSize(stockInfoEntity.image)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="isin">
              <Translate contentKey="litApp.portfolioStockInfo.isin">Isin</Translate>
            </span>
          </dt>
          <dd>{stockInfoEntity.isin}</dd>
          <dt>
            <span id="dividendYield">
              <Translate contentKey="litApp.portfolioStockInfo.dividendYield">Dividend Yield</Translate>
            </span>
          </dt>
          <dd>{stockInfoEntity.dividendYield}</dd>
          <dt>
            <span id="sector">
              <Translate contentKey="litApp.portfolioStockInfo.sector">Sector</Translate>
            </span>
          </dt>
          <dd>{stockInfoEntity.sector}</dd>
          <dt>
            <span id="industry">
              <Translate contentKey="litApp.portfolioStockInfo.industry">Industry</Translate>
            </span>
          </dt>
          <dd>{stockInfoEntity.industry}</dd>
        </dl>
        <Button tag={Link} to="/stock-info" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/stock-info/${stockInfoEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default StockInfoDetail;
