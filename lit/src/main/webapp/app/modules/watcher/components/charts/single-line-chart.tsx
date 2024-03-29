import { Card, CardContent } from "@mui/material";
import { ApexOptions } from "apexcharts";
import React, { useEffect, useState } from "react";
import ReactApexChart from "react-apexcharts";

const SingleLineChart = (props) => {
  const series = [{
    name: props.stock,
    data: props.series?.map((item) => item.value)
  }];

  const options: ApexOptions = {
    chart: {
      type: "area",
      stacked: false,
      height: 350,
      zoom: {
        type: 'x',
        enabled: true,
        autoScaleYaxis: true
      },
      toolbar: {
        autoSelected: 'zoom'
      },
      animations: {
        enabled: false,
      }
    },
    dataLabels: {
      enabled: false
    },
    markers: {
      size: 0,
    },
    title: {
      text: 'Predicted stock price for ' + props.stock,
      align: 'left'
    },
    fill: {
      type: 'gradient',
      gradient: {
        shadeIntensity: 1,
        inverseColors: false,
        opacityFrom: 0.5,
        opacityTo: 0,
        stops: [0, 90, 100]
      },
    },
    yaxis: {
      labels: {
        formatter(val) {
          return (val).toFixed(0);
        },
      },
      title: {
        text: 'Price'
      },
    },
    xaxis: {
      type: 'datetime',
      categories: props.series?.map((item) => {
        const dateTime = new Date(item.date);
        return dateTime.toDateString();
      })
    },
    tooltip: {
      shared: false,
      y: {
        formatter(val) {
          return (val).toFixed(3)
        }
      }
    },
  };

  return (
    <Card>
      <CardContent>
        <ReactApexChart options={options} series={series} type="area" height={'100%'} />
      </CardContent>
    </Card>
  )
}

export default SingleLineChart;