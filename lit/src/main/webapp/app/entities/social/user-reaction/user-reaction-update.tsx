import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IPost } from 'app/shared/model/social/post.model';
import { getEntities as getPosts } from 'app/entities/social/post/post.reducer';
import { ISocialUser } from 'app/shared/model/social/social-user.model';
import { getEntities as getSocialUsers } from 'app/entities/social/social-user/social-user.reducer';
import { getEntity, updateEntity, createEntity, reset } from './user-reaction.reducer';
import { IUserReaction } from 'app/shared/model/social/user-reaction.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { UserReactionType } from 'app/shared/model/enumerations/user-reaction-type.model';

export const UserReactionUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const posts = useAppSelector(state => state.post.entities);
  const socialUsers = useAppSelector(state => state.socialUser.entities);
  const userReactionEntity = useAppSelector(state => state.userReaction.entity);
  const loading = useAppSelector(state => state.userReaction.loading);
  const updating = useAppSelector(state => state.userReaction.updating);
  const updateSuccess = useAppSelector(state => state.userReaction.updateSuccess);
  const userReactionTypeValues = Object.keys(UserReactionType);
  const handleClose = () => {
    props.history.push('/user-reaction');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getPosts({}));
    dispatch(getSocialUsers({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    console.log("VALUES, ", values)
    const entity = {
      ...userReactionEntity,
      ...values,
      post: posts.find(it => it.id.toString() === values.post.toString()),
      socialUser: socialUsers.find(it => it.id.toString() === values.socialUser.toString()),
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
        type: 'LIT',
        ...userReactionEntity,
        post: userReactionEntity?.post?.id,
        socialUser: userReactionEntity?.socialUser?.id,
      };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="litApp.socialUserReaction.home.createOrEditLabel" data-cy="UserReactionCreateUpdateHeading">
            <Translate contentKey="litApp.socialUserReaction.home.createOrEditLabel">Create or edit a UserReaction</Translate>
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
                  id="user-reaction-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('litApp.socialUserReaction.type')}
                id="user-reaction-type"
                name="type"
                data-cy="type"
                type="select"
              >
                {userReactionTypeValues.map(userReactionType => (
                  <option value={userReactionType} key={userReactionType}>
                    {translate('litApp.UserReactionType.' + userReactionType)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="user-reaction-post"
                name="post"
                data-cy="post"
                label={translate('litApp.socialUserReaction.post')}
                type="select"
              >
                <option value="" key="0" />
                {posts
                  ? posts.map(otherEntity => (
                    <option value={otherEntity.id} key={otherEntity.id}>
                      {otherEntity.id}  {otherEntity.content}
                    </option>
                  ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="user-reaction-socialUser"
                name="socialUser"
                data-cy="socialUser"
                label={translate('litApp.socialUserReaction.socialUser')}
                type="select"
              >
                <option value="" key="0" />
                {socialUsers
                  ? socialUsers.map(otherEntity => (
                    <option value={otherEntity.id} key={otherEntity.id}>
                      {otherEntity.user}
                    </option>
                  ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/user-reaction" replace color="info">
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

export default UserReactionUpdate;
