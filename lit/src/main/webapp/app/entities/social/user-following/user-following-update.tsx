import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { ISocialUser } from 'app/shared/model/social/social-user.model';
import { getEntities as getSocialUsers } from 'app/entities/social/social-user/social-user.reducer';
import { getEntity, updateEntity, createEntity, reset } from './user-following.reducer';
import { IUserFollowing } from 'app/shared/model/social/user-following.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const UserFollowingUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const socialUsers = useAppSelector(state => state.socialUser.entities);
  const userFollowingEntity = useAppSelector(state => state.userFollowing.entity);
  const loading = useAppSelector(state => state.userFollowing.loading);
  const updating = useAppSelector(state => state.userFollowing.updating);
  const updateSuccess = useAppSelector(state => state.userFollowing.updateSuccess);
  const handleClose = () => {
    props.history.push('/user-following');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getSocialUsers({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...userFollowingEntity,
      ...values,
      socialUsers: mapIdList(values.socialUsers),
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
          ...userFollowingEntity,
          socialUsers: userFollowingEntity?.socialUsers?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="litApp.socialUserFollowing.home.createOrEditLabel" data-cy="UserFollowingCreateUpdateHeading">
            <Translate contentKey="litApp.socialUserFollowing.home.createOrEditLabel">Create or edit a UserFollowing</Translate>
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
                  id="user-following-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('litApp.socialUserFollowing.stock')}
                id="user-following-stock"
                name="stock"
                data-cy="stock"
                type="text"
              />
              <ValidatedField
                label={translate('litApp.socialUserFollowing.socialUser')}
                id="user-following-socialUser"
                data-cy="socialUser"
                type="select"
                multiple
                name="socialUsers"
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/user-following" replace color="info">
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

export default UserFollowingUpdate;
