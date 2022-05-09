import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { LanguageType } from 'app/shared/model/enumerations/language-type.model';
import { getEntities as getSocialUsers } from 'app/entities/social/social-user/social-user.reducer';
import { getEntities as getTags } from 'app/entities/social/tag/tag.reducer';
import React, { useEffect, useRef, useState } from 'react';
import { Translate, translate, ValidatedBlobField, ValidatedField, ValidatedForm } from 'react-jhipster';
import { RouteComponentProps } from 'react-router-dom';
import { createEntity, getEntity, updateEntity } from '../feed/post.reducer';
import './share.scss';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { Button, Col, Form, FormGroup, Input, Label } from 'reactstrap';
import { useForm } from 'react-hook-form';

const Share =(props: RouteComponentProps<{ id: string }>) => {
  /* Current user  */
  const account = useAppSelector(state => state.authentication.account);

  const dispatch = useAppDispatch();

  const socialUsers = useAppSelector(state => state.socialUser.entities);
  const tags = useAppSelector(state => state.tag.entities);
  const postEntity = useAppSelector(state => state.post.entity);
  const post = useAppSelector(state => state.post);
  const loading = useAppSelector(state => state.post.loading);
  const updating = useAppSelector(state => state.post.updating);
  const updateSuccess = useAppSelector(state => state.post.updateSuccess);
  const languageTypeValues = Object.keys(LanguageType);

  const [isImageSelected, setIsImageSelected] = useState(false);

  const imageHandler = () =>{
    setIsImageSelected(!isImageSelected)
  }

  const {
    handleSubmit,
    register,
    setValue,
    reset: resetForm,
    formState: { errors },
  } = useForm({ mode: 'onBlur' });

  useEffect(() => {
    resetForm(postEntity);
  }, [postEntity]);


  const handleClose = () => {
    props.history.push('/');
  };

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  useEffect(() => {
    dispatch(getSocialUsers({}));
    dispatch(getTags({}));
  }, []);

  const saveEntity = values => {

    values.date = convertDateTimeToServer(values.date);
    values.language = 'ENG';
    values.isPayedPromotion = false;

    const entity = {
      ...postEntity,
      ...values,
      socialUser: socialUsers.find(it => it.user.toString() === account.login),
    };
    
    dispatch(createEntity(entity));

  };

  const defaultValues = () => {return {date: displayDefaultDateTime()}};

  return(
    <div className="share">
      <div className="shareWrapper">

        <Form onSubmit={handleSubmit(saveEntity)}>
          <FormGroup className="shareTop"> 
            <img className="shareProfileImg" src="https://picsum.photos/200" alt="" />     

            <ValidatedField
                style={{border: "none"}}
                register={register}
                placeholder= {"What's in your mind " + account.login + " ?"}
                id="post-content"
                name="content"
                data-cy="content"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
           
                className="shareInput"
                />
          </FormGroup >
          {
            isImageSelected ?  (
          <FormGroup>
            <ValidatedBlobField
                setValue={setValue}
                register={register}
                style= {{display:"none"}} 
                id="post-image"
                name="image"
                data-cy="image"
                isImage
                accept="image/*"
                className="blobField"
            />
          </FormGroup>
          ) : null 
          }
          <hr className="shareHr"/>
          
          <FormGroup  className="shareBottom" onClick={imageHandler}>
          <label htmlFor="post-image" className="shareOption">
            <FontAwesomeIcon icon="photo-film" size="2x" color='tomato' className="shareIcon" />
            <span className="shareOptionText">Photo or Video</span>
          </label>
       
          <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating} className="shareButton">
            <FontAwesomeIcon icon="paper-plane" />
            &nbsp; Publish
          </Button>

          </FormGroup >
        </Form>
      </div>
    </div>
  )
}

export default Share;