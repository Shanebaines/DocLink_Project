import React from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import DoctorLocationMap from "./pages/DoctorLocationMap";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        // Add your page routes here...
        
        <Route path="/" element={<DoctorLocationMap />} />


      </Routes>
    </BrowserRouter>
  );
}