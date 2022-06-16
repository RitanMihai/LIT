import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';
import { loadMoreDataWhenScrolled, parseHeaderForLinks } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { IQueryParams, createEntitySlice, EntityState, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { IUserReaction, defaultValue } from 'app/shared/model/social/user-reaction.model';

const initialState: EntityState<IUserReaction> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

const apiUrl = 'services/social/api/user-reactions';
const apiSearchUrl = 'services/social/api/_search/user-reactions';

// Actions

export const searchEntities = createAsyncThunk('userReaction/search_entity', async ({ query, page, size, sort }: IQueryParams) => {
  const requestUrl = `${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`;
  return axios.get<IUserReaction[]>(requestUrl);
});

export const getEntities = createAsyncThunk('userReaction/fetch_entity_list', async ({ page, size, sort }: IQueryParams) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}&` : '?'}cacheBuster=${new Date().getTime()}`;
  return axios.get<IUserReaction[]>(requestUrl);
});

export const getEntitiesBySocialUser = createAsyncThunk('userReaction/fetch_entity_list_by_social_user', async (user: string) => {
  const requestUrl = `${apiUrl}/user/${user}`;
  return axios.get<IUserReaction[]>(requestUrl);
});

export const getEntity = createAsyncThunk(
  'userReaction/fetch_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return axios.get<IUserReaction>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

export const getEntityByPostIdAndSocialUser = createAsyncThunk(
  'userReaction/fetch_entity_by_post_id_and_social_user',
  async (entity: IUserReaction) => {
    console.log("Entitate primita ", entity)
    const requestUrl = `${apiUrl}/specific?postId=${entity.post.id}&user=${entity.socialUser}`;
    return axios.get<IUserReaction>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

export const createEntity = createAsyncThunk(
  'userReaction/create_entity',
  async (entity: IUserReaction, thunkAPI) => {
    return axios.post<IUserReaction>(apiUrl, cleanEntity(entity));
  },
  { serializeError: serializeAxiosError }
);

export const updateEntity = createAsyncThunk(
  'userReaction/update_entity',
  async (entity: IUserReaction, thunkAPI) => {
    return axios.put<IUserReaction>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
  },
  { serializeError: serializeAxiosError }
);

export const partialUpdateEntity = createAsyncThunk(
  'userReaction/partial_update_entity',
  async (entity: IUserReaction, thunkAPI) => {
    return axios.patch<IUserReaction>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
  },
  { serializeError: serializeAxiosError }
);

export const deleteEntity = createAsyncThunk(
  'userReaction/delete_entity',
  async (id: string | number, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}`;
    return await axios.delete<IUserReaction>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

// slice

export const UserReactionSlice = createEntitySlice({
  name: 'userReaction',
  initialState,
  extraReducers(builder) {
    builder
      .addCase(getEntity.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload.data;
      })
      .addCase(getEntityByPostIdAndSocialUser.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload.data;
      })
      .addCase(deleteEntity.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
      })
      .addMatcher(isFulfilled(getEntities, searchEntities), (state, action) => {
        const { data, headers } = action.payload;
        const links = parseHeaderForLinks(headers.link);

        return {
          ...state,
          loading: false,
          links,
          entities: loadMoreDataWhenScrolled(state.entities, data, links),
          totalItems: parseInt(headers['x-total-count'], 10),
        };
      })
      .addMatcher(isFulfilled(getEntitiesBySocialUser), (state, action) => {
        const { data } = action.payload;

        return {
          ...state,
          loading: false,
          entities: data,
        };
      })
      .addMatcher(isFulfilled(createEntity, updateEntity, partialUpdateEntity), (state, action) => {
        state.updating = false;
        state.loading = false;
        state.updateSuccess = true;
        state.entity = action.payload.data;
      })
      .addMatcher(isPending(getEntitiesBySocialUser, getEntities, getEntityByPostIdAndSocialUser, getEntity, searchEntities), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.loading = true;
      })
      .addMatcher(isPending(createEntity, updateEntity, partialUpdateEntity, deleteEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;
      });
  },
});

export const { reset } = UserReactionSlice.actions;

// Reducer
export default UserReactionSlice.reducer;
