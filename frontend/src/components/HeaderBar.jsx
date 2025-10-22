import React from 'react';
import './HeaderBar.css';

export default function HeaderBar() {
  return (
    <header className="header-bar">
      <div className="header-container container">
        <div className="brand">
          <div className="brand-logo" aria-hidden>
            {/* simple heart/outline logo */}
            <svg width="34" height="34" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M20.8 7.3c0 5.3-8 10-8 10s-8-4.7-8-10a5 5 0 0 1 9.2-2.1A5 5 0 0 1 20.8 7.3z" stroke="#DFF6F0" strokeWidth="1.6" fill="none" />
            </svg>
          </div>
          <div className="brand-text">DocLink</div>
        </div>
        <nav className="header-actions">
          <a className="header-link" href="#">Live Updates</a>
        </nav>
      </div>
    </header>
  );
}
