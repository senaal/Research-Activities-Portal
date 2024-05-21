import React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Home from './components/Home';
import Profile from './components/Profile';
import Navbar from './components/Navbar';
import Department from './components/Department';
import ProfilesPage from './components/Profiles';
import Article from './components/Articles';
import Faculty from './components/Faculty';
import Admin from './components/Admin';
import Login from './components/Login';
import BubbleMap from './components/BubbleMap';

export default function App() {
  return (
    <BrowserRouter>
    <Navbar />
      <Routes>
      <Route className="layout" path="/" element={<Home />}/>
      <Route className="profile" path="/profile/:id" element={<Profile/>} />
      <Route className="department" path="/department/:id" element={<Department/>} />
      <Route className="faculty" path="/faculty/:id" element={<Faculty/>} />
      <Route className="profiles" path="/profiles" element={<ProfilesPage/>} />
      <Route className="articles" path="/scientific-articles" element={<Article/>} />
      <Route className="admin" path="/admin" element={<Admin/>} />
      <Route className="adminlogin" path="/admin/login" element={<Login/>} />
      <Route className="bubblemap" path="/bubblemap" element={<BubbleMap/>} />
      </Routes>
    </BrowserRouter>
  );
}


