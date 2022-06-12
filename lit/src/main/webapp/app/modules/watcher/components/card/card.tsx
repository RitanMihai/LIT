import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Card, CardMedia, CardActions, CardContent, Typography, Box } from '@mui/material';
import { makeStyles } from '@mui/styles'
import EllipsisText from "react-ellipsis-text";

import React, { useState } from 'react';
import { NavItem, NavLink, NavbarBrand, Row, Col } from 'reactstrap';
import { NavLink as Link } from 'react-router-dom';

const paddingTop = '20px';

const GradientCard = ({ title, content, color1, color2 }) => {
    const useStyles = makeStyles({
        card: {
            margin: "5px",
            height: "150px",
            color: '#fff',
        },

        cardbg: {
            backgroundImage: `linear-gradient(45deg, ${color1}, ${color2})`

        },

        cardPngBg: {
            backgroundImage: `linear-gradient(45deg, ${color1}, ${color2})`, // url(${'https://thumbs.dreamstime.com/b/vector-seamless-pattern-graph-paper-background-blank-backdrop-template-grid-238680872.jpg'})
            backgroundSize: '100%',
        }
    })
    const classes = useStyles()

    return (
        <Card
            className={`${classes.card} ${classes.cardPngBg}`}
        >
            <CardContent>
                <Typography gutterBottom variant="h5" component="div" color={'white'} fontWeight={"bold"}>
                    <EllipsisText text={title} length={"45"} />
                </Typography>
                <Typography variant="h6" color={'white'}>
                    {content}
                </Typography>
            </CardContent>
            <Box display="flex" justifyContent="flex-end" alignItems="flex-end">
                <NavLink tag={Link} to="/" className="text-center" style={{ paddingTop }}>
                    <FontAwesomeIcon icon='arrow-up-right-from-square' color='white' />
                </NavLink>
            </Box>
        </Card >
    );
}

export default GradientCard;