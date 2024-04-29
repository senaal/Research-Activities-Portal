import React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Home from './components/Home';
import Department from './components/Department';
import Navbar from './components/Navbar';

export default function App() {
  return (
    <BrowserRouter>
    <Navbar />
      <Routes>
      <Route className="layout" path="/" element={<Home />}/>
      <Route className="layout" path="department" element={<Department />} />
      </Routes>
    </BrowserRouter>
  );
}

