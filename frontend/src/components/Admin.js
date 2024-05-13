
import React, { useState } from 'react';
import './admin.css';
import Modal from './Modal';

const Admin = () => {
  const [departmentName, setDepartmentName] = useState('');
  const [facultyId, setFacultyId] = useState('');
  const [facultyName, setFacultyName] = useState('');
  const [departmentId, setDepartmentId] = useState('');
  const [authorName, setAuthorName] = useState('');
  const [openAlexId, setOpenAlexId] = useState('');
  const [semanticId, setSemanticId] = useState('');
  const [email, setEmail] = useState('');
  const [photo, setPhoto] = useState('');
  const [title, setTitle] = useState('');
  const [departments, setDepartments] = useState([]);
  const [faculties, setFaculties] = useState([]);
  const [popupMessage, setPopupMessage] = useState('');


  const handleDepartmentSubmit = async () => {
    if (departmentName.trim() !== '') {
      try {
        const response = await fetch('http://localhost:8080/department/', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({facultyId: facultyId, departmentName: departmentName }),
        });
        if (!response.ok) {
          throw new Error('Failed to create department');
        }
        const data = await response.json();
        /*setDepartments([...departments, data]);*/
        setDepartmentName('');
        setFacultyId('');
        setPopupMessage('Department created successfully!');
      } catch (error) {
        console.error('Error creating department:', error);
        setPopupMessage('Error occurred');

      }
    }
  };

  const handleFacultySubmit = async () => {
    if (facultyName.trim() !== '') {
      try {
        const response = await fetch('http://localhost:8080/faculty/', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ facultyName: facultyName }),
        });
        if (!response.ok) {
          setPopupMessage('Error occurred!');
          throw new Error('Failed to add faculty');
        }
        const data = await response.json();
        /*setFaculties([...faculties, data]);
        setFacultyName('');*/
        setPopupMessage('Faculty created successfully!');
      } catch (error) {
        console.error('Error adding faculty:', error);
        setPopupMessage('Error occurred');
      }
    }
  };

  const handleFacultyMemberSubmit = async () => {
    if (departmentId.trim() !== '') {
      try {
        const response = await fetch('http://localhost:8080/facultymember/', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            departmentId: departmentId,
            authorName: authorName,
            openAlexId: openAlexId,
            semanticId: semanticId,
            email: email,
            photo: photo,
            title: title
            }),
        });
        if (!response.ok) {
          throw new Error('Failed to create department');
        }
        const data = await response.json();
        /*setDepartments([...departments, data]);
        setDepartmentName('');*/
        setDepartmentId('');
        setAuthorName('');
        setOpenAlexId('');
        setSemanticId('');
        setEmail('');
        setPhoto('');
        setTitle('');
        setPopupMessage('Member created successfully!');
      } catch (error) {
        console.error('Error creating member:', error);
        setPopupMessage('Error occurred');

      }
    }
  };


  const closePopup = () => {
    setPopupMessage('');
  };

  return (
    <div className="admin-page">
      {popupMessage && <Modal message={popupMessage} onClose={closePopup} />}
      <div className="section">
        <h2>Create Department</h2>
        <input
          type="text"
          value={departmentName}
          onChange={(e) => setDepartmentName(e.target.value)}
          placeholder="Enter department name"
        />
        <input
          type="number"
          value={facultyId}
          onChange={(e) => setFacultyId(e.target.value)}
          placeholder="Enter faculty id"
        />
        <button onClick={handleDepartmentSubmit}>Create Department</button>
        <ul>
          {departments.map((department, index) => (
            <li key={index}>{department.name}</li>
          ))}
        </ul>
      </div>
      <div className="section">
        <h2>Create Faculty</h2>
        <input
          type="text"
          value={facultyName}
          onChange={(e) => setFacultyName(e.target.value)}
          placeholder="Enter faculty name"
        />
        <button onClick={handleFacultySubmit}>Add Faculty</button>
        <ul>
          {faculties.map((faculty, index) => (
            <li key={index}>{faculty.name}</li>
          ))}
        </ul>
      </div>
      <div className="section">
        <h2>Create Faculty Member</h2>
        <input
          type="number"
          value={departmentId}
          onChange={(e) => setDepartmentId(e.target.value)}
          placeholder="Enter department id"
        />
        <input
          type="text"
          value={authorName}
          onChange={(e) => setAuthorName(e.target.value)}
          placeholder="Enter author name"
        />
        <input
          type="text"
          value={openAlexId}
          onChange={(e) => setOpenAlexId(e.target.value)}
          placeholder="Enter openAlexId"
        />
        <input
          type="text"
          value={semanticId}
          onChange={(e) => setSemanticId(e.target.value)}
          placeholder="Enter semanticId"
        />
        <input
          type="text"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="Enter email"
        />
        <input
          type="text"
          value={photo}
          onChange={(e) => setPhoto(e.target.value)}
          placeholder="Enter photo"
        />
        <input
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="Enter title"
        />
        <button onClick={handleFacultyMemberSubmit}>Add Faculty Member</button>
      </div>
    </div>
  );
};

export default Admin;
