import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './price-history.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const PriceHistoryDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const priceHistoryEntity = useAppSelector(state => state.priceHistory.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="priceHistoryDetailsHeading">
          <Translate contentKey="litApp.watcherPriceHistory.detail.title">PriceHistory</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{priceHistoryEntity.id}</dd>
          <dt>
            <span id="date">
              <Translate contentKey="litApp.watcherPriceHistory.date">Date</Translate>
            </span>
          </dt>
          <dd>
            {priceHistoryEntity.date ? <TextFormat value={priceHistoryEntity.date} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="open">
              <Translate contentKey="litApp.watcherPriceHistory.open">Open</Translate>
            </span>
          </dt>
          <dd>{priceHistoryEntity.open}</dd>
          <dt>
            <span id="high">
              <Translate contentKey="litApp.watcherPriceHistory.high">High</Translate>
            </span>
          </dt>
          <dd>{priceHistoryEntity.high}</dd>
          <dt>
            <span id="low">
              <Translate contentKey="litApp.watcherPriceHistory.low">Low</Translate>
            </span>
          </dt>
          <dd>{priceHistoryEntity.low}</dd>
          <dt>
            <span id="close">
              <Translate contentKey="litApp.watcherPriceHistory.close">Close</Translate>
            </span>
          </dt>
          <dd>{priceHistoryEntity.close}</dd>
          <dt>
            <span id="adjClose">
              <Translate contentKey="litApp.watcherPriceHistory.adjClose">Adj Close</Translate>
            </span>
          </dt>
          <dd>{priceHistoryEntity.adjClose}</dd>
          <dt>
            <span id="volume">
              <Translate contentKey="litApp.watcherPriceHistory.volume">Volume</Translate>
            </span>
          </dt>
          <dd>{priceHistoryEntity.volume}</dd>
          <dt>
            <Translate contentKey="litApp.watcherPriceHistory.stock">Stock</Translate>
          </dt>
          <dd>{priceHistoryEntity.stock ? priceHistoryEntity.stock.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/price-history" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/price-history/${priceHistoryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PriceHistoryDetail;
