import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './report.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ReportDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const reportEntity = useAppSelector(state => state.report.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="reportDetailsHeading">
          <Translate contentKey="litApp.socialReport.detail.title">Report</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{reportEntity.id}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="litApp.socialReport.type">Type</Translate>
            </span>
          </dt>
          <dd>{reportEntity.type}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="litApp.socialReport.description">Description</Translate>
            </span>
          </dt>
          <dd>{reportEntity.description}</dd>
          <dt>
            <Translate contentKey="litApp.socialReport.post">Post</Translate>
          </dt>
          <dd>{reportEntity.post ? reportEntity.post.id : ''}</dd>
          <dt>
            <Translate contentKey="litApp.socialReport.comment">Comment</Translate>
          </dt>
          <dd>{reportEntity.comment ? reportEntity.comment.id : ''}</dd>
          <dt>
            <Translate contentKey="litApp.socialReport.reply">Reply</Translate>
          </dt>
          <dd>{reportEntity.reply ? reportEntity.reply.id : ''}</dd>
          <dt>
            <Translate contentKey="litApp.socialReport.socialUser">Social User</Translate>
          </dt>
          <dd>{reportEntity.socialUser ? reportEntity.socialUser.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/report" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/report/${reportEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ReportDetail;
