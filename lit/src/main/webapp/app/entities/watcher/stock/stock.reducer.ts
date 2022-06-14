import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { IQueryParams, createEntitySlice, EntityState, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { IStock, defaultValue } from 'app/shared/model/watcher/stock.model';
import { start } from 'repl';
import { endsWith } from 'lodash';

const initialState: EntityState<IStock> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

const apiUrl = 'services/watcher/api/stocks';
const apiSearchUrl = 'services/watcher/api/_search/stocks';

// Actions

export const searchEntities = createAsyncThunk('stock/search_entity', async ({ query, page, size, sort }: IQueryParams) => {
  const requestUrl = `${apiSearchUrl}?query=${query}`;
  return axios.get<IStock[]>(requestUrl);
});

export const getEntities = createAsyncThunk('stock/fetch_entity_list', async ({ page, size, sort }: IQueryParams) => {
  const requestUrl = `${apiUrl}?cacheBuster=${new Date().getTime()}`;
  return axios.get<IStock[]>(requestUrl);
});

export const getEntitiesBySector = createAsyncThunk(
  'stock/entites_by_sector', async ({ query, page, size, sort }: IQueryParams) => {
    const requestUrl = `${apiUrl}/sector/${query}?page=${page}&limit=${size}&sortBy=${sort}`;
    return axios.get<IStock[]>(requestUrl);
  });

export const getEntityByTicker = createAsyncThunk(
  'stock/ticker_fetch_entity',
  async (ticker: string) => {
    const requestUrl = `${apiUrl}/symbol/${ticker}`;
    return axios.get<IStock>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

export const getEntity = createAsyncThunk(
  'stock/fetch_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return axios.get<IStock>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

export const createEntity = createAsyncThunk(
  'stock/create_entity',
  async (entity: IStock, thunkAPI) => {
    const result = await axios.post<IStock>(apiUrl, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const createEntities = createAsyncThunk(
  'stock/create_entities',
  async (entities: Array<IStock>, thunkAPI) => {
    const requestUrl = `${apiUrl}/list`;
    const result = await axios.post<Array<IStock>>(requestUrl, entities);
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const updateEntity = createAsyncThunk(
  'stock/update_entity',
  async (entity: IStock, thunkAPI) => {
    const result = await axios.put<IStock>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const partialUpdateEntity = createAsyncThunk(
  'stock/partial_update_entity',
  async (entity: IStock, thunkAPI) => {
    const result = await axios.patch<IStock>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const deleteEntity = createAsyncThunk(
  'stock/delete_entity',
  async (id: string | number, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await axios.delete<IStock>(requestUrl);
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

// slice

export const StockSlice = createEntitySlice({
  name: 'stock',
  initialState,
  extraReducers(builder) {
    builder
      .addCase(getEntity.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload.data;
      })
      .addCase(getEntityByTicker.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload.data;
      })
      .addCase(deleteEntity.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
      })
      .addMatcher(isFulfilled(getEntities, getEntitiesBySector, searchEntities), (state, action) => {
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
      .addMatcher(isPending(getEntities, getEntitiesBySector, getEntity, getEntityByTicker, searchEntities), state => {
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

export const { reset } = StockSlice.actions;

// Reducer
export default StockSlice.reducer;
