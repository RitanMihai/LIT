import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IStock } from 'app/shared/model/watcher/stock.model';
import { getEntities as getStocks } from 'app/entities/watcher/stock/stock.reducer';
import { getEntity, updateEntity, createEntity, reset } from './income-history.reducer';
import { IIncomeHistory } from 'app/shared/model/watcher/income-history.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const IncomeHistoryUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const stocks = useAppSelector(state => state.stock.entities);
  const incomeHistoryEntity = useAppSelector(state => state.incomeHistory.entity);
  const loading = useAppSelector(state => state.incomeHistory.loading);
  const updating = useAppSelector(state => state.incomeHistory.updating);
  const updateSuccess = useAppSelector(state => state.incomeHistory.updateSuccess);
  const handleClose = () => {
    props.history.push('/income-history' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getStocks({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...incomeHistoryEntity,
      ...values,
      stock: stocks.find(it => it.id.toString() === values.stock.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...incomeHistoryEntity,
          stock: incomeHistoryEntity?.stock?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="litApp.watcherIncomeHistory.home.createOrEditLabel" data-cy="IncomeHistoryCreateUpdateHeading">
            <Translate contentKey="litApp.watcherIncomeHistory.home.createOrEditLabel">Create or edit a IncomeHistory</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="income-history-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('litApp.watcherIncomeHistory.date')}
                id="income-history-date"
                name="date"
                data-cy="date"
                type="date"
              />
              <ValidatedField
                label={translate('litApp.watcherIncomeHistory.totalRevenue')}
                id="income-history-totalRevenue"
                name="totalRevenue"
                data-cy="totalRevenue"
                type="text"
              />
              <ValidatedField
                label={translate('litApp.watcherIncomeHistory.costOfRevenue')}
                id="income-history-costOfRevenue"
                name="costOfRevenue"
                data-cy="costOfRevenue"
                type="text"
              />
              <ValidatedField
                label={translate('litApp.watcherIncomeHistory.grossProfit')}
                id="income-history-grossProfit"
                name="grossProfit"
                data-cy="grossProfit"
                type="text"
              />
              <ValidatedField
                label={translate('litApp.watcherIncomeHistory.operatingExpense')}
                id="income-history-operatingExpense"
                name="operatingExpense"
                data-cy="operatingExpense"
                type="text"
              />
              <ValidatedField
                label={translate('litApp.watcherIncomeHistory.operatingIncome')}
                id="income-history-operatingIncome"
                name="operatingIncome"
                data-cy="operatingIncome"
                type="text"
              />
              <ValidatedField
                id="income-history-stock"
                name="stock"
                data-cy="stock"
                label={translate('litApp.watcherIncomeHistory.stock')}
                type="select"
              >
                <option value="" key="0" />
                {stocks
                  ? stocks.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/income-history" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default IncomeHistoryUpdate;
