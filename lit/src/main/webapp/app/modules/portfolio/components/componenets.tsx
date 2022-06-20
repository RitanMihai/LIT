import * as React from 'react';

import { JhiItemCount, JhiPagination, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Card, CardContent, Grid, Paper, Typography, CardMedia, CardActions, Box, IconButton, Tooltip } from '@mui/material';
import Button, { ButtonProps } from '@mui/material/Button';
import EllipsisText from "react-ellipsis-text";
import { NavItem, NavLink, NavbarBrand, Table } from 'reactstrap';
import { NavLink as Link } from 'react-router-dom';
import AddIcon from '@mui/icons-material/Add';
import { orange } from '@mui/material/colors';
import { styled } from '@mui/material/styles';
import ReactApexChart from 'react-apexcharts';
import { ApexOptions } from 'apexcharts';

import { makeStyles } from '@mui/styles'
import Skeleton from '@mui/material/Skeleton';

export const PortfolioCard = (props) => {
    const useStyles = makeStyles({
        card: {
            margin: "5px",
            height: "150px",
            color: '#fff',
        },

        cardbg: {
            backgroundImage: `linear-gradient(45deg, #0fbaacaa, #0fbaacaa)`

        },

        cardPngBg: {
            backgroundImage: `linear-gradient(45deg, #0fbaac, #0fbaac)`,
            backgroundSize: '100%',
        }
    })

    const classes = useStyles()

    return (
        <Card className={`${classes.card} ${classes.cardPngBg}`}>
            <CardContent>
                <Typography gutterBottom variant="h5" component="div" color={'white'} fontWeight={"bold"}>
                    <EllipsisText text={props.portfolio.name} length={41} />
                </Typography>
                <Typography variant="h6" color={'white'}>
                    {props.portfolio.stockNumber} stocks
                </Typography>
            </CardContent>
            <Box display="flex" justifyContent="flex-end" alignItems="flex-end">
                <NavLink tag={Link} to={`portfolios/portfolio/${props.portfolio.name}`} className="text-center" >
                    <FontAwesomeIcon icon='arrow-up-right-from-square' color='white' />
                </NavLink>
            </Box>
        </Card>
    );
}


const CustomIconButton = styled(Button)<ButtonProps>(({ theme }) => ({
    color: theme.palette.getContrastText(orange[500]),
    backgroundColor: orange[500],
    '&:hover': {
        backgroundColor: orange[700],
    },
    width: '100%',
    height: '100%'
}));

export const PlusCard = props => (
    <Tooltip title="Add a new portfolio">
        <CustomIconButton color="primary" aria-label="upload picture" href={props.url}>
            <AddIcon fontSize="large" />
        </CustomIconButton>
    </Tooltip>
);

export const CustomPieChart = (props) => {

    const series = props.series?.map((item) => item.invested);

    const options: ApexOptions = {
        chart: {
            width: '300',
            type: 'pie',
        },
        labels: props.series?.map((item) => item.name),
        title: {
            text: "Portfolios distribution"
        },
        responsive: [{
            breakpoint: 480,
            options: {
                chart: {
                    width: 200
                },
                legend: {
                    position: 'bottom'
                }
            }
        }]
    };


    return (
        <Card>
            <CardContent style={{ height: "300px", width: "450px" }}>
                <ReactApexChart options={options} series={series} type="pie" height={'100%'} />
            </CardContent>
        </Card>
    );

}

export const AllTranzactionsTable = (props) => {

    return (
        <Card>
            <CardContent>
                <div className="table-responsive">
                    {props.orderList && props.orderList.length > 0 ? (
                        <Table responsive>
                            <thead>
                                <tr>
                                    <th>Stock       </th>
                                    <th>Date        </th>
                                    <th>Nr. Shares  </th>
                                    <th>Share Price </th>
                                    <th>Total       </th>
                                    <th>Buy/Sell    </th>
                                    <th>Portfolio   </th>
                                    <th>Movement    </th>
                                </tr>
                            </thead>
                            <tbody>
                                {props.orderList.map((order, i) => (
                                    <tr key={`entity-${i}`} data-cy="entityTable">
                                        <td style={{ display: 'flex' }}>
                                            <Link to={`../watcher/${order.stockInfo.ticker}`}>{order.stockInfo.ticker}</Link>
                                        </td>
                                        <td>
                                            {order.subbmitedDate}
                                        </td>
                                        <td>
                                            {order.quantity}
                                        </td>
                                        <td>
                                            {order.sharePrice}
                                        </td>
                                        <td>
                                            {order.total}
                                        </td>
                                        <td>
                                            {order.type}
                                        </td>
                                        <td>
                                            {order.portfolio.name}
                                        </td>
                                        <td>
                                            IDK MAN
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </Table>) : (!props.loading && (
                            <div className="alert alert-warning">
                                <Translate contentKey="litApp.portfolioOrder.home.notFound">No Orders found</Translate>
                            </div>
                        )
                    )}
                </div>
                {props.totalItems ? (
                    <div className={props.orderList && props.orderList.length > 0 ? '' : 'd-none'}>
                        <div className="justify-content-center d-flex">
                            <JhiItemCount page={props.paginationState.activePage} total={props.totalItems} itemsPerPage={props.paginationState.itemsPerPage} i18nEnabled />
                        </div>
                        <div className="justify-content-center d-flex">
                            <JhiPagination
                                activePage={props.paginationState.activePage}
                                onSelect={props.handlePagination}
                                maxButtons={5}
                                itemsPerPage={props.paginationState.itemsPerPage}
                                totalItems={props.totalItems}
                            />
                        </div>
                    </div>
                ) : (
                    ''
                )}
            </CardContent>
        </Card>
    );
}
