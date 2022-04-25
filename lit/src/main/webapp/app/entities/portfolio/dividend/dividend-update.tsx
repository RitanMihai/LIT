import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IOrder } from 'app/shared/model/portfolio/order.model';
import { getEntities as getOrders } from 'app/entities/portfolio/order/order.reducer';
import { getEntity, updateEntity, createEntity, reset } from './dividend.reducer';
import { IDividend } from 'app/shared/model/portfolio/dividend.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { DividendType } from 'app/shared/model/enumerations/dividend-type.model';

export const DividendUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const orders = useAppSelector(state => state.order.entities);
  const dividendEntity = useAppSelector(state => state.dividend.entity);
  const loading = useAppSelector(state => state.dividend.loading);
  const updating = useAppSelector(state => state.dividend.updating);
  const updateSuccess = useAppSelector(state => state.dividend.updateSuccess);
  const dividendTypeValues = Object.keys(DividendType);
  const handleClose = () => {
    props.history.push('/dividend' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getOrders({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.dateRecived = convertDateTimeToServer(values.dateRecived);

    const entity = {
      ...dividendEntity,
      ...values,
      order: orders.find(it => it.id.toString() === values.order.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          dateRecived: displayDefaultDateTime(),
        }
      : {
          dividendType: 'PROPERTY_INCOME',
          ...dividendEntity,
          dateRecived: convertDateTimeFromServer(dividendEntity.dateRecived),
          order: dividendEntity?.order?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="litApp.portfolioDividend.home.createOrEditLabel" data-cy="DividendCreateUpdateHeading">
            <Translate contentKey="litApp.portfolioDividend.home.createOrEditLabel">Create or edit a Dividend</Translate>
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
                  id="dividend-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('litApp.portfolioDividend.dateRecived')}
                id="dividend-dateRecived"
                name="dateRecived"
                data-cy="dateRecived"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('litApp.portfolioDividend.taxRate')}
                id="dividend-taxRate"
                name="taxRate"
                data-cy="taxRate"
                type="text"
              />
              <ValidatedField
                label={translate('litApp.portfolioDividend.totalRecived')}
                id="dividend-totalRecived"
                name="totalRecived"
                data-cy="totalRecived"
                type="text"
              />
              <ValidatedField
                label={translate('litApp.portfolioDividend.dividendType')}
                id="dividend-dividendType"
                name="dividendType"
                data-cy="dividendType"
                type="select"
              >
                {dividendTypeValues.map(dividendType => (
                  <option value={dividendType} key={dividendType}>
                    {translate('litApp.DividendType.' + dividendType)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="dividend-order"
                name="order"
                data-cy="order"
                label={translate('litApp.portfolioDividend.order')}
                type="select"
              >
                <option value="" key="0" />
                {orders
                  ? orders.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/dividend" replace color="info">
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

export default DividendUpdate;
