import http from './http';

export const searchDoctors = ({ q, specialization, district, page = 0, size = 12 }) =>
  http.get('/doctor/search', { params: { q, specialization, district, page, size } });

export const fetchSpecializations = () =>
  http.get('/doctor/specializations');