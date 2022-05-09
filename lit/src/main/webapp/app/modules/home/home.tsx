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

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);

  return (
      <Col>
        {account?.login ? (
          <div>
              <div className='homeContainer'>
                <ErrorBoundaryRoute  component={Feed} />
                <ErrorBoundaryRoute component={RightBar} />
              </div>
          </div>
        ) : (
          <div>
            <Alert color="warning">
              <Translate contentKey="global.messages.info.register.noaccount">You do not have an account yet?</Translate>&nbsp;
              <Link to="/account/register" className="alert-link">
                <Translate contentKey="global.messages.info.register.link">Register a new account</Translate>
              </Link>
            </Alert>
          </div>
        )}
      </Col>
  );
};

export default Home;
