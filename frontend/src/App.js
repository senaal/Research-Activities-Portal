import React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Home from './components/Home';
import Profile from './components/Profile';
import Navbar from './components/Navbar';

export default function App() {
  return (
    <BrowserRouter>
    <Navbar />
      <Routes>
      <Route className="layout" path="/" element={<Home />}/>
      <Route className="profile" path="/profile/:id" element={<Profile/>} />
      </Routes>
    </BrowserRouter>
  );
}


