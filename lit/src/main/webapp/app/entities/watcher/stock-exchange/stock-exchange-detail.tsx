import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './stock-exchange.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const StockExchangeDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const stockExchangeEntity = useAppSelector(state => state.stockExchange.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="stockExchangeDetailsHeading">
          <Translate contentKey="litApp.watcherStockExchange.detail.title">StockExchange</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{stockExchangeEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="litApp.watcherStockExchange.name">Name</Translate>
            </span>
          </dt>
          <dd>{stockExchangeEntity.name}</dd>
          <dt>
            <span id="symbol">
              <Translate contentKey="litApp.watcherStockExchange.symbol">Symbol</Translate>
            </span>
          </dt>
          <dd>{stockExchangeEntity.symbol}</dd>
          <dt>
            <span id="country">
              <Translate contentKey="litApp.watcherStockExchange.country">Country</Translate>
            </span>
          </dt>
          <dd>{stockExchangeEntity.country}</dd>
        </dl>
        <Button tag={Link} to="/stock-exchange" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/stock-exchange/${stockExchangeEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default StockExchangeDetail;
