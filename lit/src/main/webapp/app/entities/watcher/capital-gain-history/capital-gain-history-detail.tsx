import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './capital-gain-history.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const CapitalGainHistoryDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const capitalGainHistoryEntity = useAppSelector(state => state.capitalGainHistory.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="capitalGainHistoryDetailsHeading">
          <Translate contentKey="litApp.watcherCapitalGainHistory.detail.title">CapitalGainHistory</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{capitalGainHistoryEntity.id}</dd>
          <dt>
            <span id="date">
              <Translate contentKey="litApp.watcherCapitalGainHistory.date">Date</Translate>
            </span>
          </dt>
          <dd>
            {capitalGainHistoryEntity.date ? (
              <TextFormat value={capitalGainHistoryEntity.date} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="capitalGain">
              <Translate contentKey="litApp.watcherCapitalGainHistory.capitalGain">Capital Gain</Translate>
            </span>
          </dt>
          <dd>{capitalGainHistoryEntity.capitalGain}</dd>
          <dt>
            <Translate contentKey="litApp.watcherCapitalGainHistory.stock">Stock</Translate>
          </dt>
          <dd>{capitalGainHistoryEntity.stock ? capitalGainHistoryEntity.stock.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/capital-gain-history" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/capital-gain-history/${capitalGainHistoryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CapitalGainHistoryDetail;
