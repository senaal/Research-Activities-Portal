import React, { useEffect, useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEnvelope, faPhone, faArrowLeft, faArrowRight } from '@fortawesome/free-solid-svg-icons';
import { useParams } from 'react-router-dom';
import { LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';
import { PieChart } from '@mui/x-charts';
import './profile.css';
import Tabs from './Tabs'; 

const Profile = () => {
  const [author, setAuthor] = useState(null);
  const [articles, setArticles] = useState([]);
  const [maxPage, setMaxPage] = useState(0);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [sortBy, setSortBy] = useState('publicationDate');
  const [sortOrder, setSortOrder] = useState('DESC');
  const [activeTab, setActiveTab] = useState('Scientific Articles'); 
  const [years, setYears] = useState([]);
  const [citations, setCitations] = useState([]);

  const { id } = useParams();

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Fetch author data
        const authorResponse = await fetch(`http://localhost:8080/facultymember/${id}`);
        let data = await authorResponse.json();
        let authorData = data.member;
        authorData.articleCount = data.numberOfArticles;
        authorData.projectCount = data.numberOfProjects;
        setAuthor(authorData);

        // Fetch articles data for the author with pagination
        const articlesResponse = await fetch(`http://localhost:8080/article/author/${id}?page=${page}&size=${size}&sortOrder=${sortOrder}&sortBy=${sortBy}`);
        const articlesData = await articlesResponse.json();
        setArticles(articlesData.content);
        setMaxPage(articlesData.totalPages);

        const citationsResponse = await fetch(`http://localhost:8080/citation/${id}`);
        let citations = await citationsResponse.json();
        setYears(citations.years.map(year => new Date(year, 0, 1))); 
        setCitations(citations.citations);

      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    fetchData();
  }, [id, page, size, sortBy, sortOrder]);

  const handleNextPage = () => {
    setPage(page + 1);
  };

  const handlePrevPage = () => {
    if (page > 0) {
      setPage(page - 1);
    }
  };

  const handleTabChange = (tab) => {
    setActiveTab(tab);
  };

  const handleSortByChange = (event) => {
    setSortBy(event.target.value);
  };
  
  const handleSortOrderChange = (event) => {
    setSortOrder(event.target.value);
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
 
  return (
    <div className="author" style={{ display: 'flex', flexDirection: 'row' }}>
      <div className="contact-info" style={{ marginLeft: '20px' }}>
        <img src={author && `${author.photo}`} alt="Author" className="author-photo" />
        <h1>{author && `${author.title} ${author.authorName}`}</h1>
        <div className='department'>{author && author.departmentId.departmentName}</div>
        <h2 style={{ marginTop: '30px' }}> {'Contact Info'}</h2>
        <div className='contact-line'>
          <p><FontAwesomeIcon icon={faEnvelope} />  {author && author.email}</p>
          <p><FontAwesomeIcon icon={faPhone} /> {author && author.phone} </p>
          <p style={{ marginTop: '60px' }}> <img src={require("./photos/index.PNG")} alt="Index " className="icon" /> <strong>h-index:</strong> {author && author.hindex}</p>
          <p><img src={require("./photos/index.PNG")} alt="Index " className="icon" /> <strong>Citation Count:</strong> {author && author.citedByCount}</p>
          <div className="number-of">
            <div className="count">
              <div className="article-count-circle">
                <span>{author && author.articleCount}</span>
              </div>
              <h3 className="article-count-label">Articles </h3>
            </div>
            <div className="count">
              <div className="project-count-circle">
                <span>{author && author.projectCount}</span>
              </div>
              <h3 className="project-count-label"><strong>Projects</strong> </h3>
            </div>
            <div className="count">
              <div className="project-count-circle">
                <span>{7}</span>
              </div>
              <h3 className="project-count-label"><strong>Research Areas</strong> </h3>
            </div>
          </div>
        </div>
      </div>
      <div className="author-header" style={{ marginLeft: '20px', flex: 2 }}>
        <div className='charts' style={{ display: 'flex' }}>
          <div>
            <PieChart
              series={[
                {
                  data: [
                    { id: 0, value: 30, label: 'Telecommunication' },
                    { id: 1, value: 25, label: 'Computer Networks' },
                    { id: 2, value: 20, label: 'Algorithms' },
                    { id: 3, value: 15, label: 'Artificial Intelligence' },
                    { id: 4, value: 10, label: 'Internet of Things' },
                  ],
                },
              ]}
              width={500}
              height={200}
            />
          </div>
          <div className='chart'>
          <h2 style={{ textAlign: 'center' }}>Citations Over the Years</h2>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={years.map((year, index) => ({ year, citations: citations[index] }))}>
              <XAxis
                dataKey="year"
                tickFormatter={(tick) => new Date(tick).getFullYear()}
                tick={{ fontSize: 10, angle: -45, textAnchor: 'end' }}
                interval={0}
              />
              <YAxis
                label={{ value: 'Citations', angle: -90, position: 'insideLeft' }}
              />
              <Tooltip
                labelFormatter={(value) => `Year: ${new Date(value).getFullYear()}`} 
                formatter={(value) => [`Citations: ${value}`, '']} 
              />
              <Line type="monotone" dataKey="citations" stroke="#8884d8" dot={false}/>
            </LineChart>
          </ResponsiveContainer>
        </div>
        </div>
        <div className="content-container" style={{ display: 'flex', flexDirection: 'row', marginTop: '20px' }}>
          <div className="main-content" style={{ flex: 1 }}>
            <div className="tabs" style={{ marginTop: '20px' }}>
              <Tabs
                tabs={['Scientific Articles', 'Projects']}
                defaultTab="Scientific Articles"
                onTabChange={handleTabChange}
              />
            </div>
            {activeTab === 'Scientific Articles' && (
              <>
              <div className="sort-options">
                <label htmlFor="sortBy"></label>
                <select id="sortBy" value={sortBy} onChange={handleSortByChange}>
                  <option value="publicationDate">Publication Date</option>
                  <option value="citationCount">Citation Count</option>
                </select>

                <label htmlFor="sortOrder"> </label>
                <select id="sortOrder" value={sortOrder} onChange={handleSortOrderChange}>
                  <option value="ASC">Ascending</option>
                  <option value="DESC">Descending</option>
                </select>
              </div>
                <ul>
                  {articles.map(article => (
                    <li key={article.article.articleId}>
                      <div>
                        <a href={article.article.paperPdf} className="article-title">{article.article.articleTitle}</a>
                        <p className="author-info"> {article.authorNames.join(', ')}</p>
                        <p className="publication-date">Publication Date: {new Date(article.article.publicationDate).toLocaleDateString()}</p>
                      </div>
                    </li>
                  ))}
                </ul>
                {/* Pagination controls */}
                <div className="pagination-buttons">
                  <button onClick={handlePrevPage} disabled={page === 0} style={{ marginLeft: '40%' }}>
                    <FontAwesomeIcon icon={faArrowLeft} />
                  </button>
                  {renderPageNumbers()}
                  <button onClick={handleNextPage} disabled={page === (maxPage - 1)}>
                    <FontAwesomeIcon icon={faArrowRight} />
                  </button>
                </div>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};  

export default Profile;
