import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';

import { createEntityDetailsTimeSlice, createEntitySlice, EntityDetailsTimeState, EntityState, IQueryParams } from 'app/shared/reducers/reducer.utils';
import { IOrderDetails, defaultValue } from 'app/shared/model/portfolio/order-details.model';

const initialState: EntityDetailsTimeState<IOrderDetails> = {
    loading: false,
    errorMessage: null,
    entitiesInvested: [],
    entitiesReal: [],
    entity: defaultValue,
    updating: false,
    totalItems: 0,
    updateSuccess: false,
};

const apiUrl = 'services/portfolio/api/orders-details';
const apiSearchUrl = 'services/portfolio/api/_search/orders';

// Actions
export const getEntitiesOrderDetailsByUser = createAsyncThunk('order/fetch_entity_details_list', async ({ query, page, size, sort }: IQueryParams) => {
    const requestUrl = `${apiUrl}/user/${query}/${sort ? `?page=${page}&size=${size}&sort=${sort}&` : '?'}cacheBuster=${new Date().getTime()}`;
    return axios.get<IOrderDetails[]>(requestUrl);
});

export const getEntitiesOrderDetailsRealByUser = createAsyncThunk('order/fetch_entity_details_real_list', async ({ query, page, size, sort }: IQueryParams) => {
    const requestUrl = `${apiUrl}/user/${query}/pl`;
    return axios.get<IOrderDetails[]>(requestUrl);
});

// slice

export const OrderDetailsSlice = createEntityDetailsTimeSlice({
    name: 'orderDetails',
    initialState,
    extraReducers(builder) {
        builder
            .addMatcher(isFulfilled(getEntitiesOrderDetailsByUser), (state, action) => {
                const { data, headers } = action.payload;

                return {
                    ...state,
                    loading: false,
                    entitiesInvested: data,
                    totalItems: parseInt(headers['x-total-count'], 10),
                };
            })
            .addMatcher(isFulfilled(getEntitiesOrderDetailsRealByUser), (state, action) => {
                const { data, headers } = action.payload;

                return {
                    ...state,
                    loading: false,
                    entitiesReal: data,
                    totalItems: parseInt(headers['x-total-count'], 10),
                };
            })
            .addMatcher(isPending(getEntitiesOrderDetailsByUser, getEntitiesOrderDetailsRealByUser), state => {
                state.errorMessage = null;
                state.updateSuccess = false;
                state.loading = true;
            })
    },
});

export const { reset } = OrderDetailsSlice.actions;

// Reducer
export default OrderDetailsSlice.reducer;
