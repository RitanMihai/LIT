import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './reply.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ReplyDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const replyEntity = useAppSelector(state => state.reply.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="replyDetailsHeading">
          <Translate contentKey="litApp.socialReply.detail.title">Reply</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{replyEntity.id}</dd>
          <dt>
            <span id="content">
              <Translate contentKey="litApp.socialReply.content">Content</Translate>
            </span>
          </dt>
          <dd>{replyEntity.content}</dd>
          <dt>
            <span id="date">
              <Translate contentKey="litApp.socialReply.date">Date</Translate>
            </span>
          </dt>
          <dd>{replyEntity.date ? <TextFormat value={replyEntity.date} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="language">
              <Translate contentKey="litApp.socialReply.language">Language</Translate>
            </span>
          </dt>
          <dd>{replyEntity.language}</dd>
          <dt>
            <Translate contentKey="litApp.socialReply.socialUser">Social User</Translate>
          </dt>
          <dd>{replyEntity.socialUser ? replyEntity.socialUser.id : ''}</dd>
          <dt>
            <Translate contentKey="litApp.socialReply.comment">Comment</Translate>
          </dt>
          <dd>{replyEntity.comment ? replyEntity.comment.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/reply" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/reply/${replyEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ReplyDetail;
