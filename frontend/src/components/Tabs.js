import React, { useState } from 'react';

const Tabs = ({ tabs, defaultTab, onTabChange }) => {
  const [activeTab, setActiveTab] = useState(defaultTab);

  const handleTabClick = (tab) => {
    setActiveTab(tab);
    onTabChange(tab);
  };

  return (
    <div className="tabs">
      {tabs.map((tab) => (
        <button
          key={tab}
          onClick={() => handleTabClick(tab)}
          className={tab === activeTab ? 'active' : ''}
        >
          {tab}
        </button>
      ))}
    </div>
  );
};

export default Tabs;
