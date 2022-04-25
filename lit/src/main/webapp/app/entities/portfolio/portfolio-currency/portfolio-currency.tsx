import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Input, InputGroup, FormGroup, Form, Row, Col, Table } from 'reactstrap';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { searchEntities, getEntities } from './portfolio-currency.reducer';
import { IPortfolioCurrency } from 'app/shared/model/portfolio/portfolio-currency.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const PortfolioCurrency = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const [search, setSearch] = useState('');

  const portfolioCurrencyList = useAppSelector(state => state.portfolioCurrency.entities);
  const loading = useAppSelector(state => state.portfolioCurrency.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const startSearching = e => {
    if (search) {
      dispatch(searchEntities({ query: search }));
    }
    e.preventDefault();
  };

  const clear = () => {
    setSearch('');
    dispatch(getEntities({}));
  };

  const handleSearch = event => setSearch(event.target.value);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  const { match } = props;

  return (
    <div>
      <h2 id="portfolio-currency-heading" data-cy="PortfolioCurrencyHeading">
        <Translate contentKey="litApp.portfolioPortfolioCurrency.home.title">Portfolio Currencies</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="litApp.portfolioPortfolioCurrency.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="litApp.portfolioPortfolioCurrency.home.createLabel">Create new Portfolio Currency</Translate>
          </Link>
        </div>
      </h2>
      <Row>
        <Col sm="12">
          <Form onSubmit={startSearching}>
            <FormGroup>
              <InputGroup>
                <Input
                  type="text"
                  name="search"
                  defaultValue={search}
                  onChange={handleSearch}
                  placeholder={translate('litApp.portfolioPortfolioCurrency.home.search')}
                />
                <Button className="input-group-addon">
                  <FontAwesomeIcon icon="search" />
                </Button>
                <Button type="reset" className="input-group-addon" onClick={clear}>
                  <FontAwesomeIcon icon="trash" />
                </Button>
              </InputGroup>
            </FormGroup>
          </Form>
        </Col>
      </Row>
      <div className="table-responsive">
        {portfolioCurrencyList && portfolioCurrencyList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="litApp.portfolioPortfolioCurrency.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.portfolioPortfolioCurrency.code">Code</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.portfolioPortfolioCurrency.name">Name</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.portfolioPortfolioCurrency.currencySymbol">Currency Symbol</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {portfolioCurrencyList.map((portfolioCurrency, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${portfolioCurrency.id}`} color="link" size="sm">
                      {portfolioCurrency.id}
                    </Button>
                  </td>
                  <td>{portfolioCurrency.code}</td>
                  <td>{portfolioCurrency.name}</td>
                  <td>{portfolioCurrency.currencySymbol}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${portfolioCurrency.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`${match.url}/${portfolioCurrency.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`${match.url}/${portfolioCurrency.id}/delete`}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="litApp.portfolioPortfolioCurrency.home.notFound">No Portfolio Currencies found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default PortfolioCurrency;
