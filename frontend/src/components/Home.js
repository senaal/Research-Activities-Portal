import React, { useState, useEffect } from 'react';
import { LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import PieChart from './PieChart';
import Tabs from './Tabs';
import { faArrowLeft, faArrowRight } from '@fortawesome/free-solid-svg-icons';
import HorizontalScroll from './HorizontalScroll';
import BubbleMap from './BubbleMap'; 


function Home() {
  const [articles, setArticles] = useState([]);
  const [projects, setProjects] = useState([]);
  const [maxPage, setMaxPage] = useState(0);
  const [maxPageProject, setMaxPageProject] = useState(0);
  const [page, setPage] = useState(0);
  const [pageProject, setPageProject] = useState(0);
  const [size, setSize] = useState(10);
  const [activeTab, setActiveTab] = useState('Scientific Articles');
  const [members, setMembers] = useState([]);
  const [sortBy, setSortBy] = useState('publicationDate');
  const [sortOrder, setSortOrder] = useState('DESC');
  const [sortByProject, setSortByProject] = useState('endDate');
  const [sortOrderProject, setSortOrderProject] = useState('DESC');
  const [researchAreasData, setResearchAreasData] = useState([]);
  const [years, setYears] = useState([]);
  const [citations, setCitations] = useState([]);
  const [works, setWorks] = useState([]);


  useEffect(() => {
    const fetchData = async () => {
      try {
        const articlesResponse = await fetch(`http://localhost:8080/article/scientific_articles?page=${page}&size=${size}&sortOrder=${sortOrder}&sortBy=${sortBy}`);
        const articlesData = await articlesResponse.json();
        setArticles(articlesData.content);
        setMaxPage(articlesData.totalPages);

        const facultyMembersResponse = await fetch(`http://localhost:8080/facultymember/`);
        let data = await facultyMembersResponse.json();
        console.log(data)
        setMembers(data);

        const projectsResponse = await fetch(`http://localhost:8080/project/?page=${pageProject}&size=10&sortOrder=${sortOrderProject}&sortBy=${sortByProject}`);
        const projectsData = await projectsResponse.json();
        setProjects(projectsData.content);
        setMaxPageProject(projectsData.totalPages);
        
        const citationsResponse = await fetch(`http://localhost:8080/citation/`);
        let citations = await citationsResponse.json();
        setYears(citations.years.map(year => new Date(year, 0, 1)));
        setCitations(citations.citations);
        setWorks(citations.worksCount);


      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };
    const fetchResearchAreas = async () => {
      try {
        const response = await fetch(`http://localhost:8080/faculty/research-area/`);
        let data = await response.json();
        setResearchAreasData(data);
      } catch (error) {
        console.error('Error fetching research areas data:', error);
      }
    };

    fetchData();
    fetchResearchAreas();
  }, [page, size,sortBy, sortOrder, sortByProject, sortOrderProject, pageProject]);


  const handleTabChange = (tab) => {
    setActiveTab(tab);
  };
  const handleNextPage = () => {
    setPage(page + 1);
  };

  const handlePrevPage = () => {
    if (page > 0) {
      setPage(page - 1);
    }
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


  const handleSortByChange = (event) => {
    setSortBy(event.target.value);
  };

  const handleSortOrderChange = (event) => {
    setSortOrder(event.target.value);
  };

  const handleSortByChangeProject = (event) => {
    setSortByProject(event.target.value);
  };
  
  const handleSortOrderChangeProject = (event) => {
    setSortOrderProject(event.target.value);
  };

return (
    <div className="App">
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
      <div className="tabs" style={{ marginTop: '20px' }}>
      </div>

      <Tabs
        tabs={['Scientific Articles', 'Projects', 'Faculty Members','Works Count-University Map']}
        defaultTab="Scientific Articles"
        onTabChange={handleTabChange}
      />
      {activeTab === 'Scientific Articles' && (
        <>
          <div className="sort-options">
            <select id="sortBy" value={sortBy} onChange={handleSortByChange}>
              <option value="publicationDate">Publication Date</option>
              <option value="citationCount">Citation Count</option>
            </select>

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
            <button onClick={handlePrevPage} disabled={page === 0} style={{ marginLeft: '35%' }}>
              <FontAwesomeIcon icon={faArrowLeft} />
            </button>
            {renderPageNumbers()}
            <button onClick={handleNextPage} disabled={page === (maxPage - 1)}>
              <FontAwesomeIcon icon={faArrowRight} />
            </button>
          </div>
        </>
      )}
     {activeTab === 'Faculty Members' && (
              <div>
                {members.map(department => (
                <div key={department.department.departmentId}>
                  <div className='department'>
                    <h1>{department.department.departmentName}</h1>
                    <HorizontalScroll items={department.members} /> {}
                  </div>  
                </div>
              ))}
              </div>
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
                  ):null
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
          {activeTab === 'Works Count-University Map' && ( 
            <BubbleMap />
          )}
        </div>
        

  );
}

export default Home;