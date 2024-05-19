import React, { useState, useEffect } from 'react';
import './admin.css';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  const handleLoginSubmit = async (event) => {
    event.preventDefault();
    try {
      const response = await fetch('http://localhost:8080/admin/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) {
        throw new Error('Login failed');
      }

      const data = await response.json();
      localStorage.setItem('token', data.token);
      setIsLoggedIn(true); 
    } catch (error) {
      console.error('Error during login:', error);
    }
  };

  if (isLoggedIn) {
    return <RedirectToAdmin token={localStorage.getItem('token')} />;
  }

  return (
    <div className="admin-page">
      <div>
        <form onSubmit={handleLoginSubmit}>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Email"
            required
          />
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Password"
            required
          />
          <button type="submit">Login</button>
        </form>
      </div>
    </div>
  );
};

const RedirectToAdmin = ({ token }) => {
    useEffect(() => {
        window.location.href = '/admin'; 
      }, []);
};

export default Login;
