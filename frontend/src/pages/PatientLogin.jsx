import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './PatientLanding.css';

export default function PatientLogin() {
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [query, setQuery] = useState('');
  const [selectedId, setSelectedId] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchPatients = async () => {
      try {
        setLoading(true);
        const resp = await fetch('http://localhost:8080/patient/viewAll');
        if (!resp.ok) throw new Error('Failed to fetch patients');
        const data = await resp.json();
        setPatients(data || []);
      } catch (e) {
        console.error(e);
        setPatients([]);
      } finally {
        setLoading(false);
      }
    };
    fetchPatients();
  }, []);

  const filtered = patients.filter(p => {
    if (!query) return true;
    const q = query.toLowerCase();
    return (p.firstName && p.firstName.toLowerCase().includes(q)) || (p.lastName && p.lastName.toLowerCase().includes(q)) || (p.address && p.address.toLowerCase().includes(q));
  });

  const handleLogin = () => {
    if (!selectedId) return alert('Please select a patient to login');
    localStorage.setItem('patientId', String(selectedId));
    navigate('/');
  };

  return (
    <main className="patient-landing-page">
      <section className="hero">
        <div className="container">
          <h1 className="hero-title">Patient Login</h1>
          <p className="hero-subtitle">Select your patient profile to sign in (for demo only).</p>

          <div className="search-row" style={{marginTop: 20}}>
            <input className="search-input" placeholder="Search patient by name or address..." value={query} onChange={e => setQuery(e.target.value)} />
          </div>
        </div>
      </section>

      <section className="doctors-section container" style={{paddingTop: 22}}>
        {loading ? (
          <div>Loading patients...</div>
        ) : (
          <div style={{display: 'grid', gap: 10}}>
            {filtered.map(p => (
              <div key={p.patientId} style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: 12, background: '#fff', borderRadius: 8}}>
                <div>
                  <div style={{fontWeight: 700}}>{p.firstName} {p.lastName}</div>
                  <div style={{color: '#64748b'}}>{p.address}</div>
                </div>
                <div>
                  <input type="radio" name="patient" value={p.patientId} checked={String(selectedId) === String(p.patientId)} onChange={() => setSelectedId(p.patientId)} />
                </div>
              </div>
            ))}

            <div style={{display: 'flex', gap: 10, marginTop: 12}}>
              <button className="btn btn-view" onClick={handleLogin}>Login</button>
              <button className="btn" onClick={() => { localStorage.removeItem('patientId'); navigate('/'); }}>Cancel</button>
            </div>
          </div>
        )}
      </section>
    </main>
  );
}

