import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './stock-split-history.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const StockSplitHistoryDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const stockSplitHistoryEntity = useAppSelector(state => state.stockSplitHistory.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="stockSplitHistoryDetailsHeading">
          <Translate contentKey="litApp.watcherStockSplitHistory.detail.title">StockSplitHistory</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{stockSplitHistoryEntity.id}</dd>
          <dt>
            <span id="date">
              <Translate contentKey="litApp.watcherStockSplitHistory.date">Date</Translate>
            </span>
          </dt>
          <dd>
            {stockSplitHistoryEntity.date ? (
              <TextFormat value={stockSplitHistoryEntity.date} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="ratio">
              <Translate contentKey="litApp.watcherStockSplitHistory.ratio">Ratio</Translate>
            </span>
          </dt>
          <dd>{stockSplitHistoryEntity.ratio}</dd>
          <dt>
            <Translate contentKey="litApp.watcherStockSplitHistory.stock">Stock</Translate>
          </dt>
          <dd>{stockSplitHistoryEntity.stock ? stockSplitHistoryEntity.stock.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/stock-split-history" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/stock-split-history/${stockSplitHistoryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default StockSplitHistoryDetail;
