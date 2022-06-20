import axios from "axios";

import { IPrediction, defaultValue } from "app/shared/model/prediction/prediction.model";
import { createAsyncThunk, isFulfilled } from '@reduxjs/toolkit';
import { createEntitySlice, EntityState, serializeAxiosError } from 'app/shared/reducers/reducer.utils';

const initialState: EntityState<IPrediction> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};


const apiUrl = 'http://localhost:8090/mongo/predictions/';

export const getAllPredictions = createAsyncThunk(
  'prediction/fetch_entity_list',
  async (symbol: string) => {
    const requestUrl = `${apiUrl}${symbol}`;
    return axios.get<IPrediction[]>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

// slice
export const PredictionSlice = createEntitySlice({
  name: 'prediction',
  initialState,
  extraReducers(builder) {
    builder
      .addMatcher(isFulfilled(getAllPredictions), (state, action) => {
        const { data } = action.payload;
        return {
          ...state,
          loading: false,
          entities: data,
        };
      })
  },
});

export const { reset } = PredictionSlice.actions;

// Reducer
export default PredictionSlice.reducer;
