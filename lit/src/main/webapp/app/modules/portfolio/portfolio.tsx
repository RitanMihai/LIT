import React, { PureComponent }  from 'react';
import { Link } from 'react-router-dom';
import { Translate } from 'react-jhipster';
import { Row, Col, Alert } from 'reactstrap';

import { useAppSelector } from 'app/config/store';
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';
import ErrorBoundary from 'app/shared/error/error-boundary';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, AreaChart, Area } from 'recharts';
import Widget from './components/widget/widget';
import './portfolio.scss'

export const Portfolio = () => {
  const account = useAppSelector(state => state.authentication.account);
 
    return (
      <div className="home">
        <div className="homeContainer">
          <div className="widgets">
              <Widget type="user" />
              <Widget type="order" />
              <Widget type="earning" />
              <Widget type="balance" />
          </div>
        </div>
      </div>
    );
};

export default Portfolio;
