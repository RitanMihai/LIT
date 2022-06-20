import React, { useState, useEffect } from 'react';
import './feed.scss'

import InfiniteScroll from 'react-infinite-scroll-component';
import { Link, RouteComponentProps } from 'react-router-dom';
import { openFile, byteSize, Translate, translate, TextFormat, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { searchEntities, getEntities, reset } from './post.reducer';

import { IPost } from 'app/shared/model/social/post.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import Share from 'app/modules/home/share/share';
import RightBar from '../rightbar/rightbar';
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';
import FeedPost from './component/post';
import { getEntitiesBySocialUser } from 'app/entities/social/user-reaction/user-reaction.reducer';
import { getEntityByUserIs } from 'app/entities/social/social-user/social-user.reducer';
import "./feed.scss"
import { Box } from '@mui/material';

export const Feed = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();
  const account = useAppSelector(state => state.authentication.account);

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getSortState(props.location, ITEMS_PER_PAGE, 'id'), props.location.search)
  );

  const postList = useAppSelector(state => state.post.entities);
  const loading = useAppSelector(state => state.post.loading);
  const links = useAppSelector(state => state.post.links);

  const handleLoadMore = () => {
    if ((window as any).pageYOffset > 0) {
      setPaginationState({
        ...paginationState,
        activePage: paginationState.activePage + 1,
      });
    }
  };

  useEffect(() => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      })
    );
    dispatch(getEntityByUserIs(account.login));
    dispatch(getEntitiesBySocialUser(account.login));
  }, [])



  return (
    <Box>
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
    </Box>
  );
};

export default Feed;
