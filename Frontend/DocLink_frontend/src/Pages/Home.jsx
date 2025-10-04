import React, { useCallback, useEffect, useRef, useState } from 'react';
import SearchBar from '../components/SearchBar';
import DoctorCard from '../components/DoctorCard';
import { fetchSpecializations, searchDoctors } from '../api/doctor';

export default function Home() {
  const [query, setQuery] = useState('');
  const [specialty, setSpecialty] = useState('');
  const [district, setDistrict] = useState('');

  const [specializations, setSpecializations] = useState([]);
  const [loading, setLoading] = useState(false);
  const [results, setResults] = useState([]);
  const [page, setPage] = useState(0);
  const size = 12;
  const [totalPages, setTotalPages] = useState(0);

  // load specializations for dropdown
  useEffect(() => {
    fetchSpecializations()
      .then((res) => setSpecializations(res.data || []))
      .catch(() => setSpecializations([]));
  }, []);

  const doSearch = useCallback((p = 0) => {
    setLoading(true);
    searchDoctors({ q: query, specialization: specialty, district, page: p, size })
      .then((res) => {
        const data = res.data || { content: [], totalPages: 0, page: 0 };
        setResults(data.content || []);
        setTotalPages(data.totalPages || 0);
        setPage(data.page || 0);
      })
      .catch(() => {
        setResults([]);
        setTotalPages(0);
        setPage(0);
      })
      .finally(() => setLoading(false));
  }, [query, specialty, district]);

  // debounce search when filters change
  const debounceRef = useRef(null);
  useEffect(() => {
    clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => doSearch(0), 350);
    return () => clearTimeout(debounceRef.current);
  }, [query, specialty, district, doSearch]);

  // submit just triggers current search without waiting for debounce
  const handleSubmit = (e) => {
    e.preventDefault();
    clearTimeout(debounceRef.current);
    doSearch(0);
  };

  return (
    <>
      <section className="hero container">
        <h1 className="hero-title">Find &amp; Book Your Doctor</h1>
        <p className="hero-subtitle">
          Connect with qualified healthcare professionals in your area
        </p>

        <div className="action-row">
          <button className="btn btn-primary" onClick={() => {}}>
            <span aria-hidden="true">📍</span>
            <span>Find Nearby Clinics</span>
          </button>
          <button className="btn btn-outline" onClick={() => {}}>
            <span aria-hidden="true">🗓️</span>
            <span>Emergency Booking</span>
          </button>
        </div>
      </section>

      <section className="container">
        <div className="search-card">
          <SearchBar
            query={query}
            onQueryChange={setQuery}
            specialization={specialty}
            onSpecializationChange={setSpecialty}
            district={district}
            onDistrictChange={setDistrict}
            specializationOptions={specializations}
            onSubmit={handleSubmit}
          />
        </div>

        <div className="results">
          {loading && <p>Loading…</p>}

          {!loading && results.length === 0 && (
            <p>No doctors found</p>
          )}

          <div className="results-grid">
            {results.map((doc) => (
              <DoctorCard key={doc.doctorId} doctor={doc} />
            ))}
          </div>

          {!loading && totalPages > 1 && (
            <div className="pagination">
              <button disabled={page === 0} onClick={() => doSearch(page - 1)}>Prev</button>
              <span>Page {page + 1} of {totalPages}</span>
              <button disabled={page + 1 >= totalPages} onClick={() => doSearch(page + 1)}>Next</button>
            </div>
          )}
        </div>
      </section>
    </>
  );
}