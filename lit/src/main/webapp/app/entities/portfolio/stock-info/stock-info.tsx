import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Input, InputGroup, FormGroup, Form, Row, Col, Table } from 'reactstrap';
import { openFile, byteSize, Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { searchEntities, getEntities } from './stock-info.reducer';
import { IStockInfo } from 'app/shared/model/portfolio/stock-info.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const StockInfo = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const [search, setSearch] = useState('');

  const stockInfoList = useAppSelector(state => state.stockInfo.entities);
  const loading = useAppSelector(state => state.stockInfo.loading);

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
      <h2 id="stock-info-heading" data-cy="StockInfoHeading">
        <Translate contentKey="litApp.portfolioStockInfo.home.title">Stock Infos</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="litApp.portfolioStockInfo.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="litApp.portfolioStockInfo.home.createLabel">Create new Stock Info</Translate>
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
                  placeholder={translate('litApp.portfolioStockInfo.home.search')}
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
        {stockInfoList && stockInfoList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="litApp.portfolioStockInfo.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.portfolioStockInfo.ticker">Ticker</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.portfolioStockInfo.name">Name</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.portfolioStockInfo.image">Image</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.portfolioStockInfo.isin">Isin</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.portfolioStockInfo.dividendYield">Dividend Yield</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.portfolioStockInfo.sector">Sector</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.portfolioStockInfo.industry">Industry</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {stockInfoList.map((stockInfo, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${stockInfo.id}`} color="link" size="sm">
                      {stockInfo.id}
                    </Button>
                  </td>
                  <td>{stockInfo.ticker}</td>
                  <td>{stockInfo.name}</td>
                  <td>
                    {stockInfo.image ? (
                      <div>
                        {stockInfo.imageContentType ? (
                          <a onClick={openFile(stockInfo.imageContentType, stockInfo.image)}>
                            <img src={`data:${stockInfo.imageContentType};base64,${stockInfo.image}`} style={{ maxHeight: '30px' }} />
                            &nbsp;
                          </a>
                        ) : null}
                        <span>
                          {stockInfo.imageContentType}, {byteSize(stockInfo.image)}
                        </span>
                      </div>
                    ) : null}
                  </td>
                  <td>{stockInfo.isin}</td>
                  <td>{stockInfo.dividendYield}</td>
                  <td>{stockInfo.sector}</td>
                  <td>{stockInfo.industry}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${stockInfo.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${stockInfo.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${stockInfo.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
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
              <Translate contentKey="litApp.portfolioStockInfo.home.notFound">No Stock Infos found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default StockInfo;
