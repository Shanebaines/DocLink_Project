import React from 'react';

export default function BookingSidebar({ doctor, workplace, slot, selectedSeat, selectedDate, slotId }) {
  const [name, setName] = React.useState('');
  const [loading, setLoading] = React.useState(false);
  const patientId = localStorage.getItem('patientId');

  const handleConfirm = async () => {
    if (!selectedSeat) return alert('Please select a seat');
    if (!selectedDate) return alert('Please select a date');
    if (!slotId) return alert('Slot not selected');
    if (!patientId) return alert('Please login as patient first');

    const payload = {
      patientId: Number(patientId),
      timeSlotId: Number(slotId),
      appointmentDate: selectedDate,
      seatNumber: Number(selectedSeat)
    };

    setLoading(true);
    try {
      const resp = await fetch('http://localhost:8080/appointment/book', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      if (resp.status === 201 || resp.status === 200) {
        // try parse json, otherwise text
        let body;
        try { body = await resp.json(); } catch (_) { body = await resp.text(); }
        const msg = (body && body.message) ? body.message : (typeof body === 'string' ? body : 'Booking successful');
        alert(msg);
        // after booking, navigate back to landing or to a confirmation view
        window.location.href = '/';
      } else {
        const text = await resp.text();
        alert('Error: ' + text);
      }
    } catch (e) {
      console.error(e);
      alert('Network error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="booking-sidebar">
      <div className="booking-sidebar-card">
        <h3>{doctor ? `Dr. ${doctor.doctorName || doctor.name}` : 'Doctor'}</h3>
        <p className="muted">{workplace ? workplace.hospitalName : ''}</p>
        <div className="sidebar-meta">
          <div>Selected Seat: <strong>{selectedSeat ?? '-'}</strong></div>
          <div>Fee: <strong>{doctor?.consultationFee ?? '—'}</strong></div>
        </div>
        <div style={{ marginTop: 12 }}>
          <label>Your Name</label>
          <input className="input" value={name} onChange={(e) => setName(e.target.value)} placeholder="e.g. John Doe" />
        </div>
        <button className="btn confirm" onClick={handleConfirm} disabled={loading}>{loading ? 'Booking...' : 'Confirm Booking'}</button>
      </div>
    </div>
  );
}
