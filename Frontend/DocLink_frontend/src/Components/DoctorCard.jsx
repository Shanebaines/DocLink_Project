export default function DoctorCard({ doctor }) {
  return (
    <div className="doctor-card">
      <img
        src={doctor.image || 'https://via.placeholder.com/120x120?text=Dr'}
        alt={doctor.name}
        className="doctor-card__img"
      />
      <div className="doctor-card__body">
        <h4 className="doctor-card__title">{doctor.name}</h4>
        <p className="doctor-card__spec">{doctor.specialization || '—'}</p>
      </div>
    </div>
  );
}