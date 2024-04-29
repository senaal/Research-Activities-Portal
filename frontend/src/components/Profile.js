import React, { useEffect, useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faEnvelope, faPhone } from '@fortawesome/free-solid-svg-icons'
import { useParams } from 'react-router-dom'; // Import useParams from react-router-dom
import './profile.css';

const Profile = () => {
  const [author, setAuthor] = useState(null);
  const [articles, setArticles] = useState([]);
  
  const { id } = useParams(); // Get the id from URL params
  
  useEffect(() => {
    const fetchData = async () => {
      const authorResponse = await fetch(`http://localhost:8080/facultymember/${id}`); // Use the id from URL
      const authorData = await authorResponse.json();
      setAuthor(authorData);

      // Mock articles data
      const articlesData = [
        {
          articleId: 1,
          articleTitle: "Mock Article 1",
          affiliation: "Mock University",
          doi: "/mock/doi/1",
          publicationDate: "2022-01-01T00:00:00.000Z",
          citationCount: 10,
          openAccess: true,
          authors: ["Sanem Arslan", "Haluk Rahmi Topcuoglu", "Mahmut Taylan Kandemir", "Oguz Tosun"]
        },
        {
          articleId: 2,
          articleTitle: "Mock Article 2",
          affiliation: "Mock University",
          doi: "/mock/doi/2",
          publicationDate: "2022-02-01T00:00:00.000Z",
          citationCount: 20,
          openAccess: false,
          authors: ["Sanem Arslan", "Haluk Rahmi Topcuoglu", "Mahmut Taylan Kandemir", "Oguz Tosun"]
        }
        // Add more mock articles as needed
      ];
      setArticles(articlesData);
    };

    fetchData();
  }, [id]); // Make sure to include id in dependency array

  return (
    <div className="author">
      <div className="main-content">
        <h2>Publications</h2>
        <ul>
          {articles.map(article => (
            <li key={article.articleId}>
              <div>
                <a href={article.doi} className="article-title">{article.articleTitle}</a>
                <p className="author-info">Authors: {article.authors.join(', ')}</p>
                <p className="publication-date">Publication Date: {new Date(article.publicationDate).toLocaleDateString()}</p>
              </div>
            </li>
          ))}
        </ul>
      </div>
      <div className="contact-info">
      <img src={author && `${author.photo}`} alt="Author's Photo" className="author-photo" />
        <h1>{author && `${author.title} ${author.authorName}`}</h1>
        <p>{author && author.departmentId.departmentName}</p>
        <h2 style={{ marginTop: '30px' }}> {'Contact Info'}</h2>
        <p><FontAwesomeIcon icon={faEnvelope} />  {author && author.email}</p>
        <p><FontAwesomeIcon icon={faPhone} /> {author && author.phone} </p>
        <p style={{ marginTop: '60px' }}> <img src={require("./photos/index.PNG")}  className="icon" /> <strong>h-index:</strong> {author && author.hindex}</p>
        <p><img src={require("./photos/index.PNG")}  className="icon" /> <strong>Citation Count:</strong> {author && author.citedByCount}</p>
      </div>
    </div>
  );
};  

export default Profile;
