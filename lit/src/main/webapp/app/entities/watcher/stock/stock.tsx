import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Input, InputGroup, FormGroup, Form, Row, Col, Table } from 'reactstrap';
import { openFile, byteSize, Translate, translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { searchEntities, getEntities } from './stock.reducer';
import { IStock } from 'app/shared/model/watcher/stock.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const Stock = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const [search, setSearch] = useState('');

  const stockList = useAppSelector(state => state.stock.entities);
  const loading = useAppSelector(state => state.stock.loading);

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
      <h2 id="stock-heading" data-cy="StockHeading">
        <Translate contentKey="litApp.watcherStock.home.title">Stocks</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="litApp.watcherStock.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="litApp.watcherStock.home.createLabel">Create new Stock</Translate>
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
                  placeholder={translate('litApp.watcherStock.home.search')}
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
        {stockList && stockList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="litApp.watcherStock.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.watcherStock.ticker">Ticker</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.watcherStock.name">Name</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.watcherStock.image">Image</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.watcherStock.marketCap">Market Cap</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.watcherStock.volume">Volume</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.watcherStock.peRation">Pe Ration</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.watcherStock.ipoDate">Ipo Date</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.watcherStock.isin">Isin</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.watcherStock.isDelisted">Is Delisted</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.watcherStock.hasDividend">Has Dividend</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.watcherStock.type">Type</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.watcherStock.dividendYield">Dividend Yield</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.watcherStock.stockExchange">Stock Exchange</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.watcherStock.company">Company</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.watcherStock.currency">Currency</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {stockList.map((stock, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${stock.id}`} color="link" size="sm">
                      {stock.id}
                    </Button>
                  </td>
                  <td>{stock.ticker}</td>
                  <td>{stock.name}</td>
                  <td>
                    {stock.image ? (
                      <div>
                        {stock.imageContentType ? (
                          <a onClick={openFile(stock.imageContentType, stock.image)}>
                            <img src={`data:${stock.imageContentType};base64,${stock.image}`} style={{ maxHeight: '30px' }} />
                            &nbsp;
                          </a>
                        ) : null}
                        <span>
                          {stock.imageContentType}, {byteSize(stock.image)}
                        </span>
                      </div>
                    ) : null}
                  </td>
                  <td>{stock.marketCap}</td>
                  <td>{stock.volume}</td>
                  <td>{stock.peRation}</td>
                  <td>{stock.ipoDate ? <TextFormat type="date" value={stock.ipoDate} format={APP_LOCAL_DATE_FORMAT} /> : null}</td>
                  <td>{stock.isin}</td>
                  <td>{stock.isDelisted ? 'true' : 'false'}</td>
                  <td>{stock.hasDividend ? 'true' : 'false'}</td>
                  <td>
                    <Translate contentKey={`litApp.StockType.${stock.type}`} />
                  </td>
                  <td>{stock.dividendYield}</td>
                  <td>
                    {stock.stockExchange ? <Link to={`stock-exchange/${stock.stockExchange.id}`}>{stock.stockExchange.id}</Link> : ''}
                  </td>
                  <td>{stock.company ? <Link to={`company/${stock.company.id}`}>{stock.company.id}</Link> : ''}</td>
                  <td>{stock.currency ? <Link to={`currency/${stock.currency.id}`}>{stock.currency.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${stock.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${stock.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${stock.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
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
              <Translate contentKey="litApp.watcherStock.home.notFound">No Stocks found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Stock;
