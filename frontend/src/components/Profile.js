import React, { useEffect, useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEnvelope, faPhone } from '@fortawesome/free-solid-svg-icons';
import { useParams } from 'react-router-dom';
import './profile.css';

const Profile = () => {
  const [author, setAuthor] = useState(null);
  const [articles, setArticles] = useState([]);
  
  const { id } = useParams();

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Fetch author data
        const authorResponse = await fetch(`http://localhost:8080/facultymember/${id}`);
        const authorData = await authorResponse.json();
        setAuthor(authorData);

        // Fetch articles data for the author
        const articlesResponse = await fetch(`http://localhost:8080/article/author/${id}`);
        const articlesData = await articlesResponse.json();
        setArticles(articlesData);
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    fetchData();
  }, [id]);

  return (
    <div className="author">
      <div className="main-content">
        <h2>Scientific Articles</h2>
        <ul>
          {articles.map(article => (
            <li key={article.article.articleId}>
              <div>
                <a style={{ marginTop: '20px' }} href={article.article.paperPdf} className="article-title">{article.article.articleTitle}</a>
                <p className="author-info"> {article.authorNames.join(', ')}</p>
                <p className="publication-date">Publication Date: {new Date(article.article.publicationDate).toLocaleDateString()}</p>
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
