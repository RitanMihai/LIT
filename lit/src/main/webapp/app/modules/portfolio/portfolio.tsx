import React from 'react';
import { Link } from 'react-router-dom';
import { Translate } from 'react-jhipster';
import { Row, Col, Alert } from 'reactstrap';

import { useAppSelector } from 'app/config/store';
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';
import ErrorBoundary from 'app/shared/error/error-boundary';

export const Portfolio = () => {
  const account = useAppSelector(state => state.authentication.account);

  return (
    <div>Portfolio</div>
  );
};

export default Portfolio;
