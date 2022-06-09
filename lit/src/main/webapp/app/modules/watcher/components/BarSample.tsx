import { ApexOptions } from "apexcharts";
import { useAppDispatch, useAppSelector } from "app/config/store";
import { getAllPredictions } from "app/entities/watcher/prediction/prediction.reducer";
import React, { useEffect, useState } from "react";
import ReactApexChart from "react-apexcharts";
import Chart from "react-apexcharts";
import { Table } from "reactstrap";

const BarSample = (props) => {
  const dispatch = useAppDispatch();
  const predictionList = useAppSelector(state => state.prediction.entities);
  const loading = useAppSelector(state => state.prediction.loading);

  const [price, setPrice] = useState([]);
  // The date data come as Unix, it must be converted to Date
  // Date constructor achive this easily
  const [date, setDate] = useState([]);

  useEffect(()=> {
    dispatch(getAllPredictions(props.stock));
  }, [])

  const series = [{
    name: props.stock,
    data: predictionList?.map((item) => item.value)
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
      }
    },
    dataLabels: {
      enabled: false
    },
    markers: {
      size: 0,
    },
    title: {
      text: 'Predicted stock price for '+ props.stock,
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
      categories: predictionList?.map((item) => {
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
    }
  };

  return (
    <div>
      <ReactApexChart options={options} series={series} type="area" height={350} />
      <div className="table-responsive">
          {predictionList && predictionList.length > 0 ? (
            <Table responsive>
               <thead>
                 <tr>
                  <th>
                    Date
                  </th>
                  <th>
                    Value
                  </th>
                 </tr>
               </thead>
              <tbody>
              {predictionList.map((prediction, i) => ( 
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>{prediction.date}</td>
                  <td>{prediction.value}</td>
                </tr>
               ))}
              </tbody>
            </Table>
             ) : (
              !loading && (
                <div className="alert alert-warning">
                  No Prediction Found
                </div>
              )
            )}
      </div>
    </div>
  )
}

export default BarSample;