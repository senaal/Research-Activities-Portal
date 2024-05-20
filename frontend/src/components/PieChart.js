import React from 'react';
import { Pie } from 'react-chartjs-2';
import { Chart, ArcElement, Tooltip, Legend, Title } from 'chart.js';

// Register the necessary components
Chart.register(ArcElement, Tooltip, Legend, Title);

const PieChart = ({ data }) => {
  if (!data || data.length === 0) {
    return <div>Loading...</div>;
  }
  
  data = data.slice(0, 10);

  const chartData = {
    labels: data.map(item => item.name),
    datasets: [
      {
        data: data.map(item => item.count),
        backgroundColor: [
          '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF',
          '#FF9F40', '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0',
        ],
        hoverBackgroundColor: [
          '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF',
          '#FF9F40', '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0',
        ],
      },
    ],
  };

  const options = {
    plugins: {
      legend: {
        display: false,
        position: 'bottom',
      },
      title: {
        display: true,
        position: 'top',
        text: 'Research Areas With Related Articles Count',
      },
      tooltip: {
        callbacks: {
          label: function (tooltipItem) {
            let label = chartData.labels[tooltipItem.dataIndex] || '';
            if (label) {
              label += ': ';
            }
            label += tooltipItem.raw;
            return label;
          },
        },
      },
    },
    responsive: true,
    maintainAspectRatio: false,
  };

  return (
    <div style={{ position: 'relative', width: '60%', height: '100%' }}>
      <Pie data={chartData} options={options} />
    </div>
  );
};

export default PieChart;
