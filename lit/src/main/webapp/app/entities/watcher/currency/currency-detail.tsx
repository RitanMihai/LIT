import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './currency.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const CurrencyDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const currencyEntity = useAppSelector(state => state.currency.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="currencyDetailsHeading">
          <Translate contentKey="litApp.watcherCurrency.detail.title">Currency</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{currencyEntity.id}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="litApp.watcherCurrency.code">Code</Translate>
            </span>
          </dt>
          <dd>{currencyEntity.code}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="litApp.watcherCurrency.name">Name</Translate>
            </span>
          </dt>
          <dd>{currencyEntity.name}</dd>
          <dt>
            <span id="currencySymbol">
              <Translate contentKey="litApp.watcherCurrency.currencySymbol">Currency Symbol</Translate>
            </span>
          </dt>
          <dd>{currencyEntity.currencySymbol}</dd>
        </dl>
        <Button tag={Link} to="/currency" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/currency/${currencyEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CurrencyDetail;
