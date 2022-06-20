import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { IQueryParams, createEntitySlice, EntityState, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { IPortfolioDetails, defaultValue } from 'app/shared/model/portfolio/portfolio-details.model';

const initialState: EntityState<IPortfolioDetails> = {
    loading: false,
    errorMessage: null,
    entities: [],
    entity: defaultValue,
    updating: false,
    totalItems: 0,
    updateSuccess: false,
};

const apiUrl = 'services/portfolio/api/portfolios';
const apiSearchUrl = 'services/portfolio/api/_search/portfolios';

// Actions
export const getEntitiesByPortfoliosDetailsByUser = createAsyncThunk('portfolio/fetch_entity_list', async (user: string) => {
    const requestUrl = `${apiUrl}/details/${user}`;
    return axios.get<IPortfolioDetails[]>(requestUrl);
});

// slice

export const PortfolioSlice = createEntitySlice({
    name: 'portfolio-details',
    initialState,
    extraReducers(builder) {
        builder
            .addMatcher(isFulfilled(getEntitiesByPortfoliosDetailsByUser), (state, action) => {
                const { data } = action.payload;

                return {
                    ...state,
                    loading: false,
                    entities: data
                };
            })

            .addMatcher(isPending(getEntitiesByPortfoliosDetailsByUser), state => {
                state.errorMessage = null;
                state.updateSuccess = false;
                state.loading = true;
            })
    },
});

export const { reset } = PortfolioSlice.actions;

// Reducer
export default PortfolioSlice.reducer;
