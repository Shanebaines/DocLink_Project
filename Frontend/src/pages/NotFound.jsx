import React, { useEffect } from "react";
import { useLocation, Link } from "react-router-dom";

export default function NotFound() {
  const location = useLocation();

  useEffect(() => {
    console.error("404 Error: attempted to access:", location.pathname);
  }, [location.pathname]);

  return (
    <div className="flex min-h-screen items-center justify-center bg-background">
      <div className="text-center">
        <h1 className="mb-2 text-6xl font-extrabold text-foreground">404</h1>
        <p className="mb-6 text-lg text-muted-foreground">Oops! Page not found</p>
        <Link
          to="/"
          className="inline-flex items-center justify-center rounded-lg bg-primary px-5 py-3 text-white shadow-md hover:opacity-95 transition"
        >
          Return to Home
        </Link>
      </div>
    </div>
  );
}