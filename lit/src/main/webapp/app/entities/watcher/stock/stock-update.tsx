import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm, ValidatedBlobField } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IStockExchange } from 'app/shared/model/watcher/stock-exchange.model';
import { getEntities as getStockExchanges } from 'app/entities/watcher/stock-exchange/stock-exchange.reducer';
import { ICompany } from 'app/shared/model/watcher/company.model';
import { getEntities as getCompanies } from 'app/entities/watcher/company/company.reducer';
import { ICurrency } from 'app/shared/model/watcher/currency.model';
import { getEntities as getCurrencies } from 'app/entities/watcher/currency/currency.reducer';
import { getEntity, updateEntity, createEntity, reset } from './stock.reducer';
import { IStock } from 'app/shared/model/watcher/stock.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { StockType } from 'app/shared/model/enumerations/stock-type.model';

export const StockUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const stockExchanges = useAppSelector(state => state.stockExchange.entities);
  const companies = useAppSelector(state => state.company.entities);
  const currencies = useAppSelector(state => state.currency.entities);
  const stockEntity = useAppSelector(state => state.stock.entity);
  const loading = useAppSelector(state => state.stock.loading);
  const updating = useAppSelector(state => state.stock.updating);
  const updateSuccess = useAppSelector(state => state.stock.updateSuccess);
  const stockTypeValues = Object.keys(StockType);
  const handleClose = () => {
    props.history.push('/stock');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getStockExchanges({}));
    dispatch(getCompanies({}));
    dispatch(getCurrencies({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...stockEntity,
      ...values,
      stockExchange: stockExchanges.find(it => it.id.toString() === values.stockExchange.toString()),
      company: companies.find(it => it.id.toString() === values.company.toString()),
      currency: currencies.find(it => it.id.toString() === values.currency.toString()),
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
          type: 'COMMON',
          ...stockEntity,
          stockExchange: stockEntity?.stockExchange?.id,
          company: stockEntity?.company?.id,
          currency: stockEntity?.currency?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="litApp.watcherStock.home.createOrEditLabel" data-cy="StockCreateUpdateHeading">
            <Translate contentKey="litApp.watcherStock.home.createOrEditLabel">Create or edit a Stock</Translate>
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
                  id="stock-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('litApp.watcherStock.ticker')}
                id="stock-ticker"
                name="ticker"
                data-cy="ticker"
                type="text"
              />
              <ValidatedField label={translate('litApp.watcherStock.name')} id="stock-name" name="name" data-cy="name" type="text" />
              <ValidatedBlobField
                label={translate('litApp.watcherStock.image')}
                id="stock-image"
                name="image"
                data-cy="image"
                isImage
                accept="image/*"
              />
              <ValidatedField
                label={translate('litApp.watcherStock.marketCap')}
                id="stock-marketCap"
                name="marketCap"
                data-cy="marketCap"
                type="text"
              />
              <ValidatedField
                label={translate('litApp.watcherStock.volume')}
                id="stock-volume"
                name="volume"
                data-cy="volume"
                type="text"
              />
              <ValidatedField
                label={translate('litApp.watcherStock.peRation')}
                id="stock-peRation"
                name="peRation"
                data-cy="peRation"
                type="text"
              />
              <ValidatedField
                label={translate('litApp.watcherStock.ipoDate')}
                id="stock-ipoDate"
                name="ipoDate"
                data-cy="ipoDate"
                type="date"
              />
              <ValidatedField label={translate('litApp.watcherStock.isin')} id="stock-isin" name="isin" data-cy="isin" type="text" />
              <ValidatedField
                label={translate('litApp.watcherStock.isDelisted')}
                id="stock-isDelisted"
                name="isDelisted"
                data-cy="isDelisted"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('litApp.watcherStock.hasDividend')}
                id="stock-hasDividend"
                name="hasDividend"
                data-cy="hasDividend"
                check
                type="checkbox"
              />
              <ValidatedField label={translate('litApp.watcherStock.type')} id="stock-type" name="type" data-cy="type" type="select">
                {stockTypeValues.map(stockType => (
                  <option value={stockType} key={stockType}>
                    {translate('litApp.StockType.' + stockType)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('litApp.watcherStock.dividendYield')}
                id="stock-dividendYield"
                name="dividendYield"
                data-cy="dividendYield"
                type="text"
              />
              <ValidatedField
                id="stock-stockExchange"
                name="stockExchange"
                data-cy="stockExchange"
                label={translate('litApp.watcherStock.stockExchange')}
                type="select"
              >
                <option value="" key="0" />
                {stockExchanges
                  ? stockExchanges.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="stock-company"
                name="company"
                data-cy="company"
                label={translate('litApp.watcherStock.company')}
                type="select"
              >
                <option value="" key="0" />
                {companies
                  ? companies.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="stock-currency"
                name="currency"
                data-cy="currency"
                label={translate('litApp.watcherStock.currency')}
                type="select"
              >
                <option value="" key="0" />
                {currencies
                  ? currencies.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/stock" replace color="info">
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

export default StockUpdate;
