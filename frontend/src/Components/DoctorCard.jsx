import React from "react";
import { useNavigate } from "react-router-dom";

export default function DoctorCard({ doctor }) {
  // support different dto shapes from backend
  const name = doctor?.name ?? doctor?.doctorName ?? "Unknown Doctor";
  const rawImage = doctor?.image ?? doctor?.avatar ?? "";
  const API_BASE = import.meta.env.VITE_API_BASE ?? "http://localhost:8080";
  const image = rawImage
    ? rawImage.startsWith("http")
      ? rawImage
      : `${API_BASE}/images/${rawImage}`
    : "https://via.placeholder.com/150";
  const specialization = doctor?.specialization ?? "";
  const experience = doctor?.experience ?? doctor?.yearOfExperience ?? 0;
  const qualifications = doctor?.qualifications ?? doctor?.qualification ?? [];
  const dispensaries = doctor?.dispensaries ?? doctor?.workPlaces ?? [];
  const fee = doctor?.consultationFee ?? doctor?.fee ?? 0;

  const navigate = useNavigate();

  const onBook = () => {
    const id = doctor?.doctorId ?? doctor?.id;
    if (id) navigate(`/appointment?doctorId=${id}`);
    else navigate(`/appointment`);
  };

  return (
    <div className="w-full rounded-3xl border border-border bg-card shadow-md p-6 md:p-8">
      <div className="flex flex-col md:flex-row gap-6">
        {/* Avatar */}
        <img
          src={image}
          alt={name}
          className="w-28 h-28 md:w-32 md:h-32 rounded-2xl object-cover ring-4 ring-white shadow-sm"
        />

        {/* Content */}
        <div className="flex-1">
          <div className="flex flex-wrap items-start justify-between gap-4">
            <div>
              <h3 className="text-2xl md:text-3xl font-bold text-foreground">
                {name}
              </h3>
              <span className="mt-2 inline-flex items-center gap-2 rounded-full bg-secondary/15 px-3 py-1 text-sm font-semibold text-secondary-foreground">
                <span className="inline-block h-2 w-2 rounded-full bg-secondary"></span>
                {specialization}
              </span>
            </div>
          </div>

          {/* Stats */}
          <div className="mt-4 flex flex-wrap items-center gap-x-6 gap-y-2 text-muted-foreground">
            <div className="flex items-center gap-2">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="h-5 w-5 text-yellow-500"
                viewBox="0 0 24 24"
                fill="currentColor"
              >
                <path d="M12 .587l3.668 7.431 8.2 1.193-5.934 5.787 1.401 8.168L12 18.896l-7.335 3.87 1.401-8.168L.132 9.211l8.2-1.193z" />
              </svg>
              {/* rating/reviews may be missing from backend list dto */}
              <span className="text-foreground font-medium">{doctor.rating ?? "-"}</span>
              <span>({doctor.reviews ?? 0} reviews)</span>
            </div>

            <div className="flex items-center gap-2">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="h-5 w-5 text-emerald-600"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
              >
                <path d="M12 17l-5 2 1-5L3 9l5-.7L12 3l4 5.3L21 9l-5 5 1 5z" />
              </svg>
              <span>{experience} years experience</span>
            </div>
          </div>

          {/* Qualifications */}
          <div className="mt-3 flex flex-wrap gap-2">
            {(qualifications || []).map((q) => (
              <span
                key={q}
                className="inline-flex items-center rounded-full border border-border bg-muted px-3 py-1 text-sm"
              >
                {q}
              </span>
            ))}
          </div>

          {/* Dispensaries / Workplaces */}
          <div className="mt-6 space-y-4">
            {(dispensaries || []).map((disp, idx) => (
              <div
                key={`${doctor.doctorId ?? doctor.id}-${idx}`}
                className="rounded-2xl border border-border bg-muted/50 p-4"
              >
                <div className="flex items-start justify-between gap-4">
                  <div>
                    <div className="flex items-center gap-2 text-foreground font-semibold">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        className="h-5 w-5 text-primary"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                      >
                        <path d="M3 21V7a2 2 0 012-2h3l2-2h4l2 2h3a2 2 0 012 2v14H3z" />
                        <path d="M3 10h18" />
                      </svg>
                      {disp.name ?? disp.hospitalName}
                    </div>
                    <p className="text-sm text-muted-foreground">{disp.location ?? disp.hospitalAddress}</p>
                  </div>
                </div>

                <div className="mt-3 flex flex-wrap gap-2">
                  {(disp.availableSlots || []).map((slot) => (
                    <span
                      key={slot}
                      className="inline-flex items-center gap-1 rounded-full bg-emerald-100 text-emerald-700 px-3 py-1 text-sm font-medium"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        className="h-4 w-4"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                      >
                        <circle cx="12" cy="12" r="9" />
                        <path d="M12 7v5l3 3" />
                      </svg>
                      {slot}
                    </span>
                  ))}
                </div>
              </div>
            ))}
          </div>

          {/* Footer */}
          <div className="mt-6 flex items-center justify-between">
            <div>
              <p className="text-sm text-muted-foreground">Consultation Fee</p>
              <p className="text-2xl font-extrabold text-primary">
                ${fee}
              </p>
            </div>
            <button onClick={onBook} className="inline-flex items-center gap-2 rounded-xl bg-gradient-to-r from-primary to-secondary text-white px-5 py-3 font-semibold shadow-md hover:opacity-95 transition">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="h-5 w-5"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
              >
                <rect x="3" y="4" width="18" height="18" rx="2" />
                <path d="M16 2v4M8 2v4M3 10h18" />
              </svg>
              Book Appointment
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}