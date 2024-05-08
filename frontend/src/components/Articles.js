import React, { useEffect, useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {faArrowLeft, faArrowRight } from '@fortawesome/free-solid-svg-icons';

const Article = () => {
  const [articles, setArticles] = useState([]);
  const [maxPage, setMaxPage] = useState(0);
  const [page, setpage] = useState(0);
  const [size, setsize] = useState(10);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const articlesResponse = await fetch(`http://localhost:8080/article/scientific_articles?page=${page}&size=${size}`);
        const articlesData = await articlesResponse.json();
        setArticles(articlesData.content);
        setMaxPage(articlesData.totalPages);
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    fetchData();
  }, [page, size]);

  const handleNextPage = () => {
    setpage(page + 1);
  };

  const handlePrevPage = () => {
    if (page > 0) {
      setpage(page - 1);
    }
  };


  return (
    <div className="author">
      <div className="main-content">
        <h2>Scientific Articles</h2>
        <ul>
          {articles.map(article => (
            <li key={article.article.articleId}>
              <div>
                <a  href={article.article.paperPdf} className="article-title">{article.article.articleTitle}</a>
                <p className="author-info"> {article.authorNames.join(', ')}</p>
                <p className="publication-date">Publication Date: {new Date(article.article.publicationDate).toLocaleDateString()}</p>
              </div>
            </li>
          ))}
        </ul>
        <div className="pagination-buttons">
          <button onClick={handlePrevPage} disabled={page === 0}  style={{ marginLeft: '60%' }}>
            <FontAwesomeIcon icon={faArrowLeft} />
          </button>
          <button onClick={handleNextPage} disabled={page === (maxPage-1)}>
            <FontAwesomeIcon icon={faArrowRight} />
          </button>
        </div>
      </div>
    </div>
  );
};  

export default Article;
