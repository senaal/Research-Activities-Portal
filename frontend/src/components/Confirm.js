import React from 'react';
import './modal.css';

const Confirm = ({ message, onClose, onConfirm }) => {
    return (
      <div className="confirm-overlay">
        <div className="confirm">
          <div className="confirm-content">
            <p>{message}</p>
            <div className="confirm-buttons">
              <button onClick={onConfirm}>Confirm</button>
              <button onClick={onClose}>Cancel</button>
            </div>
          </div>
        </div>
      </div>
    );
  };

  export default Confirm;