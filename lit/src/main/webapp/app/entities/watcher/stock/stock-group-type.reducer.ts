import axios from "axios";

import { createAsyncThunk, isFulfilled, isPending } from '@reduxjs/toolkit';
import { createEntityIndustrySectorSlice, createEntitySlice, EntityStateIndustrySector } from 'app/shared/reducers/reducer.utils';
import { IStockGroupTypes, defaultValue } from "app/shared/model/watcher/stock-group-type";

const initialState: EntityStateIndustrySector<IStockGroupTypes> = {
    loading: false,
    errorMessage: null,
    entitiesIndustry: [],
    entitiesSector: [],
    entities: [],
    entity: defaultValue,
    updating: false,
    updateSuccess: false,
};

const apiUrl = 'services/watcher/api/stocks';

export const getCountEntitesByTypes = createAsyncThunk(
    'stock/entites_by_sector', async (sector: string) => {
        const requestUrl = `${apiUrl}/types/${sector}`;
        return axios.get<IStockGroupTypes[]>(requestUrl);
    });


export const StockGroupTypeSlice = createEntityIndustrySectorSlice({
    name: 'stock-group',
    initialState,
    extraReducers(builder) {
        builder
            .addMatcher(isFulfilled(getCountEntitesByTypes), (state, action) => {
                const { data } = action.payload;
                switch (action.meta.arg) {
                    case "industry": return {
                        ...state,
                        loading: false,
                        entitiesIndustry: data,
                    };
                    case "sector": return {
                        ...state,
                        loading: false,
                        entitiesSector: data,
                    };
                    default:
                        return {
                            ...state,
                            loading: false,
                            entities: data,
                        };
                }
            })
            .addMatcher(isPending(getCountEntitesByTypes), state => {
                state.errorMessage = null;
                state.updateSuccess = false;
                state.loading = true;
            })
    },
});

export const { reset } = StockGroupTypeSlice.actions;

// Reducer
export default StockGroupTypeSlice.reducer;
