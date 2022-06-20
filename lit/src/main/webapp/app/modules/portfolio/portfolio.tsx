import React, { useEffect, useState } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat, getSortState, JhiItemCount } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';


import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities, getEntitiesByUserAndPortfolio } from 'app/entities/portfolio/order/order.reducer';
import { getEntitiesOrderDetailsByUser } from 'app/entities/portfolio/order/order-details.reducer';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { SORT } from 'app/shared/util/pagination.constants';
import { Grid } from '@mui/material';
import { AllTranzactionsTable, PlusCard } from './components/componenets';

export const Portfolio = (props: RouteComponentProps<{ portfolio: string }>) => {
    const dispatch = useAppDispatch();
    const account = useAppSelector(state => state.authentication.account);

    useEffect(() => {
        dispatch(
            getEntitiesByUserAndPortfolio({
                query: `${account.login},${props.match.params.portfolio}`,
                page: paginationState.activePage - 1,
                size: paginationState.itemsPerPage,
                sort: `${paginationState.sort},${paginationState.order}`,
            })
        );
    }, []);

    /* ORDERS */
    const [search, setSearch] = useState('');
    const orderDetailsList = useAppSelector(state => state.orderDetails.entitiesInvested);
    const orderList = useAppSelector(state => state.order.entities);
    const totalItems = useAppSelector(state => state.order.totalItems);
    const loading = useAppSelector(state => state.order.loading);

    const [paginationState, setPaginationState] = useState(
        overridePaginationStateWithQueryParams(getSortState(props.location, 5, 'id'), props.location.search)
    );

    /* ORDERS METHODS */
    const sortEntities = () => {
        getAllEntities();
        const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
        if (props.location.search !== endURL) {
            props.history.push(`${props.location.pathname}${endURL}`);
        }
    };

    useEffect(() => {
        sortEntities();
    }, [paginationState.activePage, paginationState.order, paginationState.sort, search]);

    useEffect(() => {
        const params = new URLSearchParams(props.location.search);
        const page = params.get('page');
        const sort = params.get(SORT);
        if (page && sort) {
            const sortSplit = sort.split(',');
            setPaginationState({
                ...paginationState,
                activePage: +page,
                sort: sortSplit[0],
                order: sortSplit[1],
            });
        }
    }, [props.location.search]);

    const getAllEntities = () => {
        dispatch(
            getEntitiesByUserAndPortfolio({
                query: `${account.login},${props.match.params.portfolio}`,
                page: paginationState.activePage - 1,
                size: paginationState.itemsPerPage,
                sort: `${paginationState.sort},${paginationState.order}`,
            })
        );
    }
    const handlePagination = currentPage =>
        setPaginationState({
            ...paginationState,
            activePage: currentPage,
        });
    return (
        <Grid container spacing={2}>
            <Grid item xs={11}>
                <h2>Portfolio {props.match.params.portfolio}</h2>
            </Grid>
            <Grid item xs={1}>
                <PlusCard url={`/portfolios/order/${props.match.params.portfolio}/new-order`} />
            </Grid>
            <Grid item xs={12}>
                <AllTranzactionsTable
                    orderList={orderList}
                    totalItems={totalItems}
                    loading={loading}
                    paginationState={paginationState}
                    handlePagination={handlePagination}
                />
            </Grid>
        </Grid>
    );
}

export default Portfolio;