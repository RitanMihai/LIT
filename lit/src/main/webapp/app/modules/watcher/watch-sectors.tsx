import React, { useEffect } from 'react';
import { Link, NavLink, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Table } from 'reactstrap';
import { Translate, openFile, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntitiesBySector } from 'app/entities/watcher/stock/stock.reducer';
import { Card, CardContent, Typography } from '@mui/material';
import { min } from 'lodash';
import { DefaultIcon } from './components/icon/icon';

export const WatchSectors = (props: RouteComponentProps<{ sector: string }>) => {
    const dispatch = useAppDispatch();
    const stockList = useAppSelector(state => state.stock.entities)['stocks'];

    useEffect(() => {
        dispatch(getEntitiesBySector({ query: props.match.params.sector, page: 0, size: 10, sort: "marketCap" }));
    }, []);

    return (
        <Card>
            <CardContent>
                {stockList && stockList.length > 0 ? (
                    <Table responsive>
                        <thead>
                            <tr>
                                <th>
                                    Stock
                                </th>
                                <th>
                                    Market Cap
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            {stockList.map((stock, i) => (
                                <tr key={`entity-${i}`}>
                                    <td style={{ display: 'flex' }}>
                                        {stock.imageContentType ? (
                                            <div>
                                                <Link to={`../${stock.ticker}`}>
                                                    <img className="default-icon" src={`data:${stock.imageContentType};base64,${stock.image}`} />
                                                </Link>
                                            </div>
                                        ) : <DefaultIcon letter={stock.ticker[0]} />}

                                        <div >
                                            <Link to={`../${stock.ticker}`}>{stock.ticker}</Link>
                                            <div>{stock.name}</div>
                                        </div>

                                    </td>
                                    <td>
                                        {stock.marketCap}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </Table>
                ) : null}
            </CardContent>
        </Card>
    );
};

export default WatchSectors;