import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './order.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const OrderDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const orderEntity = useAppSelector(state => state.order.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="orderDetailsHeading">
          <Translate contentKey="litApp.portfolioOrder.detail.title">Order</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{orderEntity.id}</dd>
          <dt>
            <span id="quantity">
              <Translate contentKey="litApp.portfolioOrder.quantity">Quantity</Translate>
            </span>
          </dt>
          <dd>{orderEntity.quantity}</dd>
          <dt>
            <span id="sharePrice">
              <Translate contentKey="litApp.portfolioOrder.sharePrice">Share Price</Translate>
            </span>
          </dt>
          <dd>{orderEntity.sharePrice}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="litApp.portfolioOrder.type">Type</Translate>
            </span>
          </dt>
          <dd>{orderEntity.type}</dd>
          <dt>
            <span id="position">
              <Translate contentKey="litApp.portfolioOrder.position">Position</Translate>
            </span>
          </dt>
          <dd>{orderEntity.position}</dd>
          <dt>
            <span id="subbmitedDate">
              <Translate contentKey="litApp.portfolioOrder.subbmitedDate">Subbmited Date</Translate>
            </span>
          </dt>
          <dd>
            {orderEntity.subbmitedDate ? <TextFormat value={orderEntity.subbmitedDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="filledDate">
              <Translate contentKey="litApp.portfolioOrder.filledDate">Filled Date</Translate>
            </span>
          </dt>
          <dd>{orderEntity.filledDate ? <TextFormat value={orderEntity.filledDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="notes">
              <Translate contentKey="litApp.portfolioOrder.notes">Notes</Translate>
            </span>
          </dt>
          <dd>{orderEntity.notes}</dd>
          <dt>
            <span id="total">
              <Translate contentKey="litApp.portfolioOrder.total">Total</Translate>
            </span>
          </dt>
          <dd>{orderEntity.total}</dd>
          <dt>
            <span id="taxes">
              <Translate contentKey="litApp.portfolioOrder.taxes">Taxes</Translate>
            </span>
          </dt>
          <dd>{orderEntity.taxes}</dd>
          <dt>
            <span id="stopLoss">
              <Translate contentKey="litApp.portfolioOrder.stopLoss">Stop Loss</Translate>
            </span>
          </dt>
          <dd>{orderEntity.stopLoss}</dd>
          <dt>
            <span id="takeProfit">
              <Translate contentKey="litApp.portfolioOrder.takeProfit">Take Profit</Translate>
            </span>
          </dt>
          <dd>{orderEntity.takeProfit}</dd>
          <dt>
            <span id="leverage">
              <Translate contentKey="litApp.portfolioOrder.leverage">Leverage</Translate>
            </span>
          </dt>
          <dd>{orderEntity.leverage}</dd>
          <dt>
            <span id="exchangeRate">
              <Translate contentKey="litApp.portfolioOrder.exchangeRate">Exchange Rate</Translate>
            </span>
          </dt>
          <dd>{orderEntity.exchangeRate}</dd>
          <dt>
            <span id="isCFD">
              <Translate contentKey="litApp.portfolioOrder.isCFD">Is CFD</Translate>
            </span>
          </dt>
          <dd>{orderEntity.isCFD ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="litApp.portfolioOrder.stockInfo">Stock Info</Translate>
          </dt>
          <dd>{orderEntity.stockInfo ? orderEntity.stockInfo.id : ''}</dd>
          <dt>
            <Translate contentKey="litApp.portfolioOrder.portfolio">Portfolio</Translate>
          </dt>
          <dd>{orderEntity.portfolio ? orderEntity.portfolio.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/order" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/order/${orderEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default OrderDetail;
