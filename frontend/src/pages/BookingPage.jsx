import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import BookingDoctorSummary from '../components/BookingDoctorSummary';
import SeatGrid from '../components/SeatGrid';
import BookingSidebar from '../components/BookingSidebar';
import './BookingPage.css';

function useQuery() {
  return new URLSearchParams(useLocation().search);
}

export default function BookingPage() {
  const query = useQuery();
  const navigate = useNavigate();
  const doctorId = query.get('doctorId');
  const hospitalId = query.get('hospitalId');
  const slotId = query.get('slotId');

  const [doctor, setDoctor] = useState(null);
  const [workplace, setWorkplace] = useState(null);
  const [slot, setSlot] = useState(null);
  const [selectedDate, setSelectedDate] = useState(() => {
    // initialize empty; we'll populate from server-provided dates list
    return '';
  });
  const [availableDates, setAvailableDates] = useState([]);
  const [selectedSeat, setSelectedSeat] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        if (!doctorId) return;
        const resp = await fetch(`http://localhost:8080/doctor/view?id=${doctorId}`);
        if (!resp.ok) throw new Error('Failed to load doctor');
        const data = await resp.json();
        setDoctor(data);
        const wp = (data.workPlaces || []).find(w => String(w.hospitalId) === String(hospitalId));
        setWorkplace(wp || null);
        if (wp && slotId) {
          const s = (wp.availableSlots || []).find(sl => String(sl.slotId) === String(slotId));
          setSlot(s || null);
        }
      } catch (e) {
        console.error(e);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [doctorId, hospitalId, slotId]);

  // fetch available dates from schedule controller when slotId changes
  useEffect(() => {
    const loadDates = async () => {
      if (!slotId) return setAvailableDates([]);
      try {
        const resp = await fetch(`http://localhost:8080/schedule/${slotId}/dates`);
        if (!resp.ok) throw new Error('Failed to load dates');
        const dates = await resp.json(); // array of ISO dates
        setAvailableDates(dates || []);
        if ((dates || []).length > 0) setSelectedDate(dates[0]);
      } catch (e) {
        console.warn('Could not load available dates', e);
        setAvailableDates([]);
      }
    };
    loadDates();
  }, [slotId]);

  if (!doctorId) {
    return (
      <div className="booking-page container">
        <div className="booking-empty">No doctor selected. Please choose a doctor to book.</div>
      </div>
    );
  }

  return (
    <main className="booking-page container">
      <div className="booking-stack">
        <div className="booking-row booking-row--doctor">
          <BookingDoctorSummary doctor={doctor} workplace={workplace} slot={slot} loading={loading} slotId={(slotId || slot?.slotId || slot?.id)} date={selectedDate} />
        </div>

        <div className="booking-row booking-row--seats">
          <section className="seat-availability-card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
              <div>
                <h3 style={{ margin: 0 }}>Seat Availability</h3>
                <small style={{ color: '#64748b' }}>Select an available seat to proceed.</small>
              </div>
              <div>
                <div className="date-selector-card">
                  <label className="date-selector-label">Select Appointment Date</label>
                  <select value={selectedDate} onChange={(e) => { setSelectedDate(e.target.value); setSelectedSeat(null); }}>
                    {availableDates.length === 0 && <option value="">No dates available</option>}
                    {availableDates.map(d => (
                      <option key={d} value={d}>{new Date(d).toLocaleDateString(undefined, { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}</option>
                    ))}
                  </select>
                </div>
              </div>
            </div>

            <div style={{ marginTop: 18 }}>
              <SeatGrid className="seat-row" slot={slot} slotId={(slotId || slot?.slotId || slot?.id)} date={selectedDate} selectedSeat={selectedSeat} onSelectSeat={setSelectedSeat} />
            </div>
          </section>
        </div>

        <div className="booking-row booking-row--confirm">
          <BookingSidebar doctor={doctor} workplace={workplace} slot={slot} selectedSeat={selectedSeat} selectedDate={selectedDate} slotId={slot?.slotId || slot?.id} />
        </div>
      </div>
    </main>
  );
}
