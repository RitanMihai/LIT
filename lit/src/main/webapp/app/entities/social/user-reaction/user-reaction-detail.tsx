import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './user-reaction.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const UserReactionDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const userReactionEntity = useAppSelector(state => state.userReaction.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="userReactionDetailsHeading">
          <Translate contentKey="litApp.socialUserReaction.detail.title">UserReaction</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{userReactionEntity.id}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="litApp.socialUserReaction.type">Type</Translate>
            </span>
          </dt>
          <dd>{userReactionEntity.type}</dd>
          <dt>
            <Translate contentKey="litApp.socialUserReaction.post">Post</Translate>
          </dt>
          <dd>{userReactionEntity.post ? userReactionEntity.post.id : ''}</dd>
          <dt>
            <Translate contentKey="litApp.socialUserReaction.socialUser">Social User</Translate>
          </dt>
          <dd>{userReactionEntity.socialUser ? userReactionEntity.socialUser.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/user-reaction" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/user-reaction/${userReactionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default UserReactionDetail;
