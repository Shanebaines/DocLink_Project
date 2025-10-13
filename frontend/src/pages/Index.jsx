import React, { useEffect, useMemo, useState } from "react";
import SearchBar from "../Components/SearchBar.jsx";
import DoctorCard from "../Components/DoctorCard.jsx";
import { toast } from "sonner";

// Backend base - adjust if your backend runs on a different port or provide VITE_API_BASE in .env
const API_BASE = import.meta.env.VITE_API_BASE ?? "http://localhost:8080";

export default function Index() {
  const [doctors, setDoctors] = useState([]); // raw list from backend (ViewDoctorsDto)
  const [detailedDoctors, setDetailedDoctors] = useState([]); // merged with /doctor/view details
  const [filteredDoctors, setFilteredDoctors] = useState([]);
  const [specializations, setSpecializations] = useState(["All Specializations"]);
  const [loading, setLoading] = useState(false);

  // helper: fetch list of doctors (non-paged) from backend and then fetch details for each
  const fetchDoctors = async (params = {}) => {
    setLoading(true);
    try {
      const q = params.q ? `q=${encodeURIComponent(params.q)}` : "";
      const specialization = params.specialization
        ? `&specialization=${encodeURIComponent(params.specialization)}`
        : "";
      // Use searchByHospitalList which supports q + hospital filtering; backend will handle empty params
      const url = `${API_BASE}/doctor/searchByHospitalList?${q}${specialization}`;

      const res = await fetch(url);
      if (!res.ok) throw new Error("Failed fetching doctors");
      const list = await res.json(); // expected List<ViewDoctorsDto>

      setDoctors(list || []);

      // Fetch details for each doctor (view endpoint). Limit concurrent requests for performance.
      const detailPromises = (list || []).map(async (d) => {
        try {
          const r = await fetch(`${API_BASE}/doctor/view?id=${d.doctorId}`);
          if (!r.ok) return mapListDtoToCard(d, null);
          const details = await r.json(); // ViewDoctorDto
          return mapListDtoToCard(d, details);
        } catch (e) {
          return mapListDtoToCard(d, null);
        }
      });

      const detailed = await Promise.all(detailPromises);
      setDetailedDoctors(detailed);
      setFilteredDoctors(detailed);
      toast.success(`Found ${detailed.length} doctors`);
    } catch (err) {
      console.error(err);
      toast.error("Failed to load doctors from server");
    } finally {
      setLoading(false);
    }
  };

  // map server DTOs to shapes used by DoctorCard (keep fields safe if details are missing)
  const mapListDtoToCard = (listDto, detailDto) => {
    const id = listDto?.doctorId ?? listDto?.doctorId ?? Math.random();
    const name = listDto?.name ?? detailDto?.doctorName ?? "Unknown";
    const image = listDto?.image ?? detailDto?.image ?? "https://via.placeholder.com/150";
    const specialization = listDto?.specialization ?? detailDto?.specialization ?? "";

    const qualifications = detailDto?.qualification
      ? Array.isArray(detailDto.qualification)
        ? detailDto.qualification
        : String(detailDto.qualification).split(",").map((s) => s.trim())
      : [];

    const experience = detailDto?.yearOfExperience ?? detailDto?.yearOfExperience ?? 0;

    const dispensaries = (detailDto?.workPlaces || []).map((w) => ({
      name: w.hospitalName,
      location: w.hospitalAddress,
      availableSlots: [], // backend does not expose slots in current DTO; keep empty
    }));

    return {
      id,
      doctorId: id,
      name,
      image,
      specialization,
      qualifications,
      experience,
      dispensaries,
      consultationFee: detailDto?.consultationFee ?? 0,
    };
  };

  // load specializations and initial doctors on mount
  useEffect(() => {
    const load = async () => {
      try {
        const spRes = await fetch(`${API_BASE}/doctor/specializations`);
        if (spRes.ok) {
          const s = await spRes.json();
          setSpecializations(["All Specializations", ...(s || [])]);
        }
      } catch (e) {
        console.warn("Could not load specializations", e);
      }

      // initial load - empty params to get all doctors
      fetchDoctors({});
    };

    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleSearch = (query, specialization) => {
    const spec = specialization && specialization !== "All Specializations" ? specialization : undefined;
    fetchDoctors({ q: query, specialization: spec });
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
            Search from verified doctors and hospitals. Book appointments with ease.
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
            {loading ? (
              <div className="p-6 text-center text-muted-foreground">Loading doctors...</div>
            ) : (
              filteredDoctors.map((doctor) => (
                <DoctorCard key={doctor.doctorId ?? doctor.id} doctor={doctor} />
              ))
            )}
          </div>
        </div>
      </main>
    </div>
  );
}