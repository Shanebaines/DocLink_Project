import React, { useEffect, useState } from 'react';

export default function SeatGrid({ slot, slotId, date, selectedSeat, onSelectSeat }) {
  const [seats, setSeats] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchSeats = async () => {
      if (!slotId || !date) {
        setSeats([]);
        return;
      }
      setLoading(true);
      try {
        console.debug('[SeatGrid] fetching seats', { slotId, date });
        const resp = await fetch(`http://localhost:8080/schedule/${slotId}/viewSlot?date=${date}`);
        if (!resp.ok) throw new Error('Failed to load seat info');
        const data = await resp.json();
        console.debug('[SeatGrid] viewSlot response', data);

        // Data shape from backend (ViewSlotDto): { freeSeats, totalSeats, seats: [{ seatNumber, status }] }
        const total = data.totalSeats ?? slot?.totalSeats ?? 0;

        // Build a map from provided seats for robustness
        const seatMap = (data.seats || []).reduce((acc, s) => {
          acc[s.seatNumber] = { seatNumber: s.seatNumber, booked: s.status != null };
          return acc;
        }, {});

        // Ensure we have a seat entry for every seat number 1..total
        const allSeats = Array.from({ length: total }).map((_, i) => {
          const num = i + 1;
          return seatMap[num] || { seatNumber: num, booked: false };
        });

        setSeats(allSeats);
      } catch (e) {
        console.error('Could not load seats', e);
        const total = slot?.totalSeats ?? 20;
        setSeats(Array.from({ length: total }).map((_, i) => ({ seatNumber: i + 1, booked: false })));
      } finally {
        setLoading(false);
      }
    };
    fetchSeats();
  }, [slotId, date, slot]);

  if (loading) return <div>Loading seats...</div>;

  const totalSeats = seats.length > 0 ? seats.length : (slot?.totalSeats ?? 0);

  return (
    <div>
      <div className="seat-legend" style={{ display: 'flex', gap: 16, marginBottom: 12, alignItems: 'center' }}>
        <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
          <span className="seat-legend-square seat" aria-hidden="true"></span>
          <small>Available</small>
        </div>
        <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
          <span className="seat-legend-square seat booked" aria-hidden="true"></span>
          <small>Booked</small>
        </div>
        <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
          <span className="seat-legend-square seat selected" aria-hidden="true"></span>
          <small>Selected</small>
        </div>
      </div>

      <div className="seat-grid" style={{ padding: 12, borderRadius: 8, border: '1px solid rgba(2,6,23,0.06)', background: '#fff' }}>
        {seats.map(s => (
          <button
            key={s.seatNumber}
            className={`seat ${selectedSeat === s.seatNumber ? 'selected' : ''} ${s.booked ? 'booked' : ''}`}
            onClick={() => { if (s.booked) return; onSelectSeat(s.seatNumber); }}
            disabled={s.booked}
            aria-label={`Seat ${s.seatNumber} ${s.booked ? 'booked' : 'available'}`}
            title={`Seat ${s.seatNumber}`}
          >
            <strong>{s.seatNumber}</strong>
          </button>
        ))}
      </div>

      <div style={{ marginTop: 12 }}>
        <small style={{ color: '#334155' }}>Total seats: <strong>{totalSeats}</strong></small>
      </div>
    </div>
  );
}
