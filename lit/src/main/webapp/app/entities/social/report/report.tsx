import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Input, InputGroup, FormGroup, Form, Row, Col, Table } from 'reactstrap';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { searchEntities, getEntities } from './report.reducer';
import { IReport } from 'app/shared/model/social/report.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const Report = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const [search, setSearch] = useState('');

  const reportList = useAppSelector(state => state.report.entities);
  const loading = useAppSelector(state => state.report.loading);

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
      <h2 id="report-heading" data-cy="ReportHeading">
        <Translate contentKey="litApp.socialReport.home.title">Reports</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="litApp.socialReport.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="litApp.socialReport.home.createLabel">Create new Report</Translate>
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
                  placeholder={translate('litApp.socialReport.home.search')}
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
        {reportList && reportList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="litApp.socialReport.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.socialReport.type">Type</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.socialReport.description">Description</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.socialReport.post">Post</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.socialReport.comment">Comment</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.socialReport.reply">Reply</Translate>
                </th>
                <th>
                  <Translate contentKey="litApp.socialReport.socialUser">Social User</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {reportList.map((report, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${report.id}`} color="link" size="sm">
                      {report.id}
                    </Button>
                  </td>
                  <td>
                    <Translate contentKey={`litApp.RportType.${report.type}`} />
                  </td>
                  <td>{report.description}</td>
                  <td>{report.post ? <Link to={`post/${report.post.id}`}>{report.post.id}</Link> : ''}</td>
                  <td>{report.comment ? <Link to={`comment/${report.comment.id}`}>{report.comment.id}</Link> : ''}</td>
                  <td>{report.reply ? <Link to={`reply/${report.reply.id}`}>{report.reply.id}</Link> : ''}</td>
                  <td>{report.socialUser ? <Link to={`social-user/${report.socialUser.id}`}>{report.socialUser.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${report.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${report.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${report.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
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
              <Translate contentKey="litApp.socialReport.home.notFound">No Reports found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Report;
