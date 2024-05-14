import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import './department.css';
import { PieChart, LineChart } from '@mui/x-charts';

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


const Department = ({ departments }) => {
  const { id } = useParams();
  const [articles, setArticles] = useState([]);
  const [departmentName, setDepartmentName] = useState('');

  useEffect(() => {
    fetch(`http://localhost:8080/article/department/${id}`)
      .then(response => response.json())
      .then(data => {
        setArticles(data);
        setDepartmentName(data[0].department.departmentName);
      })
      .catch(error => console.error('Error fetching articles:', error));
  }, [id]);


  return (
    <div>
      <div className='department-page'>
        <div className='department-header'>
          <h1>{departmentName}</h1>
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
      <div className='articles'>
        <h2>Related Articles</h2>
        <ul>
          {articles.map(article => (
            <li key={article.article.articleId}>
              <div>
                <a  href={article.article.paperPdf} className="article-title">{article.article.articleTitle}</a>
                <p className="author-info"> {article.authors.join(', ')}</p>
                <p className="publication-date">Publication Date: {new Date(article.article.publicationDate).toLocaleDateString()}</p>
              </div>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default Department;
