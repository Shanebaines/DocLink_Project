import { MapPin, Calendar } from "lucide-react";

export default function ActionButtons() {
  return (
    <div className="action-buttons">
      <button className="primary-btn">
        <MapPin size={18} /> Find Nearby Clinics
      </button>
      <button className="secondary-btn">
        <Calendar size={18} /> Emergency Booking
      </button>
    </div>
  );
}
