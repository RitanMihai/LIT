import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './income-history.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const IncomeHistoryDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const incomeHistoryEntity = useAppSelector(state => state.incomeHistory.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="incomeHistoryDetailsHeading">
          <Translate contentKey="litApp.watcherIncomeHistory.detail.title">IncomeHistory</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{incomeHistoryEntity.id}</dd>
          <dt>
            <span id="date">
              <Translate contentKey="litApp.watcherIncomeHistory.date">Date</Translate>
            </span>
          </dt>
          <dd>
            {incomeHistoryEntity.date ? <TextFormat value={incomeHistoryEntity.date} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="totalRevenue">
              <Translate contentKey="litApp.watcherIncomeHistory.totalRevenue">Total Revenue</Translate>
            </span>
          </dt>
          <dd>{incomeHistoryEntity.totalRevenue}</dd>
          <dt>
            <span id="costOfRevenue">
              <Translate contentKey="litApp.watcherIncomeHistory.costOfRevenue">Cost Of Revenue</Translate>
            </span>
          </dt>
          <dd>{incomeHistoryEntity.costOfRevenue}</dd>
          <dt>
            <span id="grossProfit">
              <Translate contentKey="litApp.watcherIncomeHistory.grossProfit">Gross Profit</Translate>
            </span>
          </dt>
          <dd>{incomeHistoryEntity.grossProfit}</dd>
          <dt>
            <span id="operatingExpense">
              <Translate contentKey="litApp.watcherIncomeHistory.operatingExpense">Operating Expense</Translate>
            </span>
          </dt>
          <dd>{incomeHistoryEntity.operatingExpense}</dd>
          <dt>
            <span id="operatingIncome">
              <Translate contentKey="litApp.watcherIncomeHistory.operatingIncome">Operating Income</Translate>
            </span>
          </dt>
          <dd>{incomeHistoryEntity.operatingIncome}</dd>
          <dt>
            <Translate contentKey="litApp.watcherIncomeHistory.stock">Stock</Translate>
          </dt>
          <dd>{incomeHistoryEntity.stock ? incomeHistoryEntity.stock.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/income-history" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/income-history/${incomeHistoryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default IncomeHistoryDetail;
