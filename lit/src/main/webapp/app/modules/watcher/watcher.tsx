import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Translate } from 'react-jhipster';
import { Row, Col, Alert } from 'reactstrap';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getAllPredictions } from 'app/entities/watcher/prediction/prediction.reducer';
import BarSample from './components/BarSample';

export const Watcher = () => {
  return (
    <div>
      <div>Watcher</div>
      <BarSample stock={"AAPL"}></BarSample>
    </div>
  );
};

export default Watcher;
