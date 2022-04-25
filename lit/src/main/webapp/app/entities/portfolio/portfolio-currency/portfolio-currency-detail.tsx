import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './portfolio-currency.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const PortfolioCurrencyDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const portfolioCurrencyEntity = useAppSelector(state => state.portfolioCurrency.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="portfolioCurrencyDetailsHeading">
          <Translate contentKey="litApp.portfolioPortfolioCurrency.detail.title">PortfolioCurrency</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{portfolioCurrencyEntity.id}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="litApp.portfolioPortfolioCurrency.code">Code</Translate>
            </span>
          </dt>
          <dd>{portfolioCurrencyEntity.code}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="litApp.portfolioPortfolioCurrency.name">Name</Translate>
            </span>
          </dt>
          <dd>{portfolioCurrencyEntity.name}</dd>
          <dt>
            <span id="currencySymbol">
              <Translate contentKey="litApp.portfolioPortfolioCurrency.currencySymbol">Currency Symbol</Translate>
            </span>
          </dt>
          <dd>{portfolioCurrencyEntity.currencySymbol}</dd>
        </dl>
        <Button tag={Link} to="/portfolio-currency" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/portfolio-currency/${portfolioCurrencyEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PortfolioCurrencyDetail;
