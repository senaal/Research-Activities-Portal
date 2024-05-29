import React, { useEffect, useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEnvelope, faPhone, faArrowLeft, faArrowRight } from '@fortawesome/free-solid-svg-icons';
import { useParams, useNavigate } from 'react-router-dom';
import { LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';
import PieChart from './PieChart';
import './profile.css';
import Tabs from './Tabs';
import MathRenderer from './MathRenderer';



const ProfileEdit = () => {
  const [author, setAuthor] = useState(null);
  const [articles, setArticles] = useState([]);
  const [maxPage, setMaxPage] = useState(0);
  const [researchAreasData, setResearchAreasData] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [sortBy, setSortBy] = useState('publicationDate');
  const [sortOrder, setSortOrder] = useState('DESC');
  const [activeTab, setActiveTab] = useState('Scientific Articles');
  const [years, setYears] = useState([]);
  const [citations, setCitations] = useState([]);
  const [projects, setProjects] = useState([]);
  const [maxPageProject, setMaxPageProject] = useState(0);
  const [pageProject, setPageProject] = useState(0);
  const [sortByProject, setSortByProject] = useState('endDate');
  const [sortOrderProject, setSortOrderProject] = useState('DESC');
  const [works, setWorks] = useState([]);
  const [showCodeModal, setShowCodeModal] = useState(false);
  const [verificationCode, setVerificationCode] = useState('');
  const [token, setToken] = useState('');
  const [isEditMode, setIsEditMode] = useState(false);
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');



  const { id } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Fetch author data
        const authorResponse = await fetch(`http://localhost:8080/facultymember/${id}`);
        let data = await authorResponse.json();
        let authorData = data.member;
        authorData.articleCount = data.numberOfArticles;
        authorData.projectCount = data.numberOfProjects;
        authorData.researchAreaCount = data.numberOfResearchAreas;
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
        setWorks(citations.worksCount);

        const projectsResponse = await fetch(`http://localhost:8080/project/author/${id}?page=${pageProject}&size=10&sortOrder=${sortOrderProject}&sortBy=${sortByProject}`);
        const projectsData = await projectsResponse.json();
        setProjects(projectsData.content);
        setMaxPageProject(projectsData.totalPages);

      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };
    const fetchResearchAreas = async () => {
      try {
        const response = await fetch(`http://localhost:8080/facultymember/research-area/${id}`);
        let data = await response.json();
        setResearchAreasData(data);
      } catch (error) {
        console.error('Error fetching research areas data:', error);
      }
    };

    fetchData();
    fetchResearchAreas();
  }, [id, page, size, sortBy, sortOrder, sortByProject, sortOrderProject, pageProject, email, phone]);

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

  const handleNextPageProject = () => {
    setPageProject(page + 1);
  };

  const handlePrevPageProject = () => {
    if (page > 0) {
      setPageProject(page - 1);
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

  const renderPageNumbersProject = () => {
    const pageNumbers = [];
    if (maxPageProject <= 5) {
      for (let i = 0; i < maxPageProject; i++) {
        pageNumbers.push(
          <button
            key={i}
            onClick={() => setPageProject(i)}
            className={i === pageProject ? 'active-page' : ''}
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
          onClick={() => setPageProject(0)}
          className={pageProject === 0 ? 'active-page' : ''}
          style={{ margin: '0 5px' }}
        >
          1
        </button>
      );

      if (pageProject > 2) {
        pageNumbers.push(<span key="left-ellipsis">...</span>);
      }

      for (let i = Math.max(1, pageProject - 1); i <= Math.min(maxPageProject - 2, pageProject + 1); i++) {
        pageNumbers.push(
          <button
            key={i}
            onClick={() => setPageProject(i)}
            className={i === pageProject ? 'active-page' : ''}
            style={{ margin: '0 5px' }}
          >
            {i + 1}
          </button>
        );
      }

      if (pageProject < maxPageProject - 3) {
        pageNumbers.push(<span key="right-ellipsis">...</span>);
      }

      pageNumbers.push(
        <button
          key={maxPageProject - 1}
          onClick={() => setPage(maxPageProject - 1)}
          className={pageProject === maxPageProject - 1 ? 'active-page' : ''}
          style={{ margin: '0 5px' }}
        >
          {maxPageProject}
        </button>
      );
    }
    return pageNumbers;
  };

  const handleSortByChangeProject = (event) => {
    setSortByProject(event.target.value);
  };

  const handleSortOrderChangeProject = (event) => {
    setSortOrderProject(event.target.value);
  };

  const handleEditClick = () => {
    setIsEditMode(true);
  };

  const handleSaveClick = async () => {
    try {

      const token = sessionStorage.getItem('token');
      console.log(token);

      if (!token) {
        throw new Error('No token found');
      }

      const response = await fetch(`http://localhost:8080/facultymember/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ email, phone, photo: author.photo, title: author.title }),

      });

      if (response.ok) {
        setIsEditMode(false);
        // Optionally, fetch updated data from the server
      } else {
        console.error('Failed to update data');
      }
    } catch (error) {
      console.error('Error updating data:', error);
    }
  };

  return (
    <div className="author" style={{ display: 'flex', flexDirection: 'row' }}>
      <div className="contact-info" style={{ marginLeft: '20px' }}>
        <img src={author && `${author.photo}`} alt="Author" className="author-photo" />
        <h1>{author && `${author.title} ${author.authorName}`}</h1>
        <div className='department'>{author && author.departmentId.departmentName}</div>
        <h2 style={{ marginTop: '30px' }}> {'Contact Info'}</h2>
        <div className='contact-line'>
          <div className="author-details">
            <p>
              <FontAwesomeIcon icon={faEnvelope} />{' '}
              {isEditMode ? (
                <input
                  type="text"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />

              ) : (
                author && author.email
              )}
            </p>
            <p>
              <FontAwesomeIcon icon={faPhone} />{' '}
              {isEditMode ? (
                <input
                  type="text"
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                />
              ) : (
                author && author.phone
              )}
            </p>
            {isEditMode ? (
              <button onClick={handleSaveClick}>Save</button>
            ) : (
              <button onClick={handleEditClick}>Edit</button>
            )}
          </div>

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
                <span>{author && author.researchAreaCount}</span>
              </div>
              <h3 className="project-count-label"><strong>Research Areas</strong> </h3>
            </div>
          </div>
        </div>
      </div>
      <div className="author-header" style={{ marginLeft: '20px', flex: 2 }}>
        <div className='charts' style={{ display: 'flex' }}>
          <div className='chart'>
            <h2>Scientific Articles</h2>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={years.map((year, index) => ({ year, works: works[index] }))}>
                <XAxis
                  dataKey="year"
                  tickFormatter={(tick) => new Date(tick).getFullYear()}
                  tick={{ fontSize: 10, angle: -45, textAnchor: 'end' }}
                  interval={0}
                />
                <YAxis
                  label={{ value: 'Articles', angle: -90, position: 'insideLeft' }}
                />
                <Tooltip
                  labelFormatter={(value) => `Year: ${new Date(value).getFullYear()}`}
                  content={({ payload, label }) => {
                    if (payload && payload.length > 0) {
                      return (
                        <div style={{ backgroundColor: '#fff', padding: '5px' }}>
                          <p>Year: {new Date(label).getFullYear()}</p>
                          {payload.map((entry, index) => (
                            <p key={index}>{entry.name.charAt(0).toUpperCase() + entry.name.slice(1)}: {entry.value}</p>
                          ))}
                        </div>
                      );
                    }
                    return null;
                  }}
                />
                <Line type="monotone" dataKey="works" stroke="#82ca9d" dot={false} name="Works" />
              </LineChart>
            </ResponsiveContainer>
          </div>
          <div className='chart'>
            <h2>Research Areas</h2>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart data={researchAreasData} />

            </ResponsiveContainer>
          </div>

          <div className='chart'>
            <h2 >Citations</h2>
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
                  content={({ payload, label }) => {
                    if (payload && payload.length > 0) {
                      return (
                        <div style={{ backgroundColor: '#fff', padding: '5px' }}>
                          <p>Year: {new Date(label).getFullYear()}</p>
                          {payload.map((entry, index) => (
                            <p key={index}>{entry.name.charAt(0).toUpperCase() + entry.name.slice(1)}: {entry.value}</p>
                          ))}
                        </div>
                      );
                    }
                    return null;
                  }}
                />
                <Line type="monotone" dataKey="citations" stroke="#8884d8" dot={false} name="Citations" />
                <Line type="monotone" dataKey="works" stroke="#82ca9d" dot={false} name="Works" />
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
                        <a href={article.article.paperPdf} className="article-title">
                          <MathRenderer content={article.article.articleTitle} />
                        </a>                        <p className="author-info"> {article.authorNames.join(', ')}</p>
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
            {activeTab === 'Projects' && (
              <>
                <div className="sort-options">
                  <select id="sortByProject" value={sortByProject} onChange={handleSortByChangeProject}>
                    <option value="EndDate">End Date</option>
                  </select>

                  <select id="sortOrder" value={sortOrderProject} onChange={handleSortOrderChangeProject}>
                    <option value="ASC">Ascending</option>
                    <option value="DESC">Descending</option>
                  </select>
                </div>
                <ul className='projects'>
                  {projects.map(project => (
                    project && project.project ? (
                      <li key={project.project.projectId}>
                        <div>
                          <a href={project.project.link} className="project-title">{project.project.projectName}</a>
                          <p className="author-info"> {project.authorNames.join(', ')}</p>
                          <p className="end-date">End Date: {new Date(project.project.endDate).toLocaleDateString()}</p>
                        </div>
                      </li>
                    ) : null
                  ))}
                </ul>
                {/* Pagination controls */}
                <div className="pagination-buttons">
                  <button onClick={handlePrevPageProject} disabled={pageProject === 0} style={{ marginLeft: '35%' }}>
                    <FontAwesomeIcon icon={faArrowLeft} />
                  </button>
                  {renderPageNumbersProject()}
                  <button onClick={handleNextPageProject} disabled={pageProject === (maxPageProject - 1)}>
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

export default ProfileEdit;
