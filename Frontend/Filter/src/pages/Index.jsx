import React, { useMemo, useState } from "react";
import SearchBar from "../Components/SearchBar.jsx";
import DoctorCard from "../Components/DoctorCard.jsx";
import { toast } from "sonner";

const mockDoctors = [
  {
    id: 1,
    name: "Dr. Sarah Mitchell",
    image:
      "https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=400&h=400&fit=crop",
    specialization: "Cardiologist",
    experience: 15,
    rating: 4.8,
    reviews: 234,
    qualifications: ["MBBS", "MD Cardiology", "FACC"],
    dispensaries: [
      {
        name: "HealthCare Plus Clinic",
        location: "123 Medical Center Dr, Downtown",
        availableSlots: ["09:00 AM", "10:30 AM", "02:00 PM", "04:30 PM"],
      },
      {
        name: "City Heart Hospital",
        location: "456 Hospital Ave, Uptown",
        availableSlots: ["11:00 AM", "03:00 PM"],
      },
    ],
    consultationFee: 150,
  },
  {
    id: 2,
    name: "Dr. James Anderson",
    image:
      "https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=400&h=400&fit=crop",
    specialization: "Dermatologist",
    experience: 12,
    rating: 4.9,
    reviews: 189,
    qualifications: ["MBBS", "MD Dermatology", "FAAD"],
    dispensaries: [
      {
        name: "SkinCare Wellness Center",
        location: "789 Wellness Blvd, Midtown",
        availableSlots: ["08:30 AM", "11:00 AM", "01:30 PM", "05:00 PM"],
      },
    ],
    consultationFee: 120,
  },
  {
    id: 3,
    name: "Dr. Emily Rodriguez",
    image:
      "https://images.unsplash.com/photo-1594824476967-48c8b964273f?w=400&h=400&fit=crop",
    specialization: "Pediatrician",
    experience: 10,
    rating: 4.7,
    reviews: 312,
    qualifications: ["MBBS", "MD Pediatrics", "FAAP"],
    dispensaries: [
      {
        name: "Children's Health Clinic",
        location: "321 Kids Care Lane, Suburb",
        availableSlots: [
          "09:00 AM",
          "10:00 AM",
          "11:00 AM",
          "02:00 PM",
          "03:00 PM",
        ],
      },
      {
        name: "Family Medical Center",
        location: "654 Family Way, Downtown",
        availableSlots: ["01:00 PM", "04:00 PM"],
      },
    ],
    consultationFee: 100,
  },
  {
    id: 4,
    name: "Dr. Michael Chen",
    image:
      "https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=400&h=400&fit=crop",
    specialization: "Orthopedic",
    experience: 18,
    rating: 4.9,
    reviews: 267,
    qualifications: ["MBBS", "MS Orthopedics", "FAAOS"],
    dispensaries: [
      {
        name: "Joint & Spine Institute",
        location: "987 Bone Health St, Medical District",
        availableSlots: ["10:00 AM", "12:00 PM", "03:00 PM"],
      },
    ],
    consultationFee: 180,
  },
];

export default function Index() {
  const [doctors] = useState(mockDoctors);
  const [filteredDoctors, setFilteredDoctors] = useState(mockDoctors);

  const specializations = useMemo(() => {
    const unique = Array.from(new Set(doctors.map((d) => d.specialization)));
    return ["All Specializations", ...unique];
  }, [doctors]);

  const handleSearch = (query, specialization) => {
    let filtered = doctors;

    if (specialization && specialization !== "All Specializations") {
      filtered = filtered.filter(
        (doctor) => doctor.specialization === specialization
      );
    }

    if ((query || "").trim() !== "") {
      const q = query.toLowerCase();
      filtered = filtered.filter(
        (doctor) =>
          doctor.name.toLowerCase().includes(q) ||
          doctor.specialization.toLowerCase().includes(q) ||
          doctor.dispensaries.some((disp) =>
            disp.name.toLowerCase().includes(q)
          )
      );
    }

    setFilteredDoctors(filtered);
    toast.success(`Found ${filtered.length} doctors`);
  };

  return (
    <div className="min-h-screen">
      {/* Top navbar */}
      <header className="sticky top-0 z-30 bg-white/80 backdrop-blur border-b border-border">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 py-3 flex items-center gap-3">
          <div className="h-9 w-9 rounded-2xl bg-gradient-to-br from-primary to-secondary text-white grid place-items-center font-bold shadow-sm">
            D
          </div>
          <span className="text-xl font-semibold">
            <span className="text-sky-600">Doc</span>
            Link
          </span>
        </div>
      </header>

      <main className="mx-auto max-w-7xl px-4 sm:px-6 py-10">
        {/* Hero */}
        <section className="text-center mb-8 sm:mb-10">
          <h1 className="text-3xl sm:text-5xl font-extrabold tracking-tight">
            Find Your Perfect{" "}
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-primary to-accent">
              Healthcare Provider
            </span>
          </h1>
          <p className="mt-3 text-muted-foreground max-w-3xl mx-auto">
            Search from thousands of verified doctors and dispensaries. Book
            appointments with ease and get the care you deserve.
          </p>
        </section>

        {/* Search */}
        <SearchBar specializations={specializations} onSearch={handleSearch} />

        {/* Results: single-column, stacked cards */}
        <div className="mt-10">
          <h2 className="text-2xl sm:text-3xl font-bold">
            Available Doctors <span className="text-primary">({filteredDoctors.length})</span>
          </h2>

          {/* Single column list */}
          <div className="mt-6 flex flex-col gap-6 max-w-3xl mx-auto">
            {filteredDoctors.map((doctor) => (
              <DoctorCard key={doctor.id} doctor={doctor} />
            ))}
          </div>
        </div>
      </main>
    </div>
  );
}