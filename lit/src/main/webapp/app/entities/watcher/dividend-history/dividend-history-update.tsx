import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IStock } from 'app/shared/model/watcher/stock.model';
import { getEntities as getStocks } from 'app/entities/watcher/stock/stock.reducer';
import { getEntity, updateEntity, createEntity, reset } from './dividend-history.reducer';
import { IDividendHistory } from 'app/shared/model/watcher/dividend-history.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const DividendHistoryUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const stocks = useAppSelector(state => state.stock.entities);
  const dividendHistoryEntity = useAppSelector(state => state.dividendHistory.entity);
  const loading = useAppSelector(state => state.dividendHistory.loading);
  const updating = useAppSelector(state => state.dividendHistory.updating);
  const updateSuccess = useAppSelector(state => state.dividendHistory.updateSuccess);
  const handleClose = () => {
    props.history.push('/dividend-history' + props.location.search);
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
      ...dividendHistoryEntity,
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
          ...dividendHistoryEntity,
          stock: dividendHistoryEntity?.stock?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="litApp.watcherDividendHistory.home.createOrEditLabel" data-cy="DividendHistoryCreateUpdateHeading">
            <Translate contentKey="litApp.watcherDividendHistory.home.createOrEditLabel">Create or edit a DividendHistory</Translate>
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
                  id="dividend-history-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('litApp.watcherDividendHistory.date')}
                id="dividend-history-date"
                name="date"
                data-cy="date"
                type="date"
              />
              <ValidatedField
                label={translate('litApp.watcherDividendHistory.dividend')}
                id="dividend-history-dividend"
                name="dividend"
                data-cy="dividend"
                type="text"
              />
              <ValidatedField
                id="dividend-history-stock"
                name="stock"
                data-cy="stock"
                label={translate('litApp.watcherDividendHistory.stock')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/dividend-history" replace color="info">
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

export default DividendHistoryUpdate;
