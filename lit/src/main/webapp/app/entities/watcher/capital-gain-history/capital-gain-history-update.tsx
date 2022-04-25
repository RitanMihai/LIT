import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IStock } from 'app/shared/model/watcher/stock.model';
import { getEntities as getStocks } from 'app/entities/watcher/stock/stock.reducer';
import { getEntity, updateEntity, createEntity, reset } from './capital-gain-history.reducer';
import { ICapitalGainHistory } from 'app/shared/model/watcher/capital-gain-history.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const CapitalGainHistoryUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const stocks = useAppSelector(state => state.stock.entities);
  const capitalGainHistoryEntity = useAppSelector(state => state.capitalGainHistory.entity);
  const loading = useAppSelector(state => state.capitalGainHistory.loading);
  const updating = useAppSelector(state => state.capitalGainHistory.updating);
  const updateSuccess = useAppSelector(state => state.capitalGainHistory.updateSuccess);
  const handleClose = () => {
    props.history.push('/capital-gain-history' + props.location.search);
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
      ...capitalGainHistoryEntity,
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
          ...capitalGainHistoryEntity,
          stock: capitalGainHistoryEntity?.stock?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="litApp.watcherCapitalGainHistory.home.createOrEditLabel" data-cy="CapitalGainHistoryCreateUpdateHeading">
            <Translate contentKey="litApp.watcherCapitalGainHistory.home.createOrEditLabel">Create or edit a CapitalGainHistory</Translate>
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
                  id="capital-gain-history-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('litApp.watcherCapitalGainHistory.date')}
                id="capital-gain-history-date"
                name="date"
                data-cy="date"
                type="date"
              />
              <ValidatedField
                label={translate('litApp.watcherCapitalGainHistory.capitalGain')}
                id="capital-gain-history-capitalGain"
                name="capitalGain"
                data-cy="capitalGain"
                type="text"
              />
              <ValidatedField
                id="capital-gain-history-stock"
                name="stock"
                data-cy="stock"
                label={translate('litApp.watcherCapitalGainHistory.stock')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/capital-gain-history" replace color="info">
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

export default CapitalGainHistoryUpdate;
