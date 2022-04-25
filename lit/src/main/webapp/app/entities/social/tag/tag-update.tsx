import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IPost } from 'app/shared/model/social/post.model';
import { getEntities as getPosts } from 'app/entities/social/post/post.reducer';
import { IComment } from 'app/shared/model/social/comment.model';
import { getEntities as getComments } from 'app/entities/social/comment/comment.reducer';
import { IReply } from 'app/shared/model/social/reply.model';
import { getEntities as getReplies } from 'app/entities/social/reply/reply.reducer';
import { getEntity, updateEntity, createEntity, reset } from './tag.reducer';
import { ITag } from 'app/shared/model/social/tag.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const TagUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const posts = useAppSelector(state => state.post.entities);
  const comments = useAppSelector(state => state.comment.entities);
  const replies = useAppSelector(state => state.reply.entities);
  const tagEntity = useAppSelector(state => state.tag.entity);
  const loading = useAppSelector(state => state.tag.loading);
  const updating = useAppSelector(state => state.tag.updating);
  const updateSuccess = useAppSelector(state => state.tag.updateSuccess);
  const handleClose = () => {
    props.history.push('/tag');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getPosts({}));
    dispatch(getComments({}));
    dispatch(getReplies({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...tagEntity,
      ...values,
      posts: mapIdList(values.posts),
      comments: mapIdList(values.comments),
      replies: mapIdList(values.replies),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...tagEntity,
          posts: tagEntity?.posts?.map(e => e.id.toString()),
          comments: tagEntity?.comments?.map(e => e.id.toString()),
          replies: tagEntity?.replies?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="litApp.socialTag.home.createOrEditLabel" data-cy="TagCreateUpdateHeading">
            <Translate contentKey="litApp.socialTag.home.createOrEditLabel">Create or edit a Tag</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="tag-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('litApp.socialTag.stockName')}
                id="tag-stockName"
                name="stockName"
                data-cy="stockName"
                type="text"
              />
              <ValidatedField label={translate('litApp.socialTag.ticker')} id="tag-ticker" name="ticker" data-cy="ticker" type="text" />
              <ValidatedField label={translate('litApp.socialTag.post')} id="tag-post" data-cy="post" type="select" multiple name="posts">
                <option value="" key="0" />
                {posts
                  ? posts.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                label={translate('litApp.socialTag.comment')}
                id="tag-comment"
                data-cy="comment"
                type="select"
                multiple
                name="comments"
              >
                <option value="" key="0" />
                {comments
                  ? comments.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                label={translate('litApp.socialTag.reply')}
                id="tag-reply"
                data-cy="reply"
                type="select"
                multiple
                name="replies"
              >
                <option value="" key="0" />
                {replies
                  ? replies.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/tag" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default TagUpdate;
