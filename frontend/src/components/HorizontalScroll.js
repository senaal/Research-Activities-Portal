import React from 'react';
import './profiles.css'; // Import your CSS file

class HorizontalScroll extends React.Component {
    handleProfileClick = (authorId) => {
        // Redirect to the profile page when the profile link is clicked
        window.location.href = `http://localhost:3000/profile/${authorId}`;
      }
      
  render() {
    return (
      <div className="scroll-container">
        {this.props.items.map((item, index) => (
          <div className="scroll-item" key={index}>
            <a onClick={() => this.handleProfileClick(item.authorId)} className="profile link">
              <img src={item.photo} alt={item.authorName} className="photo" />
              <p>{item.authorName}</p>
            </a>
          </div>
        ))}
      </div>
    );
  }
}

export default HorizontalScroll;
