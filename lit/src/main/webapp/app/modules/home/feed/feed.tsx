import React, { useState, useEffect } from 'react';
import './feed.scss'

import InfiniteScroll from 'react-infinite-scroll-component';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Input, InputGroup, FormGroup, Form, Row, Col, Table, Card, CardImg, CardBody, CardTitle, CardText, CardLink } from 'reactstrap';
import { openFile, byteSize, Translate, translate, TextFormat, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { searchEntities, getEntities, reset, createEntity, updateEntity, getEntity } from './post.reducer';
import { getEntities as getSocialUsers } from 'app/entities/social/social-user/social-user.reducer';
import { getEntities as getTags } from 'app/entities/social/tag/tag.reducer';

import { IPost } from 'app/shared/model/social/post.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import Share from 'app/modules/home/share/share';
import RightBar from '../rightbar/rightbar';
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';
import { convertDateTimeToServer } from 'app/shared/util/date-utils';
import { Avatar, AvatarGroup, CardActions, CardContent, CardHeader, CardMedia, Collapse, IconButton, Popper, Typography } from '@mui/material';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import FavoriteIcon from '@mui/icons-material/Favorite';
import ShareIcon from '@mui/icons-material/Share';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { ReactionBarSelector, ReactionCounter, SlackSelector, PokemonSelector } from '@charkour/react-reactions';
import FeedPost from './component/post';
import "./feed.scss"

export const Feed = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();


  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getSortState(props.location, ITEMS_PER_PAGE, 'id'), props.location.search)
  );
  const [sorting, setSorting] = useState(false);

  const postEntity = useAppSelector(state => state.post.entity);
  const postList = useAppSelector(state => state.post.entities);
  const loading = useAppSelector(state => state.post.loading);
  const totalItems = useAppSelector(state => state.post.totalItems);
  const links = useAppSelector(state => state.post.links);
  const updateSuccess = useAppSelector(state => state.post.updateSuccess);

  const getAllEntities = () => {
    if (search) {
      dispatch(
        searchEntities({
          query: search,
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        })
      );
    } else {
      dispatch(
        getEntities({
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        })
      );
    }
  };

  const resetAll = () => {
    dispatch(reset());
    setPaginationState({
      ...paginationState,
      activePage: 1,
    });
    dispatch(getEntities({}));
  };

  useEffect(() => {
    resetAll();
  }, []);


  useEffect(() => {
    if (updateSuccess) {
      resetAll();
    }
  }, [updateSuccess]);

  useEffect(() => {
    getAllEntities();
  }, [paginationState.activePage]);

  const handleLoadMore = () => {
    if ((window as any).pageYOffset > 0) {
      setPaginationState({
        ...paginationState,
        activePage: paginationState.activePage + 1,
      });
    }
  };

  useEffect(() => {
    if (sorting) {
      getAllEntities();
      setSorting(false);
    }
  }, [sorting, search]);

  const handleSyncList = () => {
    resetAll();
  };



  return (
    <div className="feed">
      <div >
        <ErrorBoundaryRoute component={Share} />
        <InfiniteScroll className="feedWrapper"
          dataLength={postList ? postList.length : 0}
          next={handleLoadMore}
          hasMore={paginationState.activePage - 1 < links.next}
          loader={<div className="loader">Loading ...</div>}
        >
          {postList && postList.length > 0 ? (
            <div >
              {postList.map((post, i) => (
                <div key={`entity-${i}`} data-cy="entityPost" className="post">
                  <FeedPost post={post} />
                </div>
              ))}
            </div>
          ) : (
            !loading && (
              <div className="alert alert-warning">
                <Translate contentKey="litApp.socialPost.home.notFound">No Posts found</Translate>
              </div>
            )
          )}
        </InfiniteScroll>
      </div>
    </div>
  );
};

export default Feed;
