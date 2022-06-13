import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntityByTicker } from 'app/entities/watcher/stock/stock.reducer';
import BarSample from './components/BarSample';
import { getEntitiesBySymbol } from 'app/entities/watcher/price-history/price-history.reducer';

export const WatchStock = (props: RouteComponentProps<{ name: string }>) => {
    const dispatch = useAppDispatch();
    const stockEntity = useAppSelector(state => state.stock.entity);
    const priceHistoryList = useAppSelector(state => state.priceHistory.entities);

    useEffect(() => {
        dispatch(getEntitiesBySymbol(props.match.params.name));
        dispatch(getEntityByTicker(props.match.params.name));
    }, []);


    return (
        <div>
            <div>Watch {stockEntity.name}</div>
            <BarSample lenght={priceHistoryList.length} stock={props.match.params.name} series={priceHistoryList}></BarSample>
        </div>
    );
};

export default WatchStock;