import React from 'react';
import { QueryClient, QueryClientProvider, useQuery } from 'react-query';


// Mock data
const mockArticles = [
  {
    "articleId": 3428,
    "paperPdf": null,
    "affiliation": "Bogazici University",
    "doi": "/10.1109/icc.2011.5962989\"",
    "articleTitle": "Modulation Techniques for Communication via Diffusion in Nanonetworks",
    "publicationDate": "2011-05-31T21:00:00.000+00:00",
    "citationCount": 326,
    "openAccess": false,
    "rejected": false
  },
  {
    "articleId": 3429,
    "paperPdf": null,
    "affiliation": "Bogazici University",
    "doi": "/10.1109/lcomm.2014.2320917\"",
    "articleTitle": "Three-Dimensional Channel Characteristics for Molecular Communications With an Absorbing Receiver",
    "publicationDate": "2014-05-31T21:00:00.000+00:00",
    "citationCount": 308,
    "openAccess": false,
    "rejected": false
  },
  // Add more mock articles here
];

const fetchArticles = async () => {
  // Simulate async behavior by wrapping the mock data in a Promise
  return Promise.resolve(mockArticles);
};

const Profile = () => {
  const { data: articles, isLoading, error } = useQuery('articles', fetchArticles);

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error fetching data!</div>;
  }

  return (
    <div className="research-portal">
      <div className="articles-list">
        <h1>Articles</h1>
        {articles.map(article => (
          <div key={article.articleId} className="article-item">
            <h2>{article.articleTitle}</h2>
            <p>Affiliation: {article.affiliation}</p>
            <p>DOI: {article.doi}</p>
            <p>Publication Date: {new Date(article.publicationDate).toLocaleDateString()}</p>
            <p>Citation Count: {article.citationCount}</p>
            <p>Open Access: {article.openAccess ? "Yes" : "No"}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Profile;
