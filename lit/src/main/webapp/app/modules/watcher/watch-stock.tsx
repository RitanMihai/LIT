import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntityByTicker } from 'app/entities/watcher/stock/stock.reducer';
import { getEntitiesBySymbol } from 'app/entities/watcher/price-history/price-history.reducer';
import { getAllPredictions } from 'app/entities/watcher/prediction/prediction.reducer';
import SingleLineChart from './components/charts/single-line-chart';
import MultiLineChart from './components/charts/multi-line-chart';

export const WatchStock = (props: RouteComponentProps<{ name: string }>) => {
    const dispatch = useAppDispatch();
    /* TODO:Sort data */
    const stockEntity = useAppSelector(state => state.stock.entity);
    const priceHistoryList = useAppSelector(state => state.priceHistory.entities);
    const predictionList = useAppSelector(state => state.prediction.entities);

    const { match } = props;

    useEffect(() => {
        dispatch(getEntitiesBySymbol(props.match.params.name));
        dispatch(getEntityByTicker(props.match.params.name));
        dispatch(getAllPredictions(props.match.params.name));
    }, []);


    return (
        <div>
            <div>Watch {stockEntity.name}</div>
            {priceHistoryList.length === 0 ? (
                <div> NO DATA </div>
            ) : (
                predictionList.length === 0 ? (
                    <SingleLineChart
                        series={priceHistoryList}
                    />) : (
                    <MultiLineChart lenght={priceHistoryList.length}
                        stock={props.match.params.name}
                        seriesStock={priceHistoryList}
                        seriesPrediction={predictionList} />
                ))
            }
        </div>
    );
};

export default WatchStock;