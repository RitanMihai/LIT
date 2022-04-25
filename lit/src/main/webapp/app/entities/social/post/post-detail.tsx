import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, openFile, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './post.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const PostDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const postEntity = useAppSelector(state => state.post.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="postDetailsHeading">
          <Translate contentKey="litApp.socialPost.detail.title">Post</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{postEntity.id}</dd>
          <dt>
            <span id="content">
              <Translate contentKey="litApp.socialPost.content">Content</Translate>
            </span>
          </dt>
          <dd>{postEntity.content}</dd>
          <dt>
            <span id="image">
              <Translate contentKey="litApp.socialPost.image">Image</Translate>
            </span>
          </dt>
          <dd>
            {postEntity.image ? (
              <div>
                {postEntity.imageContentType ? (
                  <a onClick={openFile(postEntity.imageContentType, postEntity.image)}>
                    <img src={`data:${postEntity.imageContentType};base64,${postEntity.image}`} style={{ maxHeight: '30px' }} />
                  </a>
                ) : null}
                <span>
                  {postEntity.imageContentType}, {byteSize(postEntity.image)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="date">
              <Translate contentKey="litApp.socialPost.date">Date</Translate>
            </span>
          </dt>
          <dd>{postEntity.date ? <TextFormat value={postEntity.date} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="language">
              <Translate contentKey="litApp.socialPost.language">Language</Translate>
            </span>
          </dt>
          <dd>{postEntity.language}</dd>
          <dt>
            <span id="isPayedPromotion">
              <Translate contentKey="litApp.socialPost.isPayedPromotion">Is Payed Promotion</Translate>
            </span>
          </dt>
          <dd>{postEntity.isPayedPromotion ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="litApp.socialPost.socialUser">Social User</Translate>
          </dt>
          <dd>{postEntity.socialUser ? postEntity.socialUser.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/post" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/post/${postEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PostDetail;
