import React, { useEffect, useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEnvelope, faPhone, faArrowLeft, faArrowRight } from '@fortawesome/free-solid-svg-icons';
import { useParams } from 'react-router-dom';
import { PieChart, LineChart } from '@mui/x-charts';
import './profile.css';

const Profile = () => {
  const [author, setAuthor] = useState(null);
  const [articles, setArticles] = useState([]);
  const [maxPage, setMaxPage] = useState(0);
  const [page, setpage] = useState(0);
  const [size, setsize] = useState(10);

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
        const articlesResponse = await fetch(`http://localhost:8080/article/author/${id}?page=${page}&size=${size}`);
        const articlesData = await articlesResponse.json();
        setArticles(articlesData.content);
        setMaxPage(articlesData.totalPages);
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    fetchData();
  }, [id, page, size]);

  const handleNextPage = () => {
    setpage(page + 1);
  };

  const handlePrevPage = () => {
    if (page > 0) {
      setpage(page - 1);
    }
  };

  const years = [
    new Date(2012, 0, 1),
    new Date(2013, 0, 1),
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
    3549, 3616, 6845, 7613, 4553, 6727, 3669, 3657,
    5663, 6527, 6827, 7124, 918,
  ];

  return (
    <div className="author">
      
      <div className="main-content">
      <div className='charts'>
            <div>
            <PieChart
                series={[
                    {
                    data: [
                        { id: 0, value: 30, label: 'Telecommunication' },
                        { id: 1, value: 25, label: 'Computer Networks' },
                        { id: 2, value: 20, label: 'Algorithms' },
                        { id: 3, value: 15, label: 'Artifical Intelligence' },
                        { id: 4, value: 10, label: 'Internet of Things' },
                    ],
                    },
                ]}
                width={500}
                height={200}
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
                width={400}
                height={200}
                />
            </div>
         </div>
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
        {/* Pagination controls */}
        <div className="pagination-buttons">
          <button onClick={handlePrevPage} disabled={page === 0}  style={{ marginLeft: '60%' }}>
            <FontAwesomeIcon icon={faArrowLeft} />
          </button>
          <button onClick={handleNextPage} disabled={page === (maxPage-1)}>
            <FontAwesomeIcon icon={faArrowRight} />
          </button>
        </div>
      </div>
      <div className="contact-info">
        <img src={author && `${author.photo}`} alt="Author" className="author-photo" />
        <h1>{author && `${author.title} ${author.authorName}`}</h1>
        <p>{author && author.departmentId.departmentName}</p>
        <h2 style={{ marginTop: '30px' }}> {'Contact Info'}</h2>
        <p><FontAwesomeIcon icon={faEnvelope} />  {author && author.email}</p>
        <p><FontAwesomeIcon icon={faPhone} /> {author && author.phone} </p>
        <p style={{ marginTop: '60px' }}> <img src={require("./photos/index.PNG")}  alt="Index " className="icon" /> <strong>h-index:</strong> {author && author.hindex}</p>
        <p><img src={require("./photos/index.PNG")} alt="Index "  className="icon" /> <strong>Citation Count:</strong> {author && author.citedByCount}</p>
        <div className="number-of">
        <div className="count">
          <div className="article-count-circle">
            <span>{author && author.articleCount}</span>
          </div>
          <p className="article-count-label"><strong>Articles</strong> </p>
        </div>
        <div className="count">
          <div className="project-count-circle">
            <span>{author && author.projectCount}</span>
          </div>
          <p className="project-count-label"><strong>Projects</strong> </p>
        </div>
        <div className="count">
          <div className="project-count-circle">
            <span>{7}</span>
          </div>
          <p className="project-count-label"><strong>Research Areas</strong> </p>
        </div>
      </div>
      </div>
      
    </div>
  );
};  

export default Profile;
