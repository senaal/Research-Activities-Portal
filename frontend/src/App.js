import React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Home from './components/Home';
import Profile from './components/Profile';
import Navbar from './components/Navbar';
import Department from './components/Department';
import ProfilesPage from './components/Profiles';
import Article from './components/Articles';
import Admin from './components/Admin';

export default function App() {
  return (
    <BrowserRouter>
    <Navbar />
      <Routes>
      <Route className="layout" path="/" element={<Home />}/>
      <Route className="profile" path="/profile/:id" element={<Profile/>} />
      <Route className="department" path="/department/:id" element={<Department/>} />
      <Route className="profiles" path="/profiles" element={<ProfilesPage/>} />
      <Route className="articles" path="/scientific-articles" element={<Article/>} />
      <Route className="admin" path="/admin" element={<Admin/>} />
      </Routes>
    </BrowserRouter>
  );
}


