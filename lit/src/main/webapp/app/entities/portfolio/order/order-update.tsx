import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IStockInfo } from 'app/shared/model/portfolio/stock-info.model';
import { getEntities as getStockInfos } from 'app/entities/portfolio/stock-info/stock-info.reducer';
import { IPortfolio } from 'app/shared/model/portfolio/portfolio.model';
import { getEntities as getPortfolios } from 'app/entities/portfolio/portfolio/portfolio.reducer';
import { getEntity, updateEntity, createEntity, reset } from './order.reducer';
import { IOrder } from 'app/shared/model/portfolio/order.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { OrderType } from 'app/shared/model/enumerations/order-type.model';
import { PositionType } from 'app/shared/model/enumerations/position-type.model';

export const OrderUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const stockInfos = useAppSelector(state => state.stockInfo.entities);
  const portfolios = useAppSelector(state => state.portfolio.entities);
  const orderEntity = useAppSelector(state => state.order.entity);
  const loading = useAppSelector(state => state.order.loading);
  const updating = useAppSelector(state => state.order.updating);
  const updateSuccess = useAppSelector(state => state.order.updateSuccess);
  const orderTypeValues = Object.keys(OrderType);
  const positionTypeValues = Object.keys(PositionType);
  const handleClose = () => {
    props.history.push('/order' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getStockInfos({}));
    dispatch(getPortfolios({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.subbmitedDate = convertDateTimeToServer(values.subbmitedDate);
    values.filledDate = convertDateTimeToServer(values.filledDate);

    const entity = {
      ...orderEntity,
      ...values,
      stockInfo: stockInfos.find(it => it.id.toString() === values.stockInfo.toString()),
      portfolio: portfolios.find(it => it.id.toString() === values.portfolio.toString()),
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
        subbmitedDate: displayDefaultDateTime(),
        filledDate: displayDefaultDateTime(),
      }
      : {
        type: 'BUY',
        position: 'OPEN',
        ...orderEntity,
        subbmitedDate: convertDateTimeFromServer(orderEntity.subbmitedDate),
        filledDate: convertDateTimeFromServer(orderEntity.filledDate),
        stockInfo: orderEntity?.stockInfo?.id,
        portfolio: orderEntity?.portfolio?.id,
      };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="litApp.portfolioOrder.home.createOrEditLabel" data-cy="OrderCreateUpdateHeading">
            <Translate contentKey="litApp.portfolioOrder.home.createOrEditLabel">Create or edit a Order</Translate>
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
                  id="order-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('litApp.portfolioOrder.quantity')}
                id="order-quantity"
                name="quantity"
                data-cy="quantity"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('litApp.portfolioOrder.sharePrice')}
                id="order-sharePrice"
                name="sharePrice"
                data-cy="sharePrice"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField label={translate('litApp.portfolioOrder.type')} id="order-type" name="type" data-cy="type" type="select">
                {orderTypeValues.map(orderType => (
                  <option value={orderType} key={orderType}>
                    {translate('litApp.OrderType.' + orderType)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('litApp.portfolioOrder.position')}
                id="order-position"
                name="position"
                data-cy="position"
                type="select"
              >
                {positionTypeValues.map(positionType => (
                  <option value={positionType} key={positionType}>
                    {translate('litApp.PositionType.' + positionType)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('litApp.portfolioOrder.subbmitedDate')}
                id="order-subbmitedDate"
                name="subbmitedDate"
                data-cy="subbmitedDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('litApp.portfolioOrder.filledDate')}
                id="order-filledDate"
                name="filledDate"
                data-cy="filledDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField label={translate('litApp.portfolioOrder.notes')} id="order-notes" name="notes" data-cy="notes" type="text" />
              <ValidatedField label={translate('litApp.portfolioOrder.total')} id="order-total" name="total" data-cy="total" type="text" validate={{
                required: { value: true, message: translate('entity.validation.required') },
                validate: v => isNumber(v) || translate('entity.validation.number'),
              }} />
              <ValidatedField label={translate('litApp.portfolioOrder.taxes')} id="order-taxes" name="taxes" data-cy="taxes" type="text" />
              <ValidatedField
                label={translate('litApp.portfolioOrder.stopLoss')}
                id="order-stopLoss"
                name="stopLoss"
                data-cy="stopLoss"
                type="text"
              />
              <ValidatedField
                label={translate('litApp.portfolioOrder.takeProfit')}
                id="order-takeProfit"
                name="takeProfit"
                data-cy="takeProfit"
                type="text"
              />
              <ValidatedField
                label={translate('litApp.portfolioOrder.leverage')}
                id="order-leverage"
                name="leverage"
                data-cy="leverage"
                type="text"
              />
              <ValidatedField
                label={translate('litApp.portfolioOrder.exchangeRate')}
                id="order-exchangeRate"
                name="exchangeRate"
                data-cy="exchangeRate"
                type="text"
              />
              <ValidatedField
                label={translate('litApp.portfolioOrder.isCFD')}
                id="order-isCFD"
                name="isCFD"
                data-cy="isCFD"
                check
                type="checkbox"
              />
              <ValidatedField
                id="order-stockInfo"
                name="stockInfo"
                data-cy="stockInfo"
                label={translate('litApp.portfolioOrder.stockInfo')}
                type="select"
              >
                <option value="" key="0" />
                {stockInfos
                  ? stockInfos.map(otherEntity => (
                    <option value={otherEntity.id} key={otherEntity.id}>
                      {otherEntity.ticker}
                    </option>
                  ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="order-portfolio"
                name="portfolio"
                data-cy="portfolio"
                label={translate('litApp.portfolioOrder.portfolio')}
                type="select"
              >
                <option value="" key="0" />
                {portfolios
                  ? portfolios.map(otherEntity => (
                    <option value={otherEntity.id} key={otherEntity.id}>
                      {otherEntity.name}
                    </option>
                  ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/order" replace color="info">
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

export default OrderUpdate;
