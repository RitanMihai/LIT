import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { ISocialUser } from 'app/shared/model/social/social-user.model';
import { getEntities as getSocialUsers } from 'app/entities/social/social-user/social-user.reducer';
import { IComment } from 'app/shared/model/social/comment.model';
import { getEntities as getComments } from 'app/entities/social/comment/comment.reducer';
import { ITag } from 'app/shared/model/social/tag.model';
import { getEntities as getTags } from 'app/entities/social/tag/tag.reducer';
import { getEntity, updateEntity, createEntity, reset } from './reply.reducer';
import { IReply } from 'app/shared/model/social/reply.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { LanguageType } from 'app/shared/model/enumerations/language-type.model';

export const ReplyUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const socialUsers = useAppSelector(state => state.socialUser.entities);
  const comments = useAppSelector(state => state.comment.entities);
  const tags = useAppSelector(state => state.tag.entities);
  const replyEntity = useAppSelector(state => state.reply.entity);
  const loading = useAppSelector(state => state.reply.loading);
  const updating = useAppSelector(state => state.reply.updating);
  const updateSuccess = useAppSelector(state => state.reply.updateSuccess);
  const languageTypeValues = Object.keys(LanguageType);
  const handleClose = () => {
    props.history.push('/reply');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getSocialUsers({}));
    dispatch(getComments({}));
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
      ...replyEntity,
      ...values,
      socialUser: socialUsers.find(it => it.id.toString() === values.socialUser.toString()),
      comment: comments.find(it => it.id.toString() === values.comment.toString()),
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
          ...replyEntity,
          date: convertDateTimeFromServer(replyEntity.date),
          socialUser: replyEntity?.socialUser?.id,
          comment: replyEntity?.comment?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="litApp.socialReply.home.createOrEditLabel" data-cy="ReplyCreateUpdateHeading">
            <Translate contentKey="litApp.socialReply.home.createOrEditLabel">Create or edit a Reply</Translate>
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
                  id="reply-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('litApp.socialReply.content')}
                id="reply-content"
                name="content"
                data-cy="content"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('litApp.socialReply.date')}
                id="reply-date"
                name="date"
                data-cy="date"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('litApp.socialReply.language')}
                id="reply-language"
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
                id="reply-socialUser"
                name="socialUser"
                data-cy="socialUser"
                label={translate('litApp.socialReply.socialUser')}
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
              <ValidatedField
                id="reply-comment"
                name="comment"
                data-cy="comment"
                label={translate('litApp.socialReply.comment')}
                type="select"
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/reply" replace color="info">
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

export default ReplyUpdate;
