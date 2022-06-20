import './home.scss';

import React from 'react';
import { Link } from 'react-router-dom';
import { Translate } from 'react-jhipster';
import { Row, Col, Alert } from 'reactstrap';

import { useAppSelector } from 'app/config/store';
import Feed from './feed/feed';
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';
import ErrorBoundary from 'app/shared/error/error-boundary';
import RightBar from './rightbar/rightbar';
import { Card, CardContent, Container, Grid } from '@mui/material';

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);

  return (
    <div>
      {account?.login ? (
        <Grid container spacing={2}>
          <Grid item xs={2}>
            <Card>
              <CardContent>
                Some analitic here
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={8}>
            <ErrorBoundaryRoute component={Feed} />
          </Grid>
          <Grid item xs={2}>
            <Card>
              <CardContent>
                Some analitic here
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      ) : (
        null
      )}
    </div>
  );
};

export default Home;
