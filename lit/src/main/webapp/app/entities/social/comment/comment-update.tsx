import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { ISocialUser } from 'app/shared/model/social/social-user.model';
import { getEntities as getSocialUsers } from 'app/entities/social/social-user/social-user.reducer';
import { IPost } from 'app/shared/model/social/post.model';
import { getEntities as getPosts } from 'app/entities/social/post/post.reducer';
import { ITag } from 'app/shared/model/social/tag.model';
import { getEntities as getTags } from 'app/entities/social/tag/tag.reducer';
import { getEntity, updateEntity, createEntity, reset } from './comment.reducer';
import { IComment } from 'app/shared/model/social/comment.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { LanguageType } from 'app/shared/model/enumerations/language-type.model';

export const CommentUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const socialUsers = useAppSelector(state => state.socialUser.entities);
  const posts = useAppSelector(state => state.post.entities);
  const tags = useAppSelector(state => state.tag.entities);
  const commentEntity = useAppSelector(state => state.comment.entity);
  const loading = useAppSelector(state => state.comment.loading);
  const updating = useAppSelector(state => state.comment.updating);
  const updateSuccess = useAppSelector(state => state.comment.updateSuccess);
  const languageTypeValues = Object.keys(LanguageType);
  const handleClose = () => {
    props.history.push('/comment');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getSocialUsers({}));
    dispatch(getPosts({}));
    dispatch(getTags({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.date = convertDateTimeToServer(values.date);

    const entity = {
      ...commentEntity,
      ...values,
      socialUser: socialUsers.find(it => it.id.toString() === values.socialUser.toString()),
      post: posts.find(it => it.id.toString() === values.post.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          date: displayDefaultDateTime(),
        }
      : {
          language: 'ENG',
          ...commentEntity,
          date: convertDateTimeFromServer(commentEntity.date),
          socialUser: commentEntity?.socialUser?.id,
          post: commentEntity?.post?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="litApp.socialComment.home.createOrEditLabel" data-cy="CommentCreateUpdateHeading">
            <Translate contentKey="litApp.socialComment.home.createOrEditLabel">Create or edit a Comment</Translate>
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
                  id="comment-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('litApp.socialComment.content')}
                id="comment-content"
                name="content"
                data-cy="content"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('litApp.socialComment.date')}
                id="comment-date"
                name="date"
                data-cy="date"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('litApp.socialComment.language')}
                id="comment-language"
                name="language"
                data-cy="language"
                type="select"
              >
                {languageTypeValues.map(languageType => (
                  <option value={languageType} key={languageType}>
                    {translate('litApp.LanguageType.' + languageType)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="comment-socialUser"
                name="socialUser"
                data-cy="socialUser"
                label={translate('litApp.socialComment.socialUser')}
                type="select"
              >
                <option value="" key="0" />
                {socialUsers
                  ? socialUsers.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="comment-post" name="post" data-cy="post" label={translate('litApp.socialComment.post')} type="select">
                <option value="" key="0" />
                {posts
                  ? posts.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/comment" replace color="info">
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

export default CommentUpdate;
