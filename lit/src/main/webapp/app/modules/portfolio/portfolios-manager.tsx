import React, { PureComponent, useEffect, useState } from 'react';

import { getSortState, JhiItemCount, JhiPagination, Translate } from 'react-jhipster';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';
import ErrorBoundary from 'app/shared/error/error-boundary';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, AreaChart, Area } from 'recharts';
import Widget from './components/widget/widget';
import './portfolio.scss'
import { Card, CardContent, Grid, Paper, Typography, CardMedia, CardActions, Box } from '@mui/material';
import Carousel from 'react-multi-carousel';

import EllipsisText from "react-ellipsis-text";
import { NavItem, NavLink, NavbarBrand } from 'reactstrap';
import { NavLink as Link, RouteComponentProps } from 'react-router-dom';
import { AllTranzactionsTable, CustomPieChart, PlusCard, PortfolioCard } from './components/componenets';
import PortfolioLineChart from './components/portfolio-line-chart';
import { getEntitiesByPortfoliosDetailsByUser } from 'app/entities/portfolio/portfolio/portfolio-details.reducer';
import { getEntitiesOrderDetailsByUser, getEntitiesOrderDetailsRealByUser } from 'app/entities/portfolio/order/order-details.reducer';
import { getEntitiesByUser } from 'app/entities/portfolio/order/order.reducer';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import Skeleton from '@mui/material/Skeleton';
export const PortfoliosManager = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const account = useAppSelector(state => state.authentication.account);
  useEffect(() => {
    dispatch(getEntitiesByPortfoliosDetailsByUser(account.login));
    dispatch(getEntitiesOrderDetailsByUser({ query: account.login }));
    dispatch(getEntitiesOrderDetailsRealByUser({ query: account.login }));
    dispatch(
      getEntitiesByUser({
        query: account.login,
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      })
    );
  }, []);

  /* PORTFOLIO */
  const portfolioDetailsList = useAppSelector(state => state.portfolioDetails.entities);

  /* ORDERS */
  const [search, setSearch] = useState('');
  const orderDetailsList = useAppSelector(state => state.orderDetails.entitiesInvested);
  const orderDetailsRealList = useAppSelector(state => state.orderDetails.entitiesReal);
  const orderList = useAppSelector(state => state.order.entities);
  const totalItems = useAppSelector(state => state.order.totalItems);
  const loadingOrder = useAppSelector(state => state.order.loading);
  const loadingOrderDetails = useAppSelector(state => state.orderDetails.loading);

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
      getEntitiesByUser({
        query: account.login,
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

  /* CAROUSEL */
  const responsive = {
    superLargeDesktop: {
      // the naming can be any, depends on you.
      breakpoint: { max: 4000, min: 3000 },
      items: 5,
    },
    desktop: {
      breakpoint: { max: 3000, min: 1024 },
      items: portfolioDetailsList ? (portfolioDetailsList.length > 2 ? 3 : portfolioDetailsList.length) : 0,
    },
    tablet: {
      breakpoint: { max: 1024, min: 464 },
      items: 2
    },
    mobile: {
      breakpoint: { max: 464, min: 0 },
      items: 1
    }
  };

  return (
    <div>
      <h2>Portfolio Manager</h2>
      <Grid container spacing={2}>
        <Grid item xs={11}>
          <Carousel responsive={responsive} shouldResetAutoplay={false}>
            {portfolioDetailsList.map((it, key) => (
              <div key={key}>
                <PortfolioCard portfolio={it} />
              </div>
            ))}
          </Carousel>
        </Grid>
        <Grid item xs={1}>
          <PlusCard url="portfolios/new" />
        </Grid>
        <Grid item xs={12}>
          {orderDetailsList.length === 0 ? (
            <Grid item xs={12}>
              <PortfolioLineChart stock="" seriesInvested={orderDetailsList} seriesReal={orderDetailsRealList} />
            </Grid>
          ) : (
            <Grid container spacing={2}>
              <Grid item xs={3}>
                <CustomPieChart series={portfolioDetailsList} />
              </Grid>
              <Grid item xs={9}>
                <PortfolioLineChart stock="" seriesInvested={orderDetailsList} seriesReal={orderDetailsRealList} />
              </Grid>
            </Grid>

          )}
        </Grid>
        <Grid item xs={12}>
          <h5>All orders</h5>
          <AllTranzactionsTable
            orderList={orderList}
            totalItems={totalItems}
            loading={loadingOrder}
            paginationState={paginationState}
            handlePagination={handlePagination}
          />
        </Grid>
      </Grid >
    </div >
  );

};

export default PortfoliosManager;
