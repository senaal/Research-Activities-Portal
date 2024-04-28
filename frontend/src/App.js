import React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Home from './components/Home';
import Profile from './components/Profile';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
      <Route className="layout" path="/" element={<Home />}/>
      <Route className="layout" path="profile" element={<Profile />} />
      </Routes>
    </BrowserRouter>
  );
}

/*
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);
*/