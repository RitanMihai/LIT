import { ApexOptions } from "apexcharts";
import React, { useEffect, useState } from "react";
import ReactApexChart from "react-apexcharts";

const MultiLineChart = (props) => {
    const series = [{
        name: "Real price",
        data: props.seriesStock.map((item) => {
            const stockItem = { x: item.date, y: item.value }
            console.log("Stock item", stockItem)
            return stockItem;
        })
    }, {
        name: "Predicted price",
        data: props.seriesPrediction.map((item) => {
            const stockItem = { x: item.date, y: item.value }
            console.log("Stock item", stockItem)
            return stockItem;
        })
    }
    ];

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
                enabled: (props.lenght > 4000) ? false : true,
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
        <div>
            <ReactApexChart options={options} series={series} type="area" height={350} />
        </div>
    )
}

export default MultiLineChart;