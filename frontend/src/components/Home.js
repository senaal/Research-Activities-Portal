import React, { useState, useEffect } from 'react';
import { LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import PieChart from './PieChart';
import Tabs from './Tabs'; 
import { faArrowLeft, faArrowRight } from '@fortawesome/free-solid-svg-icons';
import HorizontalScroll from './HorizontalScroll';


const years = [
  new Date(2014, 0, 1),
  new Date(2015, 0, 1),
  new Date(2016, 0, 1),
  new Date(2017, 0, 1),
  new Date(2018, 0, 1),
  new Date(2019, 0, 1),
  new Date(2020, 0, 1),
  new Date(2021, 0, 1),
  new Date(2022, 0, 1),
  new Date(2023, 0, 1),
  new Date(2024, 0, 1),
];
const citations = [
  6845, 7613, 4553, 6727, 3669, 3657,
  5663, 6527, 6827, 7124, 918,
];


function Home() {
  const [articles, setArticles] = useState([]);
  const [maxPage, setMaxPage] = useState(0);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [activeTab, setActiveTab] = useState('Scientific Articles'); 
  const [members, setMembers] = useState([]);
  const [sortBy, setSortBy] = useState('publicationDate');
  const [sortOrder, setSortOrder] = useState('DESC');
  const [researchAreasData, setResearchAreasData] = useState([]);


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
  }, [page, size,sortBy, sortOrder]);

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


  const handleSortByChange = (event) => {
    setSortBy(event.target.value);
  };
  
  const handleSortOrderChange = (event) => {
    setSortOrder(event.target.value);
  };

return (
    <div className="App">
      <div className='charts'>
      <div className='chart'>
          <h2 style={{ textAlign: 'center' }}>Research Areas</h2>
          <ResponsiveContainer width="100%" height={300}>
              <PieChart data={researchAreasData} />
     
          </ResponsiveContainer>
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
                labelFormatter={(value) => `Year: ${new Date(value).getFullYear()}`} // Customize tooltip label
                formatter={(value) => [`Citations: ${value}`, '']} // Customize tooltip value
              />
              <Line type="monotone" dataKey="citations" stroke="#8884d8" dot={false}/>
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>
        <div className="tabs" style={{ marginTop: '20px' }}>
        </div>

        <Tabs
          tabs={['Scientific Articles', 'Projects', 'Faculty Members']}
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
        </div>
        

  );
}

export default Home;
