import React, { useState } from 'react';

export default function Home() {
  const [query, setQuery] = useState('');
  const [specialty, setSpecialty] = useState('');
  const [district, setDistrict] = useState('');

  const handleNearby = () => {
    // TODO: implement geolocation search
    console.log('Find Nearby Clinics clicked');
  };

  const handleEmergency = () => {
    // TODO: navigate to emergency booking
    console.log('Emergency Booking clicked');
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log({ query, specialty, district });
    // TODO: run your search
  };

  return (
    <>
      <section className="hero container">
        <h1 className="hero-title">Find &amp; Book Your Doctor</h1>
        <p className="hero-subtitle">
          Connect with qualified healthcare professionals in your area
        </p>

        <div className="action-row">
          <button className="btn btn-primary" onClick={handleNearby}>
            <span className="btn-icon" aria-hidden="true">📍</span>
            <span>Find Nearby Clinics</span>
          </button>

          <button className="btn btn-outline" onClick={handleEmergency}>
            <span className="btn-icon" aria-hidden="true">🗓️</span>
            <span>Emergency Booking</span>
          </button>
        </div>
      </section>

      <section className="container">
        <div className="search-card">
          <form className="search-grid" onSubmit={handleSubmit}>
            <div className="search-input-wrap">
              <span className="icon" aria-hidden="true">🔍</span>
              <input
                className="input"
                type="search"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                placeholder="Search doctors, specialties, or hospitals..."
                aria-label="Search"
              />
            </div>

            <select
              className="select"
              value={specialty}
              onChange={(e) => setSpecialty(e.target.value)}
              aria-label="Specialty"
            >
              <option value="" disabled>All Specialties</option>
              <option value="cardiology">Cardiology</option>
              <option value="dermatology">Dermatology</option>
              <option value="orthopedics">Orthopedics</option>
              <option value="pediatrics">Pediatrics</option>
            </select>

            <select
              className="select"
              value={district}
              onChange={(e) => setDistrict(e.target.value)}
              aria-label="District"
            >
              <option value="" disabled>All Districts</option>
              <option value="colombo">Colombo</option>
              <option value="kandy">Kandy</option>
              <option value="galle">Galle</option>
              <option value="matara">Matara</option>
            </select>
          </form>
        </div>
      </section>
    </>
  );
}