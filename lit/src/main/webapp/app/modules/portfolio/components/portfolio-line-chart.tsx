import { Card, CardContent } from "@mui/material";
import { ApexOptions } from "apexcharts";
import React, { useEffect, useState } from "react";
import ReactApexChart from "react-apexcharts";

const PortfolioLineChart = (props) => {
  let addedPortfolioValue = 0;
  const series = [{
    name: "Invested",
    data: props.seriesInvested?.map((item) => {
      addedPortfolioValue = addedPortfolioValue + item.sum;
      const dateTime = new Date(item.date);
      const stockItem = { x: item.date, y: addedPortfolioValue }
      return stockItem;
    })
  },
  {
    name: "Value",
    data: props.seriesReal?.map((item) => {
      const stockItem = { x: item.date, y: item.value }
      return stockItem;
    })
  }
  ];

  const options: ApexOptions = {
    chart: {
      type: "area",
      stacked: false,
      width: '500px',
      zoom: {
        type: 'x',
        enabled: true,
        autoScaleYaxis: true
      },
      toolbar: {
        autoSelected: 'zoom'
      },
      animations: {
        enabled: true,
      }
    },
    noData: {
      text: "No orders were placed to be displayed",
      align: "center",
      verticalAlign: "middle",
      style: { color: 'gray', fontSize: '60px' }
    },
    dataLabels: {
      enabled: false
    },

    markers: {
      size: 0,
    },
    title: {
      text: 'Portofolios value',
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
      type: 'datetime'
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
      <CardContent style={{ height: "300px" }}>
        <ReactApexChart options={options} series={series} type="area" height={'100%'} />
      </CardContent>
    </Card>
  )
}

export default PortfolioLineChart;