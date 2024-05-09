import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { FaSearch } from 'react-icons/fa'; 
import logo from './photos/photo.jpeg';
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
    <form onSubmit={handleSubmit} className='nav-link'>
      <input
        placeholder='Search'
        value={query}
        onChange={handleChange}
      />
      <button >
        <FaSearch />
      </button>
    </form>
  );
};
function Navbar(){
    useEffect(() => {
        const navLinks = document.querySelectorAll('.nav-link');
    
        navLinks.forEach(function(link) {
          link.addEventListener('click', function() {
            navLinks.forEach(function(navLink) {
              navLink.classList.remove('active');
            });
            this.classList.add('active');
          });
        });
      }, []);
      const handleSearch = (query) => {
        console.log("Searching for:", query);
      }; 
  return (
    <div>

    <div className='header'>
        <div className='unilogo'>
            <img src={logo} alt="logo" />
        </div>

        <div className='uniname'>
            <h1>BOGAZICI UNIVERSITY RESEARCH PORTAL</h1>      
        </div> 
    </div>
   
      <nav className='navbar'>
      <div className='nav-box'>
          <Link to="/faculty" className='nav-link'>
            Faculties
          </Link>
        </div>
        <div className='nav-box'>
          <Link to="/department" className='nav-link'>
            Departments
          </Link>
        </div>
        <div className='nav-box'>
            <Link to="/profiles" className='nav-link'>
            Faculty Members
          </Link>
        </div>
        <div className='nav-box'>
          <SearchBar onSearch={handleSearch} />
        </div>
      </nav>
    </div>
  );
};

export default Navbar;
