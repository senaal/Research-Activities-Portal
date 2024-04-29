import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import logo from './photo.jpeg';

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
          <Link to="/department" className='nav-link'>
            Departments
          </Link>
        </div>
        <div className='nav-box'>
            <Link to="/profiles" className='nav-link'>
            Profiles
          </Link>
        </div>
        <div className='nav-box'>
            <Link to="/scientific-articles" className='nav-link'>
            Scientific Articles
          </Link>
        </div>
        <div className='nav-box'>
          <Link to="/projects" className='nav-link'>
            Projects
          </Link>
        </div>
      </nav>
    </div>
  );
};

export default Navbar;
