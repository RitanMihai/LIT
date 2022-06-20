import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText, UncontrolledTooltip } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm, ValidatedBlobField } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IPortfolioUser } from 'app/shared/model/portfolio/portfolio-user.model';
import { getEntities as getPortfolioUsers, reset } from 'app/entities/portfolio/portfolio-user/portfolio-user.reducer';
import { IPortfolioCurrency } from 'app/shared/model/portfolio/portfolio-currency.model';
import { getEntities as getPortfolioCurrencies } from 'app/entities/portfolio/portfolio-currency/portfolio-currency.reducer';

import { IPortfolio } from 'app/shared/model/portfolio/portfolio.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { Card, CardContent, Container } from '@mui/material';
import { createEntity, getEntity, updateEntity } from 'app/entities/portfolio/portfolio/portfolio.reducer';


export const PortfolioDetail = (props: RouteComponentProps<{ id: string }>) => {
    const dispatch = useAppDispatch();
    const account = useAppSelector(state => state.authentication.account);

    const [isNew] = useState(!props.match.params || !props.match.params.id);

    const portfolioUsers = useAppSelector(state => state.portfolioUser.entities);
    const portfolioCurrencies = useAppSelector(state => state.portfolioCurrency.entities);
    const portfolioEntity = useAppSelector(state => state.portfolio.entity);
    const loading = useAppSelector(state => state.portfolio.loading);
    const updating = useAppSelector(state => state.portfolio.updating);
    const updateSuccess = useAppSelector(state => state.portfolio.updateSuccess);
    const handleClose = () => {
        props.history.push('/portfolios-manager' + props.location.search);
    };

    useEffect(() => {
        if (isNew) {
            dispatch(reset());
        } else {
            dispatch(getEntity(props.match.params.id));
        }

        dispatch(getPortfolioUsers({}));
        dispatch(getPortfolioCurrencies({}));
    }, []);

    useEffect(() => {
        if (updateSuccess) {
            handleClose();
        }
    }, [updateSuccess]);

    const saveEntity = values => {
        console.log("SAVE VALUES ", values)
        const entity = {
            ...portfolioEntity,
            ...values,
            portfolioUser: portfolioUsers.find(it => it.user.toString() === account.login),
            portfolioCurrency: portfolioCurrencies.find(it => it.id.toString() === values.portfolioCurrency.toString()),
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
                ...portfolioEntity,
                portfolioUser: portfolioEntity?.portfolioUser?.id,
                portfolioCurrency: portfolioEntity?.portfolioCurrency?.id,
            };

    return (
        <Container>
            <h2 id="litApp.portfolioPortfolio.home.createOrEditLabel" data-cy="PortfolioCreateUpdateHeading">
                Create a Portfolio
            </h2>
            <Card >
                <CardContent>

                    {loading ? (
                        <p>Loading...</p>
                    ) : (
                        <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
                            {!isNew ? (
                                <ValidatedField
                                    name="id"
                                    required
                                    readOnly
                                    id="portfolio-id"
                                    label={translate('global.field.id')}
                                    validate={{ required: true }}
                                />
                            ) : null}
                            <ValidatedField
                                label={translate('litApp.portfolioPortfolio.name')}
                                id="portfolio-name"
                                name="name"
                                data-cy="name"
                                type="text"
                            />
                            <ValidatedField
                                id="portfolio-portfolioCurrency"
                                name="portfolioCurrency"
                                data-cy="portfolioCurrency"
                                label={translate('litApp.portfolioPortfolio.portfolioCurrency')}
                                type="select"
                            >
                                <option value="" key="0" />
                                {portfolioCurrencies
                                    ? portfolioCurrencies.map(otherEntity => (
                                        <option value={otherEntity.id} key={otherEntity.id}>
                                            {otherEntity.name}
                                        </option>
                                    ))
                                    : null}
                            </ValidatedField>
                            <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/portfolios-manager" replace color="info">
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

                </CardContent>
            </Card>
        </Container>
    );
}

export default PortfolioDetail;