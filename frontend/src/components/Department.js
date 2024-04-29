import React, { useState } from 'react';
import { FaSearch } from 'react-icons/fa'; 
import './department.css';
import { PieChart,LineChart } from '@mui/x-charts';

const SearchBar = ({ onSearch }) => {
  const [query, setQuery] = useState('');

  const handleChange = (event) => {
    setQuery(event.target.value);
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    onSearch(query);
  };

  return (
    <form onSubmit={handleSubmit} className='input'>
      <input
        className='text'
        placeholder='Search'
        value={query}
        onChange={handleChange}
      />
      <button className='submit'>
        <FaSearch />
      </button>
    </form>
  );
};

const years = [
    new Date(2012, 0, 1),
    new Date(2013, 0, 1),
    new Date(2014, 0, 1),
    new Date(2015, 0, 1),
    new Date(2016, 0, 1),
    new Date(2017, 0, 1),
    new Date(2018, 0, 1),
    new Date(2019, 0, 1),
    new Date(2020, 0, 1),
    new Date(2021, 0, 1),
    new Date(2022, 0, 1),
    new Date(2023, 0, 1),
    new Date(2024, 0, 1),
  ];
  
  const citations = [
    3549, 3616, 6845, 7613, 4553, 6727, 3669, 3657,
    5663, 6527, 6827, 7124, 918,
  ];
const Department = ({ departments }) => {
    const [searchResults, setSearchResults] = useState([]);

  const handleSearch = (query) => {
    console.log("Searching for:", query);
  };

  return (
    <div>
        <div className='department-page'>
            <div className='department-header'>
            <h1>COMPUTER ENGINEERING</h1>
        <SearchBar onSearch={handleSearch} />
            </div>
        </div>
        <div className='charts'>
            <div>
            <PieChart
                series={[
                    {
                    data: [
                        { id: 0, value: 30, label: 'Telecommunication' },
                        { id: 1, value: 25, label: 'Computer Networks' },
                        { id: 2, value: 20, label: 'Algorithms' },
                        { id: 3, value: 15, label: 'Artifical Intelligence' },
                        { id: 4, value: 10, label: 'Internet of Things' },
                    ],
                    },
                ]}
                width={700}
                height={400}
            />
        </div>
        <div className='chart'>
        <LineChart
        xAxis={[
          {
            id: 'Years',
            data: years,
            scaleType: 'time',
            valueFormatter: (date) => date.getFullYear().toString(),
          },
        ]}
            series={[
                {
                    id: 'Cmpe',
                    label: 'Citations',
                    data: citations,
                    stack: 'total',
                    area: false,
                    showMark: false,
                  },
            ]}
                width={700}
                height={400}
                />
            </div>
         </div>
    </div>
  );
};

export default Department;
