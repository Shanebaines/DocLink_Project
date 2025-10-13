import React, { useMemo, useState } from "react";

const defaultSpecs = [
  "All Specializations",
  "Cardiologist",
  "Dermatologist",
  "Pediatrician",
  "Orthopedic",
];

export default function SearchBar({
  onSearch,
  onLocationSearch,
  specializations = defaultSpecs,
}) {
  const [query, setQuery] = useState("");
  const [specialization, setSpecialization] = useState(
    specializations?.[0] || "All Specializations"
  );

  // Keep selected specialization if list changes
  useMemo(() => {
    if (!specializations.includes(specialization)) {
      setSpecialization(specializations?.[0] || "All Specializations");
    }
  }, [specializations]); // eslint-disable-line react-hooks/exhaustive-deps

  const doSearch = () => {
    if (typeof onSearch === "function") onSearch(query, specialization);
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") doSearch();
  };

  return (
    <div className="max-w-5xl mx-auto">
      <div className="rounded-2xl border border-border bg-card/80 backdrop-blur p-4 sm:p-6 shadow-md">
        <div className="grid grid-cols-1 md:grid-cols-[1fr_220px] gap-4 items-center">
          {/* Search input */}
          <div className="flex items-center gap-3 rounded-xl border border-input bg-background px-4 py-3">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-5 w-5 text-muted-foreground"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
            >
              <circle cx="11" cy="11" r="8"></circle>
              <path d="M21 21L16.65 16.65"></path>
            </svg>
            <input
              type="text"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="Search doctors or dispensaries..."
              className="w-full bg-transparent outline-none text-foreground placeholder:text-muted-foreground"
            />
          </div>

          {/* Specialization select */}
          <select
            value={specialization}
            onChange={(e) => setSpecialization(e.target.value)}
            className="h-[48px] rounded-xl border border-input bg-background px-4 text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
          >
            {specializations.map((spec) => (
              <option key={spec} value={spec}>
                {spec}
              </option>
            ))}
          </select>
        </div>

        <div className="mt-4 grid grid-cols-1 md:grid-cols-[1fr_260px] gap-4">
          <button
            onClick={doSearch}
            className="h-[48px] rounded-xl text-white font-medium bg-gradient-to-r from-primary to-secondary shadow-md hover:opacity-95 active:scale-[.99] transition"
          >
            Search
          </button>

          <button
            type="button"
            onClick={onLocationSearch}
            className="h-[48px] rounded-xl border border-input bg-background text-foreground flex items-center justify-center gap-2 hover:bg-muted transition"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-5 w-5"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
            >
              <path d="M12 2v2m0 16v2m8-10h2M2 12H0m16.243 4.243 1.414 1.414M6.343 6.343 4.929 4.929m0 14.142 1.414-1.414M17.657 6.343l1.414-1.414" />
              <circle cx="12" cy="12" r="4" />
            </svg>
            Find Nearby Doctors
          </button>
        </div>
      </div>
    </div>
  );
}