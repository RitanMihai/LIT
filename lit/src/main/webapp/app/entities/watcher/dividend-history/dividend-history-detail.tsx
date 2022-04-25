import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './dividend-history.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const DividendHistoryDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const dividendHistoryEntity = useAppSelector(state => state.dividendHistory.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="dividendHistoryDetailsHeading">
          <Translate contentKey="litApp.watcherDividendHistory.detail.title">DividendHistory</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{dividendHistoryEntity.id}</dd>
          <dt>
            <span id="date">
              <Translate contentKey="litApp.watcherDividendHistory.date">Date</Translate>
            </span>
          </dt>
          <dd>
            {dividendHistoryEntity.date ? (
              <TextFormat value={dividendHistoryEntity.date} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="dividend">
              <Translate contentKey="litApp.watcherDividendHistory.dividend">Dividend</Translate>
            </span>
          </dt>
          <dd>{dividendHistoryEntity.dividend}</dd>
          <dt>
            <Translate contentKey="litApp.watcherDividendHistory.stock">Stock</Translate>
          </dt>
          <dd>{dividendHistoryEntity.stock ? dividendHistoryEntity.stock.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/dividend-history" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/dividend-history/${dividendHistoryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DividendHistoryDetail;
