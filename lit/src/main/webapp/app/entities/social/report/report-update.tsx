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
import { ISocialUser } from 'app/shared/model/social/social-user.model';
import { getEntities as getSocialUsers } from 'app/entities/social/social-user/social-user.reducer';
import { getEntity, updateEntity, createEntity, reset } from './report.reducer';
import { IReport } from 'app/shared/model/social/report.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { RportType } from 'app/shared/model/enumerations/rport-type.model';

export const ReportUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const posts = useAppSelector(state => state.post.entities);
  const comments = useAppSelector(state => state.comment.entities);
  const replies = useAppSelector(state => state.reply.entities);
  const socialUsers = useAppSelector(state => state.socialUser.entities);
  const reportEntity = useAppSelector(state => state.report.entity);
  const loading = useAppSelector(state => state.report.loading);
  const updating = useAppSelector(state => state.report.updating);
  const updateSuccess = useAppSelector(state => state.report.updateSuccess);
  const rportTypeValues = Object.keys(RportType);
  const handleClose = () => {
    props.history.push('/report');
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
    dispatch(getSocialUsers({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...reportEntity,
      ...values,
      post: posts.find(it => it.id.toString() === values.post.toString()),
      comment: comments.find(it => it.id.toString() === values.comment.toString()),
      reply: replies.find(it => it.id.toString() === values.reply.toString()),
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
          type: 'SPAM',
          ...reportEntity,
          post: reportEntity?.post?.id,
          comment: reportEntity?.comment?.id,
          reply: reportEntity?.reply?.id,
          socialUser: reportEntity?.socialUser?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="litApp.socialReport.home.createOrEditLabel" data-cy="ReportCreateUpdateHeading">
            <Translate contentKey="litApp.socialReport.home.createOrEditLabel">Create or edit a Report</Translate>
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
                  id="report-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField label={translate('litApp.socialReport.type')} id="report-type" name="type" data-cy="type" type="select">
                {rportTypeValues.map(rportType => (
                  <option value={rportType} key={rportType}>
                    {translate('litApp.RportType.' + rportType)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('litApp.socialReport.description')}
                id="report-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField id="report-post" name="post" data-cy="post" label={translate('litApp.socialReport.post')} type="select">
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
                id="report-comment"
                name="comment"
                data-cy="comment"
                label={translate('litApp.socialReport.comment')}
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
              <ValidatedField id="report-reply" name="reply" data-cy="reply" label={translate('litApp.socialReport.reply')} type="select">
                <option value="" key="0" />
                {replies
                  ? replies.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="report-socialUser"
                name="socialUser"
                data-cy="socialUser"
                label={translate('litApp.socialReport.socialUser')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/report" replace color="info">
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

export default ReportUpdate;
