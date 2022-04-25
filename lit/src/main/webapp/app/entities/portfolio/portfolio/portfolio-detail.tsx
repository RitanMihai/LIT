import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, UncontrolledTooltip, Row, Col } from 'reactstrap';
import { Translate, openFile, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './portfolio.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const PortfolioDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const portfolioEntity = useAppSelector(state => state.portfolio.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="portfolioDetailsHeading">
          <Translate contentKey="litApp.portfolioPortfolio.detail.title">Portfolio</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{portfolioEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="litApp.portfolioPortfolio.name">Name</Translate>
            </span>
          </dt>
          <dd>{portfolioEntity.name}</dd>
          <dt>
            <span id="value">
              <Translate contentKey="litApp.portfolioPortfolio.value">Value</Translate>
            </span>
          </dt>
          <dd>{portfolioEntity.value}</dd>
          <dt>
            <span id="image">
              <Translate contentKey="litApp.portfolioPortfolio.image">Image</Translate>
            </span>
          </dt>
          <dd>
            {portfolioEntity.image ? (
              <div>
                {portfolioEntity.imageContentType ? (
                  <a onClick={openFile(portfolioEntity.imageContentType, portfolioEntity.image)}>
                    <img src={`data:${portfolioEntity.imageContentType};base64,${portfolioEntity.image}`} style={{ maxHeight: '30px' }} />
                  </a>
                ) : null}
                <span>
                  {portfolioEntity.imageContentType}, {byteSize(portfolioEntity.image)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="unrealisedValue">
              <Translate contentKey="litApp.portfolioPortfolio.unrealisedValue">Unrealised Value</Translate>
            </span>
          </dt>
          <dd>{portfolioEntity.unrealisedValue}</dd>
          <dt>
            <span id="profitOrLoss">
              <Translate contentKey="litApp.portfolioPortfolio.profitOrLoss">Profit Or Loss</Translate>
            </span>
            <UncontrolledTooltip target="profitOrLoss">
              <Translate contentKey="litApp.portfolioPortfolio.help.profitOrLoss" />
            </UncontrolledTooltip>
          </dt>
          <dd>{portfolioEntity.profitOrLoss}</dd>
          <dt>
            <Translate contentKey="litApp.portfolioPortfolio.portfolioUser">Portfolio User</Translate>
          </dt>
          <dd>{portfolioEntity.portfolioUser ? portfolioEntity.portfolioUser.id : ''}</dd>
          <dt>
            <Translate contentKey="litApp.portfolioPortfolio.portfolioCurrency">Portfolio Currency</Translate>
          </dt>
          <dd>{portfolioEntity.portfolioCurrency ? portfolioEntity.portfolioCurrency.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/portfolio" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/portfolio/${portfolioEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PortfolioDetail;
