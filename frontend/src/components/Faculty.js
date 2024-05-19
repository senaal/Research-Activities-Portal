import React, { useState, useEffect } from 'react';
import { PieChart, LineChart } from '@mui/x-charts';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Tabs from './Tabs'; 
import { faArrowLeft, faArrowRight } from '@fortawesome/free-solid-svg-icons';
import HorizontalScroll from './HorizontalScroll';
import { useParams } from 'react-router-dom'; // Import useParams to get parameters from URL

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


const Faculty = () => {
    const [articles, setArticles] = useState([]);
    const [maxPage, setMaxPage] = useState(0);
    const [page, setPage] = useState(0);
    const [size, setSize] = useState(10);
    const [activeTab, setActiveTab] = useState('Scientific Articles'); 
    const [members, setMembers] = useState([]);
    const [name, setName] = useState("");
    const [sortBy, setSortBy] = useState('publicationDate');
    const [sortOrder, setSortOrder] = useState('DESC');
    const { id } = useParams(); 
    

  useEffect(() => {
    const fetchData = async () => {
      try {
        const articlesResponse = await fetch(`http://localhost:8080/article/faculty/${id}?page=${page}&size=${size}&sortOrder=${sortOrder}&sortBy=${sortBy}`);
        const articlesData = await articlesResponse.json();
        setArticles(articlesData.content);
        setMaxPage(articlesData.totalPages);

      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    fetchData();
  }, [id, page, size,sortBy, sortOrder]);

  useEffect(() => {
    const fetchDataMembers = async () => {
      try {
        const facultyMembersResponse = await fetch(`http://localhost:8080/facultymember/`);
        let data = await facultyMembersResponse.json();
        const filteredMembers = data.filter(member => member.department.facultyId.facultyId === parseInt(id));
        setMembers(filteredMembers);
        setName(filteredMembers[0].department.facultyId.facultyName);
        
     

      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    fetchDataMembers();
  }, [id]); 

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
    <div>
      <div className='department-page'>
        <div className='department-header'>
          <h1>{name}</h1>
        </div>
      </div>
      <div className='charts'>
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
                highlightScope: { faded: 'global', highlighted: 'item' },
                faded: { innerRadius: 30, additionalRadius: -30, color: 'gray' },
              },
            ]}
            width={700}
            height={400}
          />
        </div>
        <div className='chart'>
          <LineChart
            xAxis={[
              {
                id: 'Years',
                data: years,
                scaleType: 'time',
                valueFormatter: (date) => date.getFullYear().toString(),
              },
            ]}
            series={[
              {
                id: 'Cmpe',
                label: 'Citations',
                data: citations,
                stack: 'total',
                area: false,
                showMark: false,
              },
            ]}
            width={700}
            height={400}
          />
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
                  article && article.article ? (
                    <li key={article.article.articleId}>
                      <div>
                        <a href={article.article.paperPdf} className="article-title">{article.article.articleTitle}</a>
                        <p className="author-info"> {article.authorNames.join(', ')}</p>
                        <p className="publication-date">Publication Date: {new Date(article.article.publicationDate).toLocaleDateString()}</p>
                      </div>
                    </li>
                  ) : null
                ))}
              </ul>
              {/* Pagination controls */}
              <div className="pagination-buttons">
                <button onClick={handlePrevPage} disabled={page === 0} style={{ marginLeft: '45%' }}>
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

export default Faculty;
