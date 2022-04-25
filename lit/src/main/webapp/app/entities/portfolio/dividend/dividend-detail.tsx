import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './dividend.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const DividendDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const dividendEntity = useAppSelector(state => state.dividend.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="dividendDetailsHeading">
          <Translate contentKey="litApp.portfolioDividend.detail.title">Dividend</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{dividendEntity.id}</dd>
          <dt>
            <span id="dateRecived">
              <Translate contentKey="litApp.portfolioDividend.dateRecived">Date Recived</Translate>
            </span>
          </dt>
          <dd>
            {dividendEntity.dateRecived ? <TextFormat value={dividendEntity.dateRecived} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="taxRate">
              <Translate contentKey="litApp.portfolioDividend.taxRate">Tax Rate</Translate>
            </span>
          </dt>
          <dd>{dividendEntity.taxRate}</dd>
          <dt>
            <span id="totalRecived">
              <Translate contentKey="litApp.portfolioDividend.totalRecived">Total Recived</Translate>
            </span>
          </dt>
          <dd>{dividendEntity.totalRecived}</dd>
          <dt>
            <span id="dividendType">
              <Translate contentKey="litApp.portfolioDividend.dividendType">Dividend Type</Translate>
            </span>
          </dt>
          <dd>{dividendEntity.dividendType}</dd>
          <dt>
            <Translate contentKey="litApp.portfolioDividend.order">Order</Translate>
          </dt>
          <dd>{dividendEntity.order ? dividendEntity.order.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/dividend" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/dividend/${dividendEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DividendDetail;
