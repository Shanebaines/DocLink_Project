import React, { useEffect, useRef, useState } from "react";
import { useLocation, useParams } from "react-router-dom";
import "./DoctorLocationMap.css";

function DoctorLocationMap() {
  const params = useParams();
  const location = useLocation();

  // Determine doctor id coming from route param (/doctor/:id) or query string (?id=)
  const routeIdFromPath = params?.id;
  const qs = new URLSearchParams(location.search);
  const routeIdFromQuery = qs.get('id');
  const routeDoctorId = routeIdFromPath || routeIdFromQuery || null;

  const mapRef = useRef(null);
  const mapInstanceRef = useRef(null);
  const doctorMarkersRef = useRef([]);
  const patientMarkerRef = useRef(null);
  const [selectedDoctor, setSelectedDoctor] = useState(null);
  const [mapLoading, setMapLoading] = useState(true);
  const [allDoctors, setAllDoctors] = useState([]);
  const [selectedDoctorFromDropdown, setSelectedDoctorFromDropdown] = useState({
    doctorId: null,
    name: ""
  });
  const [loadingDoctors, setLoadingDoctors] = useState(true);
  const [doctorDetails, setDoctorDetails] = useState(null);
  const [loadingDoctorDetails, setLoadingDoctorDetails] = useState(false);
  const [doctorWorkplaces, setDoctorWorkplaces] = useState([]);
  
  // Patient states
  const [allPatients, setAllPatients] = useState([]);
  const [selectedPatient, setSelectedPatient] = useState(null);
  const [loadingPatients, setLoadingPatients] = useState(true);
  
  // Radius filter states
  const [viewMode, setViewMode] = useState('all'); // 'all' or 'radius'
  const [radius, setRadius] = useState(5); // in kilometers
  const [maxRadius, setMaxRadius] = useState(50); // max radius in kilometers
  
  // Expanded workplaces state (to track which workplace's slots are visible)
  const [expandedWorkplaces, setExpandedWorkplaces] = useState({});

  // Helper: whether the page was opened from the landing (route provides a doctor id)
  const isFromLanding = Boolean(routeDoctorId);

  // Fetch doctors from API
  useEffect(() => {
    const fetchDoctors = async () => {
      try {
        setLoadingDoctors(true);
        const response = await fetch("http://localhost:8080/doctor/viewAll");
        if (!response.ok) {
          throw new Error("Failed to fetch doctors");
        }
        const data = await response.json();
        setAllDoctors(data);
      } catch (error) {
        console.error("Error fetching doctors:", error);
        setAllDoctors([]);
      } finally {
        setLoadingDoctors(false);
      }
    };
    fetchDoctors();
  }, []);

  // Fetch patients from API
  useEffect(() => {
    const fetchPatients = async () => {
      try {
        setLoadingPatients(true);
        const response = await fetch("http://localhost:8080/patient/viewAll");
        if (!response.ok) {
          throw new Error("Failed to fetch patients");
        }
        const data = await response.json();
        setAllPatients(data);
      } catch (error) {
        console.error("Error fetching patients:", error);
        setAllPatients([]);
      } finally {
        setLoadingPatients(false);
      }
    };
    fetchPatients();
  }, []);

  // If navigated with a doctor id (route param or query) pre-select that doctor
  useEffect(() => {
    if (!routeDoctorId) return;
    const idNum = parseInt(routeDoctorId, 10);
    if (!isFinite(idNum)) return;

    if (allDoctors && allDoctors.length > 0) {
      const found = allDoctors.find(d => Number(d.doctorId) === idNum);
      if (found) {
        setSelectedDoctorFromDropdown({ doctorId: found.doctorId, name: found.name });
      } else {
        setSelectedDoctorFromDropdown(prev => ({ ...prev, doctorId: idNum }));
      }
    } else {
      setSelectedDoctorFromDropdown(prev => ({ ...prev, doctorId: idNum }));
    }
  }, [routeDoctorId, allDoctors]);

  // If a `patientId` exists in localStorage assume the patient is logged in and fetch details
  useEffect(() => {
    const patientId = localStorage.getItem('patientId');
    if (!patientId) return;
    const fetchPatient = async () => {
      try {
        const resp = await fetch(`http://localhost:8080/patient/viewPatient?id=${patientId}`);
        if (!resp.ok) throw new Error('Failed to fetch patient');
        const data = await resp.json();
        const gpsMatch = data.gpsLocation ? data.gpsLocation.match(/\(([^,]+),([^)]+)\)/) : null;
        const patientWithCoords = {
          ...data,
          lat: gpsMatch ? parseFloat(gpsMatch[1]) : null,
          lng: gpsMatch ? parseFloat(gpsMatch[2]) : null
        };
        setSelectedPatient(patientWithCoords);
      } catch (e) {
        console.warn('Could not load logged-in patient', e);
      }
    };
    fetchPatient();
  }, []);

  // Calculate distance between two coordinates (Haversine formula)
  const calculateDistance = (lat1, lng1, lat2, lng2) => {
    const R = 6371; // Earth's radius in kilometers
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLng = (lng2 - lng1) * Math.PI / 180;
    const a = 
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
      Math.sin(dLng / 2) * Math.sin(dLng / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c; // Distance in kilometers
  };

  // Filter workplaces based on radius
  const getFilteredWorkplaces = () => {
    if (viewMode === 'all' || !selectedPatient || !selectedPatient.lat || !selectedPatient.lng) {
      return doctorWorkplaces;
    }

    return doctorWorkplaces.filter(workplace => {
      const distance = calculateDistance(
        selectedPatient.lat,
        selectedPatient.lng,
        workplace.lat,
        workplace.lng
      );
      return distance <= radius;
    }).map(workplace => ({
      ...workplace,
      distance: calculateDistance(
        selectedPatient.lat,
        selectedPatient.lng,
        workplace.lat,
        workplace.lng
      )
    })).sort((a, b) => a.distance - b.distance);
  };

  // Fetch doctor details when a doctor id changes
  useEffect(() => {
    const fetchDoctorDetails = async () => {
      if (!selectedDoctorFromDropdown.doctorId) {
        setDoctorDetails(null);
        setDoctorWorkplaces([]);
        return;
      }
      try {
        setLoadingDoctorDetails(true);
        const response = await fetch(
          `http://localhost:8080/doctor/view/${selectedDoctorFromDropdown.doctorId}`
        );
        if (!response.ok) {
          throw new Error("Failed to fetch doctor details");
        }
        const data = await response.json();
        setDoctorDetails(data);
        const workplaces = (data.workPlaces || []).map(workplace => {
          const gpsMatch = workplace.gpsLocation ? workplace.gpsLocation.match(/\(([^,]+),([^)]+)\)/) : null;
          return {
            ...workplace,
            lat: gpsMatch ? parseFloat(gpsMatch[1]) : null,
            lng: gpsMatch ? parseFloat(gpsMatch[2]) : null
          };
        }).filter(wp => wp.lat && wp.lng);
        setDoctorWorkplaces(workplaces);
        const filteredWorkplaces = viewMode === 'all' ? workplaces : workplaces.filter(wp => {
          if (!selectedPatient || !selectedPatient.lat || !selectedPatient.lng) return true;
          const distance = calculateDistance(selectedPatient.lat, selectedPatient.lng, wp.lat, wp.lng);
          return distance <= radius;
        });
        updateMapMarkers(filteredWorkplaces, selectedPatient);
      } catch (error) {
        console.error("Error fetching doctor details:", error);
        setDoctorDetails(null);
        setDoctorWorkplaces([]);
      } finally {
        setLoadingDoctorDetails(false);
      }
    };
    fetchDoctorDetails();
  }, [selectedDoctorFromDropdown.doctorId]);

  // Update map when patient is selected
  useEffect(() => {
    if (mapInstanceRef.current) {
      const filteredWorkplaces = getFilteredWorkplaces();
      updateMapMarkers(filteredWorkplaces, selectedPatient);
    }
  }, [selectedPatient]);

  // Update map markers when radius or view mode changes
  useEffect(() => {
    if (mapInstanceRef.current) {
      const filteredWorkplaces = getFilteredWorkplaces();
      updateMapMarkers(filteredWorkplaces, selectedPatient);
    }
  }, [radius, viewMode]);

  // Create custom red marker icon
  const createRedIcon = () => {
    if (!window.L) return null;
    return window.L.divIcon({
      className: 'custom-red-marker',
      html: `<div style="
        width: 32px;
        height: 32px;
        background-color: #dc2626;
        border: 3px solid white;
        border-radius: 50% 50% 50% 0;
        transform: rotate(-45deg);
        box-shadow: 0 4px 6px rgba(0,0,0,0.3);
        display: flex;
        align-items: center;
        justify-content: center;
      ">
        <div style="
          width: 12px;
          height: 12px;
          background-color: white;
          border-radius: 50%;
          transform: rotate(45deg);
        "></div>
      </div>`,
      iconSize: [32, 32],
      iconAnchor: [16, 32],
      popupAnchor: [0, -32]
    });
  };

  // Update map markers when workplaces or patient changes
  const updateMapMarkers = (workplaces, patient) => {
    if (!mapInstanceRef.current) return;

    // Clear existing doctor markers
    doctorMarkersRef.current.forEach(marker => marker.remove());
    doctorMarkersRef.current = [];

    // Clear existing patient marker
    if (patientMarkerRef.current) {
      patientMarkerRef.current.remove();
      patientMarkerRef.current = null;
    }

    const allLocations = [];

    // Add doctor workplace markers (green/blue)
    workplaces.forEach((workplace) => {
      const marker = window.L.marker([workplace.lat, workplace.lng])
        .addTo(mapInstanceRef.current);
      
      const distanceText = workplace.distance !== undefined 
        ? `<p style="margin: 4px 0; font-size: 13px; color: #059669; font-weight: 600;">📍 ${workplace.distance.toFixed(2)} km away</p>`
        : '';
      
      marker.bindPopup(`
        <div style="min-width: 250px;">
          <h3 style="margin: 0 0 8px 0; font-size: 16px; font-weight: 600; color: #059669;">${workplace.hospitalName}</h3>
          
          ${distanceText}
          <p style="margin: 4px 0; font-size: 14px;">${workplace.hospitalAddress}</p>
          <p style="margin: 4px 0; font-size: 13px; color: #6b7280;">${workplace.phoneNumber}</p>
          
        </div>
      `);
      marker.on("click", () => {
        setSelectedDoctor(workplace);
      });
      doctorMarkersRef.current.push(marker);
      allLocations.push([workplace.lat, workplace.lng]);
    });

    // Add patient marker (red)
    if (patient && patient.lat && patient.lng) {
      const redIcon = createRedIcon();
      const patientMarker = window.L.marker([patient.lat, patient.lng], {
        icon: redIcon
      }).addTo(mapInstanceRef.current);
      
      patientMarker.bindPopup(`
        <div style="min-width: 250px;">
          <h3 style="margin: 0 0 8px 0; font-size: 16px; font-weight: 600; color: #dc2626;">Patient Location</h3>
          <p style="margin: 4px 0; font-weight: 500; font-size: 15px;">${patient.firstName} ${patient.lastName}</p>
          <p style="margin: 4px 0; font-size: 14px; color: #6b7280;">
            <svg style="display: inline; vertical-align: middle; margin-right: 4px;" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
              <circle cx="12" cy="10" r="3"></circle>
            </svg>
            ${patient.address}
          </p>
        </div>
      `);
      
      patientMarkerRef.current = patientMarker;
      allLocations.push([patient.lat, patient.lng]);
    }

    // Fit map to show all markers
    if (allLocations.length === 0) {
      mapInstanceRef.current.setView([6.9271, 79.8612], 13);
    } else if (allLocations.length === 1) {
      mapInstanceRef.current.setView(allLocations[0], 15);
    } else {
      try {
        const bounds = window.L.latLngBounds(allLocations);
        mapInstanceRef.current.fitBounds(bounds, { padding: [50, 50] });
      } catch (error) {
        console.error("Error fitting bounds:", error);
        mapInstanceRef.current.setView(allLocations[0], 13);
      }
    }
  };

  // Handle doctor selection from dropdown
  const handleDoctorSelect = (e) => {
    const selectedId = parseInt(e.target.value);
    if (selectedId) {
      const doctor = allDoctors.find(d => d.doctorId === selectedId);
      if (doctor) {
        setSelectedDoctorFromDropdown({
          doctorId: doctor.doctorId,
          name: doctor.name
        });
        setSelectedDoctor(null);
      }
    } else {
      setSelectedDoctorFromDropdown({
        doctorId: null,
        name: ""
      });
      setSelectedDoctor(null);
    }
  };

  // Handle patient selection from dropdown
  const handlePatientSelect = (e) => {
    const selectedId = parseInt(e.target.value);
    if (selectedId) {
      const patient = allPatients.find(p => p.patientId === selectedId);
      if (patient) {
        const gpsMatch = patient.gpsLocation.match(/\(([^,]+),([^)]+)\)/);
        const patientWithCoords = {
          ...patient,
          lat: gpsMatch ? parseFloat(gpsMatch[1]) : null,
          lng: gpsMatch ? parseFloat(gpsMatch[2]) : null
        };
        setSelectedPatient(patientWithCoords);
      }
    } else {
      setSelectedPatient(null);
    }
  };

  // Toggle workplace slots expansion
  const toggleWorkplaceExpansion = (hospitalId) => {
    setExpandedWorkplaces(prev => ({
      ...prev,
      [hospitalId]: !prev[hospitalId]
    }));
  };

  // Initialize map
  useEffect(() => {
    const link = document.createElement("link");
    link.rel = "stylesheet";
    link.href = "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.css";
    document.head.appendChild(link);

    const script = document.createElement("script");
    script.src = "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.js";
    script.async = true;
    script.onload = () => {
      if (mapRef.current && !mapInstanceRef.current) {
        const map = window.L.map(mapRef.current).setView([6.9271, 79.8612], 13);
        window.L.tileLayer(
          "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
          {
            attribution: "© OpenStreetMap contributors",
            maxZoom: 19,
          }
        ).addTo(map);
        mapInstanceRef.current = map;
        setTimeout(() => setMapLoading(false), 500);
      }
    };
    document.body.appendChild(script);

    return () => {
      if (mapInstanceRef.current) {
        mapInstanceRef.current.remove();
        mapInstanceRef.current = null;
      }
      doctorMarkersRef.current = [];
      patientMarkerRef.current = null;
    };
  }, []);

  const handleLocationClick = (workplace) => {
    if (mapInstanceRef.current) {
      mapInstanceRef.current.setView([workplace.lat, workplace.lng], 15);
      setSelectedDoctor(workplace);
      const marker = doctorMarkersRef.current.find(
        (m, idx) => {
          const filteredWorkplaces = getFilteredWorkplaces();
          return filteredWorkplaces[idx]?.hospitalId === workplace.hospitalId;
        }
      );
      if (marker) {
        marker.openPopup();
      }
    }
  };

  return (
    <div>
      {/* Selection Section */}
      {isFromLanding ? (
        <div className="doctor-selection-section">
          <div className="selection-card">
            <h2 className="selection-title">Doctor Selected</h2>
            <p className="selection-subtitle">You selected a doctor from the landing page. Patient selection is taken from your account where possible.</p>
            {selectedDoctorFromDropdown?.name && (
              <div style={{ marginTop: 12 }}>
                <strong>Doctor:</strong> Dr. {selectedDoctorFromDropdown.name}
              </div>
            )}
            {selectedPatient && (
              <div style={{ marginTop: 8 }}>
                <strong>Patient:</strong> {selectedPatient.firstName} {selectedPatient.lastName}
              </div>
            )}
          </div>
        </div>
      ) : (
        <div className="doctor-selection-section">
          <div className="selection-card">
            <h2 className="selection-title">Select Doctor & Patient</h2>
            <p className="selection-subtitle">
              Choose a doctor and patient to view their locations on the map
            </p>

            {/* Doctor Selection */}
            <div className="form-group">
              <label className="form-label">Doctor Name</label>
              <select
                value={selectedDoctorFromDropdown.doctorId || ""}
                onChange={handleDoctorSelect}
                disabled={loadingDoctors}
                className="form-select"
              >
                <option value="">
                  {loadingDoctors ? "Loading doctors..." : "-- Select a doctor --"}
                </option>
                {allDoctors.map((doctor) => (
                  <option key={doctor.doctorId} value={doctor.doctorId}>
                    {doctor.name} - {doctor.specialization}
                  </option>
                ))}
              </select>
            </div>

            {/* Patient Selection */}
            <div className="form-group" style={{ marginTop: '20px' }}>
              <label className="form-label">Patient Name</label>
              <select
                value={selectedPatient?.patientId || ""}
                onChange={handlePatientSelect}
                disabled={loadingPatients}
                className="form-select"
              >
                <option value="">
                  {loadingPatients ? "Loading patients..." : "-- Select a patient --"}
                </option>
                {allPatients.map((patient) => (
                  <option key={patient.patientId} value={patient.patientId}>
                    {patient.firstName} {patient.lastName} - {patient.address}
                  </option>
                ))}
              </select>
            </div>

            {loadingDoctorDetails && (
              <div className="loading-message">Loading doctor details...</div>
            )}

            {/* Doctor Details Display */}
            {doctorDetails && !loadingDoctorDetails && (
              <div className="doctor-details-card">
                <div className="details-grid">
                  <div className="details-column">
                    <h3 className="doctor-details-name">{doctorDetails.doctorName}</h3>
                    <p className="doctor-details-specialty">{doctorDetails.specialization}</p>
                    <p className="doctor-details-text">{doctorDetails.qualification}</p>
                    <p className="doctor-details-text">Experience: {doctorDetails.yearOfExperience} years</p>
                  </div>
                  <div className="details-column">
                    <p className="doctor-details-contact">
                      <span style={{ marginRight: 6 }}>📞</span>{doctorDetails.phoneNumber}
                    </p>
                    <p className="doctor-details-contact">
                      📧 {doctorDetails.email}
                    </p>
                    <p className="doctor-details-contact">
                      <span style={{ marginRight: 6 }}>📍</span>{doctorDetails.address}
                    </p>
                    <p className="doctor-details-contact">
                      License: {doctorDetails.licenseNumber}
                    </p>
                  </div>
                </div>
              </div>
            )}

            {/* Patient Details Display */}
            {selectedPatient && (
              <div className="doctor-details-card" style={{ marginTop: '20px', borderLeft: '4px solid #dc2626' }}>
                <div className="details-grid">
                  <div className="details-column">
                    <h3 className="doctor-details-name" style={{ color: '#dc2626' }}>
                      {selectedPatient.firstName} {selectedPatient.lastName}
                    </h3>
                    <p className="doctor-details-text">Patient ID: {selectedPatient.patientId}</p>
                    <p className="doctor-details-text">User ID: {selectedPatient.userId}</p>
                  </div>
                  <div className="details-column">
                    <p className="doctor-details-contact">
                      <span style={{ marginRight: 6 }}>📍</span>{selectedPatient.address}
                    </p>
                    <p className="doctor-details-contact" style={{ color: '#dc2626', fontWeight: '500' }}>
                      GPS: ({selectedPatient.lat}, {selectedPatient.lng})
                    </p>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      )}

      <div className="doctor-map-container">
        {/* Sidebar */}
        <div className="sidebar">
          <div className="sidebar-header">
            <h1 className="sidebar-title">
              {selectedDoctorFromDropdown.doctorId
                ? `Dr. ${selectedDoctorFromDropdown.name}'s Locations`
                : "Doctor Locations"}
            </h1>
            <p className="sidebar-subtitle">
              {doctorWorkplaces.length > 0
                ? `${doctorWorkplaces.length} workplace${doctorWorkplaces.length !== 1 ? 's' : ''} available`
                : "Select a doctor to view locations"}
            </p>
            {selectedPatient && (
              <>
                <p className="sidebar-subtitle" style={{ color: '#dc2626', marginTop: '8px' }}>
                  📍 Patient: {selectedPatient.firstName} {selectedPatient.lastName}
                </p>
                
                {/* View Mode Toggle */}
                <div style={{ marginTop: '16px', display: 'flex', gap: '8px' }}>
                  <button
                    onClick={() => setViewMode('all')}
                    style={{
                      flex: 1,
                      padding: '10px 16px',
                      backgroundColor: viewMode === 'all' ? '#059669' : '#f3f4f6',
                      color: viewMode === 'all' ? 'white' : '#6b7280',
                      border: 'none',
                      borderRadius: '8px',
                      fontSize: '14px',
                      fontWeight: '600',
                      cursor: 'pointer',
                      transition: 'all 0.2s'
                    }}
                  >
                    View All Locations
                  </button>
                  <button
                    onClick={() => setViewMode('radius')}
                    style={{
                      flex: 1,
                      padding: '10px 16px',
                      backgroundColor: viewMode === 'radius' ? '#059669' : '#f3f4f6',
                      color: viewMode === 'radius' ? 'white' : '#6b7280',
                      border: 'none',
                      borderRadius: '8px',
                      fontSize: '14px',
                      fontWeight: '600',
                      cursor: 'pointer',
                      transition: 'all 0.2s'
                    }}
                  >
                    Find from Radius
                  </button>
                </div>

                {/* Radius Slider */}
                {viewMode === 'radius' && (
                  <div style={{
                    marginTop: '16px',
                    padding: '16px',
                    backgroundColor: '#f9fafb',
                    borderRadius: '8px',
                    border: '1px solid #e5e7eb'
                  }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                      <label style={{ fontSize: '14px', fontWeight: '600', color: '#374151' }}>
                        Search Radius
                      </label>
                      <span style={{ fontSize: '16px', fontWeight: '700', color: '#059669' }}>
                        {radius} km
                      </span>
                    </div>
                    <input
                      type="range"
                      min="0"
                      max={maxRadius}
                      step="0.5"
                      value={radius}
                      onChange={(e) => setRadius(parseFloat(e.target.value))}
                      style={{
                        width: '100%',
                        height: '6px',
                        borderRadius: '3px',
                        outline: 'none',
                        cursor: 'pointer'
                      }}
                    />
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '4px' }}>
                      <span style={{ fontSize: '12px', color: '#6b7280' }}>0 km</span>
                      <span style={{ fontSize: '12px', color: '#6b7280' }}>{maxRadius} km</span>
                    </div>
                    <p style={{ fontSize: '12px', color: '#6b7280', marginTop: '8px', marginBottom: 0 }}>
                      Showing locations within {radius} km of patient's location
                    </p>
                  </div>
                )}
              </>
            )}
          </div>
          <div className="doctor-list">
            {loadingDoctorDetails ? (
              <div className="loading-message">Loading locations...</div>
            ) : doctorWorkplaces.length === 0 ? (
              <div className="loading-message">No locations available. Please select a doctor.</div>
            ) : getFilteredWorkplaces().length === 0 ? (
              <div className="loading-message">
                No locations found within {radius} km radius. Try increasing the radius.
              </div>
            ) : (
              getFilteredWorkplaces().map((workplace) => (
                <div
                  key={workplace.hospitalId}
                  className={`doctor-card ${
                    selectedDoctor?.hospitalId === workplace.hospitalId ? "selected" : ""
                  }`}
                >
                  <div 
                    className="doctor-card-content"
                    onClick={() => handleLocationClick(workplace)}
                  >
                    <div className="doctor-info">
                      <h3 className="doctor-name">{workplace.hospitalName}</h3>
                      <p className="doctor-specialty">{workplace.timePeriod}</p>
                      <div className="doctor-details">
                        <div className="detail-item">
                          <span style={{ marginRight: 6 }}>📍</span>
                          <span>{workplace.hospitalAddress}</span>
                        </div>
                        <div className="detail-item">
                          <span style={{ marginRight: 6 }}>📞</span>
                          <span>{workplace.phoneNumber}</span>
                        </div>
                        {workplace.distance !== undefined && (
                          <div className="detail-item" style={{ color: '#059669', fontWeight: '600' }}>
                            <span style={{ marginRight: 6 }}>📍</span>
                            <span>{workplace.distance.toFixed(2)} km away</span>
                          </div>
                        )}
                      </div>
                    </div>
                    <div style={{ width: 40, height: 40 }} />
                  </div>
                  
                  {/* Available Slots Section */}
                  {workplace.availableSlots && workplace.availableSlots.length > 0 && (
                    <div style={{ marginTop: '12px', borderTop: '1px solid #e5e7eb', paddingTop: '12px' }}>
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          toggleWorkplaceExpansion(workplace.hospitalId);
                        }}
                        style={{
                          width: '100%',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'space-between',
                          padding: '8px 12px',
                          backgroundColor: '#f9fafb',
                          border: '1px solid #e5e7eb',
                          borderRadius: '6px',
                          cursor: 'pointer',
                          transition: 'all 0.2s',
                          fontSize: '14px',
                          fontWeight: '600',
                          color: '#374151'
                        }}
                      >
                        <span>Available Time Slots ({workplace.availableSlots.length})</span>
                        <span style={{ transform: expandedWorkplaces[workplace.hospitalId] ? 'rotate(180deg)' : 'rotate(0deg)', transition: 'transform 0.2s' }}>⌄</span>
                      </button>
                      
                      {/* Expanded Slots */}
                      {expandedWorkplaces[workplace.hospitalId] && (
                        <div style={{ marginTop: '12px', display: 'flex', flexDirection: 'column', gap: '10px' }}>
                          {workplace.availableSlots.map((slot) => (
                            <div
                              key={slot.slotId}
                              style={{
                                padding: '12px',
                                backgroundColor: '#f0fdf4',
                                border: '1px solid #bbf7d0',
                                borderRadius: '8px',
                                display: 'flex',
                                flexDirection: 'column',
                                gap: '8px'
                              }}
                            >
                              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                  <span style={{ color: '#059669' }}>📅</span>
                                  <span style={{ fontSize: '14px', fontWeight: '600', color: '#065f46' }}>
                                    {slot.dayOfWeek}
                                  </span>
                                </div>
                                <span style={{
                                  fontSize: '12px',
                                  fontWeight: '600',
                                  color: '#059669',
                                  backgroundColor: '#d1fae5',
                                  padding: '4px 8px',
                                  borderRadius: '4px'
                                }}>
                                  {slot.totalSeats} seats
                                </span>
                              </div>
                              
                              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                <span style={{ color: '#059669' }}>⏰</span>
                                <span style={{ fontSize: '13px', color: '#065f46' }}>
                                  {slot.timePeriod}
                                </span>
                              </div>
                              
                              <button
                                onClick={(e) => {
                                  e.stopPropagation();
                                  // TODO: Navigate to booking page
                                  console.log('Book slot:', slot.slotId);
                                }}
                                style={{
                                  marginTop: '4px',
                                  padding: '8px 16px',
                                  backgroundColor: '#059669',
                                  color: 'white',
                                  border: 'none',
                                  borderRadius: '6px',
                                  fontSize: '13px',
                                  fontWeight: '600',
                                  cursor: 'pointer',
                                  transition: 'all 0.2s'
                                }}
                              >
                                View & Book Slot
                              </button>
                            </div>
                          ))}
                        </div>
                      )}
                    </div>
                  )}
                </div>
              ))
            )}
          </div>
        </div>

        {/* Map */}
        <div className="map-container">
          {/* Doctor summary overlay shown on top of the map */}
          {doctorDetails && (
            <div className="map-doctor-overlay">
              <div className="map-doctor-left">
                <img
                  src={doctorDetails.image || '/default-avatar.svg'}
                  alt={doctorDetails.doctorName}
                  className="map-doctor-avatar"
                  onError={(e) => { e.target.onerror = null; e.target.src = '/default-avatar.svg'; }}
                />
              </div>
              <div className="map-doctor-info">
                <div className="map-doctor-name">Dr. {doctorDetails.doctorName}</div>
                <div className="map-doctor-special">{doctorDetails.specialization} • {doctorDetails.qualification}</div>
                <div className="map-doctor-meta">
                  <span>Exp: {doctorDetails.yearOfExperience ?? doctorDetails.yearOfExperience === 0 ? doctorDetails.yearOfExperience : '—'} yrs</span>
                  <span className="meta-sep">|</span>
                  <span>Fee: {doctorDetails.consultationFee ? doctorDetails.consultationFee : '—'}</span>
                </div>
              </div>
              <div className="map-doctor-actions">
                <a className="map-doctor-contact" href={`tel:${doctorDetails.phoneNumber}`}>📞</a>
                <a className="map-doctor-contact" href={`mailto:${doctorDetails.email}`}>✉️</a>
              </div>
            </div>
          )}
          {mapLoading && (
            <div className="map-loading">
              <div className="loading-spinner"></div>
              <p>Loading map...</p>
            </div>
          )}
          <div
            ref={mapRef}
            className="map"
            style={{ opacity: mapLoading ? 0 : 1 }}
          />
        </div>
      </div>
    </div>
  );
}

export default DoctorLocationMap;

