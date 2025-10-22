import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './PatientLanding.css';
import StatsCards from '../components/StatsCards';
import HeaderBar from '../components/HeaderBar';

function formatLocalDate(isoString) {
  if (!isoString) return '';
  try {
    const d = new Date(isoString);
    return d.toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' });
  } catch (e) {
    return isoString;
  }
}

function friendlyDayOfWeek(day) {
  if (!day) return '';
  // backend may send 'MONDAY' etc. convert to 'Monday'
  const s = String(day).toLowerCase();
  return s.charAt(0).toUpperCase() + s.slice(1);
}

export default function PatientLanding() {
  const [doctors, setDoctors] = useState([]);
  const [query, setQuery] = useState('');
  const [loading, setLoading] = useState(true);
  const [appointments, setAppointments] = useState([]);
  const [appointmentError, setAppointmentError] = useState(null);
  const [currentPatient, setCurrentPatient] = useState(null);
  const navigate = useNavigate();

  // Try to read the logged-in patient id from localStorage
  const patientId = localStorage.getItem('patientId');

  useEffect(() => {
    const fetchDoctors = async () => {
      try {
        setLoading(true);
        const resp = await fetch('http://localhost:8080/doctor/viewAll');
        if (!resp.ok) throw new Error('Failed to fetch doctors');
        const data = await resp.json();
        setDoctors(data || []);
      } catch (e) {
        console.error(e);
        setDoctors([]);
      } finally {
        setLoading(false);
      }
    };
    fetchDoctors();
  }, []);

  // load logged-in patient details for header and default location
  useEffect(() => {
    if (!patientId) {
      setCurrentPatient(null);
      return;
    }
    const fetchPatient = async () => {
      try {
        const resp = await fetch(`http://localhost:8080/patient/viewPatient?id=${patientId}`);
        if (!resp.ok) throw new Error('Failed to fetch patient');
        const data = await resp.json();
        setCurrentPatient(data || null);
      } catch (e) {
        console.error('Could not load patient details', e);
        setCurrentPatient(null);
      }
    };
    fetchPatient();
  }, [patientId]);

  useEffect(() => {
    if (!patientId) return;
    const fetchAppointments = async () => {
      try {
        const resp = await fetch(`http://localhost:8080/appointment/viewMyAppointments?id=${patientId}`);
        if (!resp.ok) throw new Error('Failed to fetch appointments');
        const data = await resp.json();
        // normalize keys to camelCase if backend uses different naming
        const normalized = (data || []).map(a => ({
          appointmentId: a.appointmentId || null,
          hospitalId: a.hospitalId || a.HospitalId || null,
          hospitalName: a.hospitalName || a.HospitalName || a.hospital || '',
          doctorId: a.doctorId || a.DoctorId || null,
          doctorName: a.doctorName || a.DoctorName || a.Doctor || '',
          seatNumber: a.seatNumber ?? a.seat ?? null,
          timeSlot: a.timeSlot || a.timeperiod || '',
          availableTime: a.availableTime || a.availableTime || null,
          dayOfWeek: a.dayOfWeek || a.DayOfWeek || null,
          raw: a
        }));
        setAppointments(normalized);
        setAppointmentError(null);
      } catch (e) {
        console.error(e);
        setAppointments([]);
        setAppointmentError(String(e.message || e));
      }
    };
    fetchAppointments();
  }, [patientId]);

  const filtered = doctors.filter(d => {
    if (!query) return true;
    const q = query.toLowerCase();
    return (d.name && d.name.toLowerCase().includes(q)) || (d.specialization && d.specialization.toLowerCase().includes(q));
  });

  const handleLogout = () => {
    localStorage.removeItem('patientId');
    setCurrentPatient(null);
    setAppointments([]);
    navigate('/');
  };

  return (
    <main className="patient-landing-page">
      <HeaderBar />
      <section className="hero">
        <div className="container" style={{position: 'relative'}}>
          <h1 className="hero-title">Find Your Doctor</h1>
          <p className="hero-subtitle">Browse our list of world-class specialists in Sri Lanka</p>

          {/* top-right login status */}
          <div style={{position: 'absolute', right: 0, top: 18}}>
            {currentPatient ? (
              <div style={{display: 'flex', alignItems: 'center', gap: 12}}>
                <div style={{textAlign: 'right'}}>
                  <div style={{fontWeight: 700}}>{currentPatient.patientName}</div>
                  <div style={{fontSize: 12, color: '#64748b'}}>{currentPatient.email}</div>
                </div>
                <button className="btn" onClick={handleLogout}>Logout</button>
              </div>
            ) : (
              <div style={{display: 'flex', gap: 8}}>
                <button className="btn btn-view" onClick={() => navigate('/login')}>Sign in</button>
              </div>
            )}
          </div>

          <div className="search-row">
            <div className="search-input-wrap">
              <span className="search-icon"></span>
              <input
                aria-label="search"
                className="search-input"
                placeholder="Search by name or specialization..."
                value={query}
                onChange={(e) => setQuery(e.target.value)}
              />
            </div>
            <button className="search-btn" onClick={() => { /* intentionally no-op; filtering is live */ }}>Search</button>
          </div>
          {/* Stats cards under the search bar */}
          <StatsCards />
        </div>
      </section>

      <section className="doctors-section container">
        <div className="doctors-grid">
          {loading ? (
            <div className="loading">Loading doctors...</div>
          ) : filtered.length === 0 ? (
            <div className="loading">No doctors found.</div>
          ) : (
            filtered.map((doc) => (
              <article key={doc.doctorId} className="doc-card">
                <div className="doc-card-image" aria-hidden>
                  <img
                    src={doc.image || '/default-avatar.svg'}
                    alt={`Dr. ${doc.name}`}
                    onError={(e) => { e.target.onerror = null; e.target.src = '/default-avatar.svg'; }}
                  />
                </div>
                <div className="doc-card-body">
                  <h3 className="doc-name">Dr. {doc.name}</h3>
                  <p className="doc-specialty">{doc.specialization}</p>
                  <div className="doc-actions">
                      <button className="btn btn-book" onClick={() => navigate(`/doctor/${doc.doctorId}`)}>Book Appointment</button>
                  </div>
                </div>
              </article>
            ))
          )}
        </div>
      </section>

      <section className="appointments-section">
        <div className="container">
          <h2 className="appointments-title">My Appointments</h2>
          <p className="appointments-sub">Here are your upcoming appointments.</p>

          {!patientId ? (
            <div className="appointments-empty">Please login as a patient to see your appointments (set localStorage 'patientId').</div>
          ) : appointmentError ? (
            <div className="appointments-empty" style={{color:'#b91c1c'}}>Error loading appointments: {appointmentError}</div>
          ) : appointments.length === 0 ? (
            <div className="appointments-empty">No appointments found.</div>
          ) : (
            <div className="appointment-grid">
              {appointments.map((a) => (
                <div key={`${a.doctorId}-${a.hospitalId}-${a.seatNumber}-${a.availableTime || ''}`} className="appointment-card">
                  <h3 className="appointment-doctor">{a.doctorName ? `Dr. ${a.doctorName}` : 'Doctor'}</h3>
                  <p className="appointment-hospital">{a.hospitalName}</p>
                  <div className="appointment-meta">
                    <div className="meta-item">📅 {formatLocalDate(a.availableTime)}</div>
                    <div className="meta-item">⏰ {a.timeSlot}</div>
                    <div className="meta-item">🎫 Seat: {a.seatNumber ?? '-'}</div>
                    <div className="meta-item">{friendlyDayOfWeek(a.dayOfWeek)}</div>
                  </div>
                  <div className="appointment-actions">
                    <button className="btn btn-secondary" onClick={() => navigate(`/doctor/${a.doctorId}`)}>View Details</button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </section>
    </main>
  );
}
