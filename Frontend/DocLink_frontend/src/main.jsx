import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App.jsx';

// CSS import order: base first, Global LAST
import './index.css';   // if you use it
import './App.css';     // if you have src/App.css
import './Style/Global.css'; // keep this last so it overrides others

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);