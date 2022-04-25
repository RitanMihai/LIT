import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IStock } from 'app/shared/model/watcher/stock.model';
import { getEntities as getStocks } from 'app/entities/watcher/stock/stock.reducer';
import { getEntity, updateEntity, createEntity, reset } from './price-history.reducer';
import { IPriceHistory } from 'app/shared/model/watcher/price-history.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const PriceHistoryUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const stocks = useAppSelector(state => state.stock.entities);
  const priceHistoryEntity = useAppSelector(state => state.priceHistory.entity);
  const loading = useAppSelector(state => state.priceHistory.loading);
  const updating = useAppSelector(state => state.priceHistory.updating);
  const updateSuccess = useAppSelector(state => state.priceHistory.updateSuccess);
  const handleClose = () => {
    props.history.push('/price-history' + props.location.search);
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
      ...priceHistoryEntity,
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
          ...priceHistoryEntity,
          stock: priceHistoryEntity?.stock?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="litApp.watcherPriceHistory.home.createOrEditLabel" data-cy="PriceHistoryCreateUpdateHeading">
            <Translate contentKey="litApp.watcherPriceHistory.home.createOrEditLabel">Create or edit a PriceHistory</Translate>
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
                  id="price-history-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('litApp.watcherPriceHistory.date')}
                id="price-history-date"
                name="date"
                data-cy="date"
                type="date"
              />
              <ValidatedField
                label={translate('litApp.watcherPriceHistory.open')}
                id="price-history-open"
                name="open"
                data-cy="open"
                type="text"
              />
              <ValidatedField
                label={translate('litApp.watcherPriceHistory.high')}
                id="price-history-high"
                name="high"
                data-cy="high"
                type="text"
              />
              <ValidatedField
                label={translate('litApp.watcherPriceHistory.low')}
                id="price-history-low"
                name="low"
                data-cy="low"
                type="text"
              />
              <ValidatedField
                label={translate('litApp.watcherPriceHistory.close')}
                id="price-history-close"
                name="close"
                data-cy="close"
                type="text"
              />
              <ValidatedField
                label={translate('litApp.watcherPriceHistory.adjClose')}
                id="price-history-adjClose"
                name="adjClose"
                data-cy="adjClose"
                type="text"
              />
              <ValidatedField
                label={translate('litApp.watcherPriceHistory.volume')}
                id="price-history-volume"
                name="volume"
                data-cy="volume"
                type="text"
              />
              <ValidatedField
                id="price-history-stock"
                name="stock"
                data-cy="stock"
                label={translate('litApp.watcherPriceHistory.stock')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/price-history" replace color="info">
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

export default PriceHistoryUpdate;
