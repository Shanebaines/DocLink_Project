import { Search } from "lucide-react";
import Dropdown from "./Dropdown";

export default function SearchBar() {
  return (
    <div className="search-bar">
      <div className="search-input">
        <Search size={18} className="search-icon" />
        <input
          type="text"
          placeholder="Search doctors, specialties, or hospitals..."
        />
      </div>
      <Dropdown
        defaultText="All Specialties"
        options={["Cardiology", "Dentistry", "Pediatrics", "Neurology"]}
      />
      <Dropdown
        defaultText="All Districts"
        options={["Colombo", "Kandy", "Galle", "Jaffna"]}
      />
    </div>
  );
}
