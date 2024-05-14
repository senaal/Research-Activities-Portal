import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { FaSearch } from 'react-icons/fa'; 
import logo from './photos/photo.jpeg';
import './navbar.css';

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
    <form onSubmit={handleSubmit}>
      <input
        placeholder='Search'
        value={query}
        onChange={handleChange}
      />
      <button type="submit">
        <FaSearch />
      </button>
    </form>
  );
};

function Navbar() {
  const [showFacultyDropdown, setShowFacultyDropdown] = useState(false);
  const [showDepartmentDropdown, setShowDepartmentDropdown] = useState(false);
  const [departments, setDepartments] = useState([]);
  useEffect(() => {
    const fetchData = async () => {
      try {;
        const departmentsResponse = await fetch('http://localhost:8080/department/');
        const departmentsData = await departmentsResponse.json();
        setDepartments(departmentsData);

      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    fetchData();
  },);

  return (
    <div>
      <div className='header'>
      <Link to="/"> 
          <div className='unilogo'>
            <img src={logo} alt="logo" />
          </div>
        </Link>
        <div className='uniname'>
          <h1>BOGAZICI UNIVERSITY RESEARCH PORTAL</h1>      
        </div> 
      </div>
   
      <nav className='navbar'>
        <div className='nav-box'>
          <div className="dropdown-box" onMouseEnter={() => setShowFacultyDropdown(true)} onMouseLeave={() => setShowFacultyDropdown(false)}>
            <span className="dropdown-name">Faculties</span> 
            {showFacultyDropdown && (
              <div className="dropdown-content faculty-dropdown">
                <Link to="/faculty1" className='nav-link' key="faculty1">Faculty 1</Link>
                <Link to="/faculty2" className='nav-link' key="faculty2">Faculty 2</Link>
              </div>
            )}
          </div>
        </div>

        <div className='nav-box'>
      <div
        className="dropdown-box"
        onMouseEnter={() => setShowDepartmentDropdown(true)}
        onMouseLeave={() => setShowDepartmentDropdown(false)}
      >
          <span className="dropdown-name">Departments</span>
          {showDepartmentDropdown && (
            <div className="dropdown-content department-dropdown">
              {departments.map(department => (
                <Link
                  to={`/department/${department.departmentId}`}
                  className='nav-link'
                  key={department.departmentId}
                  onClick={() => setShowDepartmentDropdown(false)} 
                >
                  {department.departmentName}
                </Link>
              ))}
            </div>
          )}
        </div>
      </div>

        <div className='nav-box'>
          <SearchBar />
        </div>
      </nav>
    </div>
  );
}

export default Navbar;
