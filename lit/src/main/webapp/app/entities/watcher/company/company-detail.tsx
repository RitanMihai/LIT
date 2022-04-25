import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, openFile, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './company.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const CompanyDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const companyEntity = useAppSelector(state => state.company.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="companyDetailsHeading">
          <Translate contentKey="litApp.watcherCompany.detail.title">Company</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{companyEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="litApp.watcherCompany.name">Name</Translate>
            </span>
          </dt>
          <dd>{companyEntity.name}</dd>
          <dt>
            <span id="image">
              <Translate contentKey="litApp.watcherCompany.image">Image</Translate>
            </span>
          </dt>
          <dd>
            {companyEntity.image ? (
              <div>
                {companyEntity.imageContentType ? (
                  <a onClick={openFile(companyEntity.imageContentType, companyEntity.image)}>
                    <img src={`data:${companyEntity.imageContentType};base64,${companyEntity.image}`} style={{ maxHeight: '30px' }} />
                  </a>
                ) : null}
                <span>
                  {companyEntity.imageContentType}, {byteSize(companyEntity.image)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="description">
              <Translate contentKey="litApp.watcherCompany.description">Description</Translate>
            </span>
          </dt>
          <dd>{companyEntity.description}</dd>
          <dt>
            <span id="employees">
              <Translate contentKey="litApp.watcherCompany.employees">Employees</Translate>
            </span>
          </dt>
          <dd>{companyEntity.employees}</dd>
          <dt>
            <span id="sector">
              <Translate contentKey="litApp.watcherCompany.sector">Sector</Translate>
            </span>
          </dt>
          <dd>{companyEntity.sector}</dd>
          <dt>
            <span id="industry">
              <Translate contentKey="litApp.watcherCompany.industry">Industry</Translate>
            </span>
          </dt>
          <dd>{companyEntity.industry}</dd>
          <dt>
            <span id="ceo">
              <Translate contentKey="litApp.watcherCompany.ceo">Ceo</Translate>
            </span>
          </dt>
          <dd>{companyEntity.ceo}</dd>
          <dt>
            <span id="site">
              <Translate contentKey="litApp.watcherCompany.site">Site</Translate>
            </span>
          </dt>
          <dd>{companyEntity.site}</dd>
          <dt>
            <span id="dateOfEstablishment">
              <Translate contentKey="litApp.watcherCompany.dateOfEstablishment">Date Of Establishment</Translate>
            </span>
          </dt>
          <dd>
            {companyEntity.dateOfEstablishment ? (
              <TextFormat value={companyEntity.dateOfEstablishment} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
        </dl>
        <Button tag={Link} to="/company" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/company/${companyEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CompanyDetail;
