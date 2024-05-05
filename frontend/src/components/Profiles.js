
import React, { useEffect, useState } from 'react';
import HorizontalScroll from './HorizontalScroll';
import './profiles.css';



const ProfilesPage = () => {
  const [members, setMembers] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Fetch members data
        const facultyMembersResponse = await fetch(`http://localhost:8080/facultymember/`);
        let data = await facultyMembersResponse.json();
        console.log(data)
        setMembers(data);

      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    fetchData();
  });

  return (
    <div>
       <ul>
          {members.map(department => (
            <div key={department.department.departmentId}>
              <div className='department'>
                <h1>{department.department.departmentName}</h1>
                <HorizontalScroll items={department.members} /> {}
              </div>  
            </div>
          ))}
        </ul>
    </div>
  );
}

export default ProfilesPage;