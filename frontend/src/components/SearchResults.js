import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowLeft, faArrowRight } from '@fortawesome/free-solid-svg-icons';
import './searchResults.css';

const SearchResults = () => {
  const { query } = useParams();
  const [searchResults, setSearchResults] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [maxPage, setMaxPage] = useState(0);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [sortOrder] = useState('DESC');
  const [sortBy] = useState('publicationDate');
  const navigate = useNavigate();

  useEffect(() => {
    if (!query) {
      navigate('/');
    } else {
      fetchSearchResults();
    }
  }, [query, page]);

  const fetchSearchResults = async () => {
    setIsLoading(true);
    try {
      const response = await fetch(`http://localhost:8080/article/search?title=${query}&page=${page}&size=${size}&sortOrder=${sortOrder}&sortBy=${sortBy}`);
      const data = await response.json();
      setSearchResults(data.content);
      setMaxPage(data.totalPages);
      setIsLoading(false);
    } catch (error) {
      console.error('Error fetching search results:', error);
      setIsLoading(false);
    }
  };

  const handleNextPage = () => {
    setPage(page + 1);
  };

  const handlePrevPage = () => {
    if (page > 0) {
      setPage(page - 1);
    }
  };

  const renderPageNumbers = () => {
    const pageNumbers = [];
    if (maxPage <= 5) {
      for (let i = 0; i < maxPage; i++) {
        pageNumbers.push(
          <button
            key={i}
            onClick={() => setPage(i)}
            className={i === page ? 'active-page' : ''}
            style={{ margin: '0 5px' }}
          >
            {i + 1}
          </button>
        );
      }
    } else {
      pageNumbers.push(
        <button
          key={0}
          onClick={() => setPage(0)}
          className={page === 0 ? 'active-page' : ''}
          style={{ margin: '0 5px' }}
        >
          1
        </button>
      );

      if (page > 2) {
        pageNumbers.push(<span key="left-ellipsis">...</span>);
      }

      for (let i = Math.max(1, page - 1); i <= Math.min(maxPage - 2, page + 1); i++) {
        pageNumbers.push(
          <button
            key={i}
            onClick={() => setPage(i)}
            className={i === page ? 'active-page' : ''}
            style={{ margin: '0 5px' }}
          >
            {i + 1}
          </button>
        );
      }

      if (page < maxPage - 3) {
        pageNumbers.push(<span key="right-ellipsis">...</span>);
      }

      pageNumbers.push(
        <button
          key={maxPage - 1}
          onClick={() => setPage(maxPage - 1)}
          className={page === maxPage - 1 ? 'active-page' : ''}
          style={{ margin: '0 5px' }}
        >
          {maxPage}
        </button>
      );
    }
    return pageNumbers;
  };

  const handleBack = () => {
    navigate(-1); // Go back to the previous page
  };

  return (
    <div className="search-results-page">
      <button onClick={handleBack}>Back</button>
      <h2>Search Results for "{query}"</h2>
      {isLoading && <p>Loading...</p>}
      {searchResults.length > 0 ? (
        <ul>
          {searchResults.map(article => (
            <li key={article.article.articleId}>
              <div>
                <a href={article.article.paperPdf} className="article-title">{article.article.articleTitle}</a>
                <p className="author-info">{article.authorNames.join(', ')}</p>
                <p className="publication-date">Publication Date: {new Date(article.article.publicationDate).toLocaleDateString()}</p>
              </div>
            </li>
          ))}
        </ul>
      ) : (
        !isLoading && <p>No results found.</p>
      )}
      <div className="pagination-buttons">
        <button onClick={handlePrevPage} disabled={page === 0} style={{ marginLeft: '45%' }}>
          <FontAwesomeIcon icon={faArrowLeft} />
        </button>
        {renderPageNumbers()}
        <button onClick={handleNextPage} disabled={page === (maxPage - 1)}>
          <FontAwesomeIcon icon={faArrowRight} />
        </button>
      </div>
    </div>
  );
};

export default SearchResults;
