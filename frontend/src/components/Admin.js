import React, { useState, useEffect } from 'react';
import './admin.css';
import Modal from './Modal';
import Tabs from './Tabs'; 
import Confirm from './Confirm';



const Admin = () => {
  const [departmentName, setDepartmentName] = useState('');
  const [facultyId, setFacultyId] = useState('');
  const [facultyName, setFacultyName] = useState('');
  const [authorName, setAuthorName] = useState('');
  const [openAlexId, setOpenAlexId] = useState('');
  const [semanticId, setSemanticId] = useState('');
  const [authorId, setAuthorId] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [photo, setPhoto] = useState('');
  const [title, setTitle] = useState('');
  const [newdepartmentId, setnewDepartmentId] = useState('');
  const [newauthorName, setnewAuthorName] = useState('');
  const [newopenAlexId, setnewOpenAlexId] = useState('');
  const [newsemanticId, setnewSemanticId] = useState('');
  const [newemail, setnewEmail] = useState('');
  const [newphone, setnewPhone] = useState('');
  const [newphoto, setnewPhoto] = useState('');
  const [newtitle, setnewTitle] = useState('');
  const [deletedFacultyId, setDeletedFacultyId] = useState('');
  const [deletedDepartmentId, setDeletedDepartmentId] = useState('');
  const [departments, setDepartments] = useState([]);
  const [members, setMembers] = useState([]);
  const [faculties, setFaculties] = useState([]);
  const [popupMessage, setPopupMessage] = useState('');
  const [activeTab, setActiveTab] = useState('Create Faculty'); 
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [confirmAction, setConfirmAction] = useState(null);


  useEffect(() => {
    fetchFaculties();
    fetchDepartments();
    fetchMembers();
  }, []);

  const fetchFaculties = async () => {
    try {
      const response = await fetch('http://localhost:8080/faculty/');
      if (!response.ok) {
        throw new Error('Failed to fetch faculties');
      }
      const data = await response.json();
      setFaculties(data);
    } catch (error) {
      console.error('Error fetching faculties:', error);
      setPopupMessage('Error occurred');
    }
  };

  const fetchDepartments = async () => {
    try {
      const response = await fetch('http://localhost:8080/department/');
      if (!response.ok) {
        throw new Error('Failed to fetch faculties');
      }
      const data = await response.json();
      setDepartments(data);
    } catch (error) {
      console.error('Error fetching departments:', error);
      setPopupMessage('Error occurred');
    }
  };

  const fetchMembers = async () => {
    try {
      const response = await fetch('http://localhost:8080/facultymember/all');
      if (!response.ok) {
        throw new Error('Failed to fetch members');
      }
      const data = await response.json();
      setMembers(data);
    } catch (error) {
      console.error('Error fetching members:', error);
      setPopupMessage('Error occurred');
    }
  };

  const handleDepartmentSubmit = async () => {
    if (departmentName.trim() !== '') {
      try {
        const response = await fetch('http://localhost:8080/department/', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ facultyId: facultyId, departmentName: departmentName }),
        });
        if (!response.ok) {
          throw new Error('Failed to create department');
        }
        const data = await response.json();
        setDepartmentName('');
        setFacultyId('');
        setPopupMessage('Department created successfully!');
        fetchDepartments();
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
        setPopupMessage('Faculty created successfully!');
        fetchFaculties(); 
      } catch (error) {
        console.error('Error adding faculty:', error);
        setPopupMessage('Error occurred');
      }
    }
  };

  const handleFacultyMemberSubmit = async () => {
    if (newdepartmentId.trim() !== '') {
      try {
        const response = await fetch('http://localhost:8080/facultymember/', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            departmentId: newdepartmentId,
            authorName: newauthorName,
            openAlexId: newopenAlexId,
            semanticId: newsemanticId,
            email: newemail,
            photo: newphoto,
            title: newtitle,
            phone: newphone,
          }),
        });
        if (!response.ok) {
          throw new Error('Failed to create department');
        }
        const data = await response.json();
        setnewDepartmentId('');
        setnewAuthorName('');
        setnewOpenAlexId('');
        setnewSemanticId('');
        setnewEmail('');
        setnewPhone('');
        setnewPhoto('');
        setnewTitle('');
        setPopupMessage('Member created successfully!');
      } catch (error) {
        console.error('Error creating member:', error);
        setPopupMessage('Error occurred');
      }
    }
  };

  const handleMemberSelect = (e) => {
    console.log("Member selected:", e.target.value); // Check if this log appears in the console
    const selectedMemberId = e.target.value;
    setAuthorId(selectedMemberId);
    fetch(`http://localhost:8080/facultymember/${selectedMemberId}`)
      .then(response => response.json())
      .then(data => {
        setAuthorName(data.member.authorName);
        setEmail(data.member.email);
        setPhoto(data.member.photo);
        setPhone(data.member.phone);
        setTitle(data.member.title);
        setOpenAlexId(data.member.openAlexId);
        setSemanticId(data.member.semanticId);
        
      })
      .catch(error => console.error('Error fetching member details:', error));
  };

  const handleEditFacultyMemberSubmit = () => {
    const editedData = {
      phone: phone,
      email: email,
      photo: photo,
      title: title,
    };
    fetch(`http://localhost:8080/facultymember/${authorId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(editedData)
    })
    .then(response => {
      if (response.ok) {
        setPopupMessage('Member updated successfully!');
      } else {
        console.error('Failed to update member data');
        setPopupMessage('Error occured!');

      }
    })
    .catch(error => console.error('Error updating member data:', error));
    setPopupMessage('Error occured!');
  };

  const handleDeleteFacultySubmit = () => {
    fetch(`http://localhost:8080/faculty/${deletedFacultyId}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json'
      },
    })
    .then(response => {
      if (response.ok) {
        setPopupMessage('Faculty is deleted!');
      } else {
        console.error('Failed to delete faculty');
        setPopupMessage('Error occured!');

      }
      fetchFaculties();

    })
    .catch(error => console.error('Error deleting faculty', error));
    setPopupMessage('Error occured!');
  };

  const handleDeleteDepartmentSubmit = () => {
    fetch(`http://localhost:8080/department/${deletedDepartmentId}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json'
      },
    })
    .then(response => {
      if (response.ok) {
        setPopupMessage('Department is deleted!');
      } else {
        console.error('Failed to delete department');
        setPopupMessage('Error occured!');

      }
      fetchFaculties();

    })
    .catch(error => console.error('Error deleting department', error));
    setPopupMessage('Error occured!');
  };

  const handleDeleteConfirmation = (facultyId) => {
    setDeletedFacultyId(facultyId); 
    setConfirmAction(() => () => handleDeleteFacultySubmit(facultyId)); 
    setShowConfirmation(true); 
  }

  const handleDeleteDepartmentConfirmation = (departmentId) => {
    setDeletedDepartmentId(departmentId); 
    setConfirmAction(() => () => handleDeleteDepartmentSubmit(departmentId)); 
    setShowConfirmation(true); 
  }

  const handleConfirmationAction = () => {
    if (confirmAction) {
      confirmAction(); 
      setShowConfirmation(false); 
    }
  };
  const closePopup = () => {
    setPopupMessage('');
  };

  const handleTabChange = (tab) => {
    setActiveTab(tab);
  };

  return (
    <div className="admin-page">
      {popupMessage && <Modal message={popupMessage} onClose={closePopup} />}
      <div className="tabs" style={{ marginTop: '20px' }}>
        <Tabs
          tabs={['Create Faculty', 'Create Department', 'Create Faculty Member', 'Edit Faculty Member', 'Delete Faculty', 'Delete Department']}
          defaultTab="Create Faculty"
          onTabChange={handleTabChange}
        />
      </div>
      {activeTab === 'Create Department' && (
        <>
      <div className="section">
        <h2>Create Department</h2>
        <select value={facultyId} onChange={(e) => setFacultyId(e.target.value)}>
          <option value="">Select Faculty of Department</option>
          {faculties.map((faculty) => (
            <option key={faculty.facultyId} value={faculty.facultyId}>
              {faculty.facultyName}
            </option>
          ))}
        </select>
        <input
          type="text"
          value={departmentName}
          onChange={(e) => setDepartmentName(e.target.value)}
          placeholder="Enter department name"
        />
        
        <button onClick={handleDepartmentSubmit}>Create Department</button>
        
      </div>
      </>
    )}
    {activeTab === 'Create Faculty' && (
            <>
      <div className="section">
        <h2>Create Faculty</h2>
        <input
          type="text"
          value={facultyName}
          onChange={(e) => setFacultyName(e.target.value)}
          placeholder="Enter faculty name"
        />
        <button onClick={handleFacultySubmit}>Add Faculty</button>
        
      </div>
      </>
    )}
    {activeTab === 'Create Faculty Member' && (
            <>
      <div className="section">
        <h2>Create Faculty Member</h2>
        <select value={newdepartmentId} onChange={(e) => setnewDepartmentId(e.target.value)}>
          <option value="">Select Department of Member</option>
          {departments.map((department) => (
            <option key={department.departmentId} value={department.departmentId}>
              {department.departmentName}
            </option>
          ))}
        </select>
        <input
          type="text"
          value={newauthorName}
          onChange={(e) => setnewAuthorName(e.target.value)}
          placeholder="Enter author name"
        />
        <input
          type="text"
          value={newopenAlexId}
          onChange={(e) => setnewOpenAlexId(e.target.value)}
          placeholder="Enter openAlexId"
        />
        <input
          type="number"
          value={newsemanticId}
          onChange={(e) => setnewSemanticId(e.target.value)}
          placeholder="Enter semanticId"
        />
        <input
          type="text"
          value={newemail}
          onChange={(e) => setnewEmail(e.target.value)}
          placeholder="Enter email"
        />
        <input
          type="text"
          value={newphone}
          onChange={(e) => setnewPhone(e.target.value)}
          placeholder="Enter phone"
        />
        <input
          type="text"
          value={newphoto}
          onChange={(e) => setnewPhoto(e.target.value)}
          placeholder="Enter photo"
        />
        <input
          type="text"
          value={newtitle}
          onChange={(e) => setnewTitle(e.target.value)}
          placeholder="Enter title"
        />
        <button onClick={handleFacultyMemberSubmit}>Add Faculty Member</button>
      </div>
      </>
    )}
    {activeTab === 'Edit Faculty Member' && (
            <>
      <div className="section">
      <h2>Edit Faculty Member</h2>
      <select value={authorId} onChange={handleMemberSelect}>
        <option value="">Select Member</option>
        {members.map((member) => (
          <option key={member.authorId} value={member.authorId}>
            {member.authorName}
          </option>
        ))}
      </select>
      <p> Email</p>
      <input
        type="text"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        placeholder="Enter email"
        title="Email"
      />
      <p> Phone</p>
      <input
        type="text"
        value={phone}
        onChange={(e) => setPhone(e.target.value)}
        placeholder="Enter phone"
        title="Phone"
      />
      <p> Photo</p>
      <input
        type="text"
        value={photo}
        onChange={(e) => setPhoto(e.target.value)}
        placeholder="Enter photo"
        title="Photo"
      />
      <p> Title</p>
      <input
        type="text"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        placeholder="Enter title"
        title="Title"
      />
      <button onClick={handleEditFacultyMemberSubmit}>Submit</button>
      </div>
      </>
    )}
    {activeTab === 'Delete Faculty' && (
            <>
      <div className="section">
        <h2>Delete Faculty</h2>
        <select value={deletedFacultyId} onChange={(e) => setDeletedFacultyId(e.target.value)}>
          <option value="">Select Faculty</option>
          {faculties.map((faculty) => (
            <option key={faculty.facultyId} value={faculty.facultyId}>
              {faculty.facultyName}
            </option>
          ))}
        </select>
        <button onClick={() => handleDeleteConfirmation(deletedFacultyId)}>Delete Faculty</button>
        
      </div>
      {showConfirmation && (
        <Confirm message="Are you sure? Faculty and related departments will be deleted." onClose={() => setShowConfirmation(false)} onConfirm={handleConfirmationAction}>
        </Confirm>
      )}
      </>
    )}
    {activeTab === 'Delete Department' && (
            <>
      <div className="section">
        <h2>Delete Department</h2>
        <select value={deletedDepartmentId} onChange={(e) => setDeletedDepartmentId(e.target.value)}>
          <option value="">Select Department</option>
          {departments.map((department) => (
            <option key={department.departmentId} value={department.departmentId}>
              {department.departmentName}
            </option>
          ))}
        </select>
        <button onClick={() => handleDeleteDepartmentConfirmation(deletedDepartmentId)}>Delete Department</button>
        
      </div>
      {showConfirmation && (
        <Confirm message="Are you sure? Department will be deleted." onClose={() => setShowConfirmation(false)} onConfirm={handleConfirmationAction}>
        </Confirm>
      )}
      </>
    )}
    </div>
  );
};

export default Admin;
