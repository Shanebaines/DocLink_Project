import React, { useEffect, useState } from 'react';

export default function BookingDoctorSummary({ doctor, workplace, slot, loading, slotId, date }) {
  const [availability, setAvailability] = useState(null);

  useEffect(() => {
    const load = async () => {
      if (!slotId || !date) {
        setAvailability(null);
        return;
      }
      try {
        const resp = await fetch(`http://localhost:8080/schedule/${slotId}/viewSlot?date=${date}`);
        if (!resp.ok) throw new Error('Failed');
        const data = await resp.json();
        // Backend returns ViewSlotDto: availability (boolean), freeSeats, totalSeats, timePeriod, hospitalName, doctorName
        console.debug('[BookingDoctorSummary] viewSlot data', data);
        setAvailability({
          availableFlag: data.availability ?? null,
          free: data.freeSeats ?? null,
          total: data.totalSeats ?? null,
          timePeriod: data.timePeriod ?? '',
          hospitalName: data.hospitalName ?? null,
          doctorName: data.doctorName ?? null
        });
      } catch (e) {
        console.warn('Could not load slot availability', e);
        setAvailability(null);
      }
    };
    load();
  }, [slotId, date]);

  // Don't block rendering the summary when parent doctor-loading is true.
  // Show placeholders if doctor is not yet available so the card skeleton (Date/Time/Seats) is visible.

  return (
    <section className="booking-doctor-summary">
      <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
        <img src={(doctor && doctor.profilePhoto) ? doctor.profilePhoto : '/default-avatar.svg'} alt="avatar" style={{ width: 64, height: 64, borderRadius: 10, objectFit: 'cover', border: '3px solid rgba(17,153,142,0.06)' }} />
        <div style={{ flex: 1 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <h2 style={{ margin: 0 }}>{workplace?.hospitalName || availability?.hospitalName || 'Hospital'}</h2>
              <div style={{ color: '#64748b', marginTop: 6 }}>{(doctor && (doctor.doctorName || doctor.name)) ? `Dr. ${doctor.doctorName || doctor.name}` : (availability?.doctorName ? `Dr. ${availability.doctorName}` : '')}</div>
            </div>
            <div>
              <span className={`status-badge ${availability?.availableFlag ? 'available' : 'unavailable'}`}>{availability?.availableFlag ? 'Available' : 'Unavailable'}</span>
            </div>
          </div>
          <p className="booking-info" style={{ margin: '10px 0 0', color: '#94a3b8' }}>{workplace ? `${workplace.hospitalName}` : ''}</p>
        </div>
      </div>
      <div style={{ marginTop: 16 }}>
        <div style={{ display: 'grid', gridTemplateColumns: 'auto 1fr', gap: 8, alignItems: 'center' }}>
          <div style={{ color: '#64748b', paddingRight: 8 }}>Date</div>
          <div>
            <div className="date-pill">{date ? new Date(date).toLocaleDateString(undefined, { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }) : '—'}</div>
          </div>

          <div style={{ color: '#64748b', paddingRight: 8, marginTop: 8 }}>Time</div>
          <div style={{ marginTop: 8 }}>{availability?.timePeriod || (slot ? slot.timePeriod : '—')}</div>

          <div style={{ color: '#64748b', paddingRight: 8, marginTop: 8 }}>Total Seats</div>
          <div style={{ marginTop: 8 }}>{availability?.total ?? (slot?.totalSeats ?? '—')}</div>

          <div style={{ color: '#64748b', paddingRight: 8, marginTop: 8 }}>Available</div>
          <div style={{ marginTop: 8, color: '#065f46', fontWeight: 700 }}>{availability?.free ?? '—'}</div>
        </div>
      </div>
    </section>
  );
}
