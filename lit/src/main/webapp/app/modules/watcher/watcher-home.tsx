import React, { useEffect, useState } from 'react';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import Carousel from "react-multi-carousel";
import "react-multi-carousel/lib/styles.css";
import CategoryCard from './components/card/card';
import { getCountEntitesByTypes } from 'app/entities/watcher/stock/stock-group-type.reducer';
import './watcher.scss'

export const WatcherHome = () => {
  const gradients = [
    {
      key: 0,
      color1: '#2193b0',
      color2: '#6dd5ed80',
    },
    {
      key: 1,
      color1: '#ee9ca7',
      color2: '#ffdde180',
    },
    {
      key: 2,
      color1: '#7F7FD5',
      color2: '#91EAE480',
    },
    {
      key: 3,
      color1: '#2193b0',
      color2: '#6dd5ed80',
    },
    {
      key: 4,
      color1: '#4e54c8',
      color2: '#8f94fb80',
    },
    {
      key: 5,
      color1: '#11998e',
      color2: '#38ef7d80',
    },
    {
      key: 6,
      color1: '#FC5C7D',
      color2: '#6A82FB80',
    },
    {
      key: 7,
      color1: '#0575E6',
      color2: '#00F26080',
    },
    {
      key: 8,
      color1: '#F2994A',
      color2: '#F2C94C80',
    },
    {
      key: 9,
      color1: '#f4c4f3',
      color2: '#fc67fa80',
    },
    {
      key: 10,
      color1: '#f4c4f3',
      color2: '#fc67fa80',
    },
    {
      key: 14,
      color1: '#f4c4f3',
      color2: '#fc67fa80',
    },
    {
      key: 15,
      color1: '#f4c4f3',
      color2: '#fc67fa80',
    },
  ]

  const responsive = {
    superLargeDesktop: {
      // the naming can be any, depends on you.
      breakpoint: { max: 4000, min: 3000 },
      items: 5,
    },
    desktop: {
      breakpoint: { max: 3000, min: 1024 },
      items: 3
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

  const dispatch = useAppDispatch();
  const stockGroupSector = useAppSelector(state => state.stockGroupTypes.entitiesSector);
  const stockGroupIndustry = useAppSelector(state => state.stockGroupTypes.entitiesIndustry);
  const loadingSector = useAppSelector(state => state.stockGroupTypes.loading);
  const loadingIndustry = useAppSelector(state => state.stockGroupTypes.loading);

  useEffect(() => {
    dispatch(getCountEntitesByTypes("sector"));
  }, []);

  useEffect(() => {
    dispatch(getCountEntitesByTypes("industry"));
  }, []);


  return (
    <div>
      <h2>Explore markets</h2>
      <h4>By Sector</h4>
      <Carousel responsive={responsive} shouldResetAutoplay={false} >
        {stockGroupSector.map((data, index) => (
          <CategoryCard
            key={index}
            title={data.category}
            content={`${data.number} stocks`}
            color1={'#FFC644'} color2={'#FFAD44'}
            category={"sectors"} />
        ))}
      </Carousel>
      <h4>By Industry</h4>
      <Carousel responsive={responsive} shouldResetAutoplay={false}>
        {stockGroupIndustry.map((data, index) => (
          <CategoryCard
            key={index}
            title={data.category}
            content={`${data.number} stocks`}
            color1={'#44BEFF'} color2={'#449EFF'}
            category={"industries"} />
        ))}
      </Carousel>
    </div>
  );
};

export default WatcherHome;
