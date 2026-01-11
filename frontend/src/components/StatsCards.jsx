import React, { useEffect, useState } from 'react';
import './StatsCards.css';

function safeCountFromResponse(resp) {
  if (!resp) return 0;
  if (Array.isArray(resp)) return resp.length;
  if (typeof resp === 'object') {
    if ('count' in resp && typeof resp.count === 'number') return resp.count;
    // try to detect list inside common wrapper
    for (const k of Object.keys(resp)) {
      if (Array.isArray(resp[k])) return resp[k].length;
    }
  }
  return 0;
}

export default function StatsCards() {
  const [counts, setCounts] = useState({
    doctors: null,
    hospitals: null,
    dispensaries: null,
    pharmacies: null,
    patients: null
  });

  useEffect(() => {
    const backend = 'http://localhost:8080';

    const endpoints = {
      doctors: `${backend}/doctor/viewAll`,
      hospitals: `${backend}/hospital/viewAll`,
      dispensaries: `${backend}/dispensary/viewAll`,
      pharmacies: `${backend}/pharmacy/viewAll`,
      patients: `${backend}/patient/viewAll`
    };

    const fetchOne = async (key, url) => {
      try {
        const r = await fetch(url);
        if (!r.ok) throw new Error('bad');
        const data = await r.json();
        return safeCountFromResponse(data);
      } catch (e) {
        return null; // null means unavailable
      }
    };

    (async () => {
      const results = {};
      await Promise.all(Object.keys(endpoints).map(async (k) => {
        results[k] = await fetchOne(k, endpoints[k]);
      }));
      setCounts(results);
    })();
  }, []);

  const metrics = [
    { key: 'doctors', title: 'Available', subtitle: 'Doctors', color: 'green' },
    { key: 'hospitals', title: 'Registered', subtitle: 'Hospitals', color: 'blue' },
    { key: 'dispensaries', title: 'Registered', subtitle: 'Dispensaries', color: 'teal' },
    { key: 'pharmacies', title: 'Registered', subtitle: 'Pharmacies', color: 'purple' },
    { key: 'patients', title: 'Total', subtitle: 'Patients', color: 'indigo' }
  ];

  // Use the site's blue->teal theme for all stat cards so they match the header and hero
  const themePrimary = ['#1e90ff', '#11998e'];
  const colorMap = {
    green: themePrimary,
    blue: themePrimary,
    teal: themePrimary,
    purple: themePrimary,
    indigo: themePrimary
  };

  return (
    <div className="stats-cards-wrap container">
      <div className="stats-cards">
        {metrics.map((m) => {
          const value = counts[m.key];
          const display = value === null ? '—' : value;
          const colors = colorMap[m.color] || ['#e5e7eb', '#d1d5db'];
          return (
            <div key={m.key} className="stats-card" style={{ background: `linear-gradient(90deg, ${colors[0]} 0%, ${colors[1]} 100%)` }}>
              <div className="stats-card-body">
                <div className="stats-left">
                  <div className="stats-label">{m.title}</div>
                  <div className="stats-value">{display}</div>
                  <div className="stats-sub">{m.subtitle}</div>
                </div>
                <div className="stats-icon" aria-hidden>
                  {(() => {
                    switch (m.key) {
                      case 'doctors':
                        return (
                          <svg width="36" height="36" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M6 14v1a5 5 0 0 0 10 0v-1" stroke="rgba(255,255,255,0.95)" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" />
                            <circle cx="8" cy="7" r="2.2" stroke="rgba(255,255,255,0.95)" strokeWidth="1.4" fill="none" />
                            <circle cx="16" cy="7" r="2.2" stroke="rgba(255,255,255,0.95)" strokeWidth="1.4" fill="none" />
                          </svg>
                        );
                      case 'hospitals':
                        return (
                          <svg width="36" height="36" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M3 21V7a1 1 0 0 1 1-1h16a1 1 0 0 1 1 1v14" stroke="rgba(255,255,255,0.95)" strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round" />
                            <path d="M7 21V11h10v10" stroke="rgba(255,255,255,0.95)" strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round" />
                            <path d="M12 7v6" stroke="rgba(255,255,255,0.95)" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" />
                            <path d="M10 9h4" stroke="rgba(255,255,255,0.95)" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" />
                          </svg>
                        );
                      case 'dispensaries':
                      case 'pharmacies':
                        return (
                          <svg width="36" height="36" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <rect x="3" y="9" width="18" height="10" rx="3" stroke="rgba(255,255,255,0.95)" strokeWidth="1.4" fill="none" />
                            <path d="M3 13h18" stroke="rgba(255,255,255,0.95)" strokeWidth="1.4" strokeLinecap="round" />
                            <path d="M8 6h8v3H8z" stroke="rgba(255,255,255,0.95)" strokeWidth="1.2" fill="rgba(255,255,255,0.06)" />
                          </svg>
                        );
                      case 'patients':
                        return (
                          <svg width="36" height="36" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <circle cx="9" cy="8" r="2.2" stroke="rgba(255,255,255,0.95)" strokeWidth="1.4" fill="none" />
                            <path d="M3 21v-1a4 4 0 0 1 4-4h6a4 4 0 0 1 4 4v1" stroke="rgba(255,255,255,0.95)" strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round" />
                            <circle cx="18" cy="8" r="1.6" stroke="rgba(255,255,255,0.9)" strokeWidth="1.2" fill="none" />
                          </svg>
                        );
                      default:
                        return (
                          <svg width="36" height="36" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M6 12h12" stroke="rgba(255,255,255,0.95)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
                            <path d="M6 16h8" stroke="rgba(255,255,255,0.85)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
                          </svg>
                        );
                    }
                  })()}
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
