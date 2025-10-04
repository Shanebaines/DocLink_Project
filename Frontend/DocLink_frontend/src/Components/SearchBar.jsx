import { Search } from "lucide-react";
import Dropdown from "./Dropdown";

export default function SearchBar({
  query,
  onQueryChange,
  specialization,
  onSpecializationChange,
  district,
  onDistrictChange,
  specializationOptions = [],
  districtOptions = ["Colombo", "Kandy", "Galle", "Jaffna"],
  onSubmit,
}) {
  return (
    <form className="search-bar" onSubmit={onSubmit}>
      <div className="search-input">
        <Search size={18} className="search-icon" />
        <input
          type="text"
          placeholder="Search doctors, specialties, or hospitals..."
          value={query}
          onChange={(e) => onQueryChange(e.target.value)}
        />
      </div>

      <Dropdown
        defaultText="All Specialties"
        options={specializationOptions}
        value={specialization}
        onChange={(e) => onSpecializationChange(e.target.value)}
      />

      <Dropdown
        defaultText="All Districts"
        options={districtOptions}
        value={district}
        onChange={(e) => onDistrictChange(e.target.value)}
      />
    </form>
  );
}