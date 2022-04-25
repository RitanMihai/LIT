import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm, ValidatedBlobField } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity, updateEntity, createEntity, reset } from './stock-info.reducer';
import { IStockInfo } from 'app/shared/model/portfolio/stock-info.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const StockInfoUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const stockInfoEntity = useAppSelector(state => state.stockInfo.entity);
  const loading = useAppSelector(state => state.stockInfo.loading);
  const updating = useAppSelector(state => state.stockInfo.updating);
  const updateSuccess = useAppSelector(state => state.stockInfo.updateSuccess);
  const handleClose = () => {
    props.history.push('/stock-info');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...stockInfoEntity,
      ...values,
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
          ...stockInfoEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="litApp.portfolioStockInfo.home.createOrEditLabel" data-cy="StockInfoCreateUpdateHeading">
            <Translate contentKey="litApp.portfolioStockInfo.home.createOrEditLabel">Create or edit a StockInfo</Translate>
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
                  id="stock-info-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('litApp.portfolioStockInfo.ticker')}
                id="stock-info-ticker"
                name="ticker"
                data-cy="ticker"
                type="text"
              />
              <ValidatedField
                label={translate('litApp.portfolioStockInfo.name')}
                id="stock-info-name"
                name="name"
                data-cy="name"
                type="text"
              />
              <ValidatedBlobField
                label={translate('litApp.portfolioStockInfo.image')}
                id="stock-info-image"
                name="image"
                data-cy="image"
                isImage
                accept="image/*"
              />
              <ValidatedField
                label={translate('litApp.portfolioStockInfo.isin')}
                id="stock-info-isin"
                name="isin"
                data-cy="isin"
                type="text"
              />
              <ValidatedField
                label={translate('litApp.portfolioStockInfo.dividendYield')}
                id="stock-info-dividendYield"
                name="dividendYield"
                data-cy="dividendYield"
                type="text"
              />
              <ValidatedField
                label={translate('litApp.portfolioStockInfo.sector')}
                id="stock-info-sector"
                name="sector"
                data-cy="sector"
                type="text"
              />
              <ValidatedField
                label={translate('litApp.portfolioStockInfo.industry')}
                id="stock-info-industry"
                name="industry"
                data-cy="industry"
                type="text"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/stock-info" replace color="info">
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

export default StockInfoUpdate;
