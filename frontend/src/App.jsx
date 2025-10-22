import React from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import DoctorLocationMap from "./pages/DoctorLocationMap";
import PatientLanding from "./pages/PatientLanding";
import PatientLogin from "./pages/PatientLogin";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Landing page for patients */}
        <Route path="/" element={<PatientLanding />} />

        {/* Doctor map/details page - doctor id is provided by the landing page */}
        <Route path="/doctor/:id" element={<DoctorLocationMap />} />

        {/* Simple patient login page */}
        <Route path="/login" element={<PatientLogin />} />

      </Routes>
    </BrowserRouter>
  );
}