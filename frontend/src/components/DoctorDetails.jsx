import React from 'react';
import { useNavigate } from 'react-router-dom';
import './DoctorDetails.css';

export default function DoctorDetails({
  doctor,
  workplaces = [],
  expandedWorkplaces = {},
  toggleWorkplaceExpansion = () => {},
  selectedPatient = null,
  viewMode,
  radius
}) {
  const navigate = useNavigate();
  if (!doctor) return null;

  return (
    <div className="doctor-details-card">
      <div className="details-top">
        <div className="details-avatar">
          <img src={doctor.image || '/default-avatar.svg'} alt={doctor.doctorName || doctor.name || 'Doctor'} onError={(e) => { e.target.onerror = null; e.target.src = '/default-avatar.svg'; }} />
        </div>
        <div className="details-main">
          <h3 className="doctor-details-name">Dr. {doctor.doctorName || doctor.name}</h3>
          <div className="doctor-details-specialty">{doctor.specialization}</div>
          <div className="doctor-details-qual">{doctor.qualification}</div>
          <div className="doctor-details-meta">Experience: {doctor.yearOfExperience ?? '—'} yrs • Fee: {doctor.consultationFee ?? '—'}</div>
        </div>
        <div className="details-contacts">
          <a className="contact-pill" href={`tel:${doctor.phoneNumber}`}>📞 {doctor.phoneNumber || '—'}</a>
          <a className="contact-pill" href={`mailto:${doctor.email}`}>✉️ {doctor.email || '—'}</a>
          <div className="contact-pill">📍 {doctor.address || '—'}</div>
          <div className="contact-pill">License: {doctor.licenseNumber || '—'}</div>
        </div>
      </div>

      <div className="details-workplaces">
        <h4 className="workplaces-title">Workplaces{workplaces && workplaces.length ? ` (${workplaces.length})` : ''}</h4>
        {(!workplaces || workplaces.length === 0) && (
          <div className="workplace-empty">No workplaces available for this doctor.</div>
        )}

        {workplaces && workplaces.map((wp) => (
          <div key={wp.hospitalId || wp.id || wp.hospitalName} className="workplace">
            <div className="workplace-head">
              <div>
                <div className="workplace-name">{wp.hospitalName}</div>
                <div className="workplace-address">{wp.hospitalAddress}</div>
              </div>
              <div className="workplace-meta">
                {wp.distance !== undefined && <div className="workplace-distance">{wp.distance.toFixed(2)} km</div>}
                <button className="btn btn-small" onClick={(e) => { e.stopPropagation(); toggleWorkplaceExpansion(wp.hospitalId); }}>
                  {expandedWorkplaces[wp.hospitalId] ? 'Hide slots' : `Slots (${(wp.availableSlots||[]).length})`}
                </button>
              </div>
            </div>

            {expandedWorkplaces[wp.hospitalId] && wp.availableSlots && wp.availableSlots.length > 0 && (
              <div className="workplace-slots">
                {wp.availableSlots.map(slot => (
                  <div key={slot.slotId || slot.id || `${slot.dayOfWeek}-${slot.timePeriod}`} className="slot-row">
                    <div className="slot-info">
                      <div className="slot-day">{slot.dayOfWeek}</div>
                      <div className="slot-time">{slot.timePeriod}</div>
                    </div>
                    <div className="slot-right">
                      <div className="slot-seats">Seats: {slot.totalSeats ?? slot.seatCount ?? '—'}</div>
                      <button className="btn btn-small primary" onClick={(e) => { e.stopPropagation(); const q = new URLSearchParams({ doctorId: doctor.doctorId || doctor.doctorId || doctor.id, hospitalId: wp.hospitalId, slotId: slot.slotId || slot.id }); navigate(`/booking?${q.toString()}`); }}>View & Book Slot</button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
