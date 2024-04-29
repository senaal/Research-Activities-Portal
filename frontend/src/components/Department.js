import React, { useState } from 'react';
import { FaSearch } from 'react-icons/fa'; 
import './department.css';

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

const Department = ({ departments }) => {
    const [searchResults, setSearchResults] = useState([]);

  const handleSearch = (query) => {
    console.log("Searching for:", query);
  };

  return (
    <div className='department-page'>
        <div className='department-header'>
        <h1>COMPUTER ENGINEERING</h1>
      <SearchBar onSearch={handleSearch} />
      </div>
    </div>
  );
};

export default Department;
