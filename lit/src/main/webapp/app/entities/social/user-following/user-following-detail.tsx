import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './user-following.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const UserFollowingDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const userFollowingEntity = useAppSelector(state => state.userFollowing.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="userFollowingDetailsHeading">
          <Translate contentKey="litApp.socialUserFollowing.detail.title">UserFollowing</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{userFollowingEntity.socialUsers}</dd>
          <dt>
            <span id="stock">
              <Translate contentKey="litApp.socialUserFollowing.stock">Stock</Translate>
            </span>
          </dt>
          <dd>{userFollowingEntity.stock}</dd>
          <dt>
            <Translate contentKey="litApp.socialUserFollowing.socialUser">Social User</Translate>
          </dt>
          <dd>
            {userFollowingEntity.socialUsers
              ? userFollowingEntity.socialUsers.map((val, i) => (
                <span key={val.id}>
                  <a>{val.id}</a>
                  {userFollowingEntity.socialUsers && i === userFollowingEntity.socialUsers.length - 1 ? '' : ', '}
                </span>
              ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/user-following" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/user-following/${userFollowingEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default UserFollowingDetail;
