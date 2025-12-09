-- Give \i path-of-your-file-structure-to setupData.sql

select * from patients;
select * from doctor_time_slots;
select * from appointments;
select * from doctors;
select * from users;
select * from hospitals;
select * from hospital_doctors;
select * from doctor_availability;
select * from medications;
select * from medical_records;
select * from medical_reports;
select * from prescriptions;
select * from prescription_medications;
select * from prescription_tokens;

INSERT INTO users
(username, password, first_name, last_name, phone_number, email, address, gps_location,
 registration_date, user_type, gender_type, created_at, updated_at)
VALUES
-- Patients
('john_doe',   'hashed_password_123', 'John', 'Doe', '1234567890', 'john.doe@example.com',
 '123 Elm Street, Springfield', '(40.7128, -74.0060)',
 '2024-01-15', 'patient', 'male', '2024-01-15 10:00:00', '2024-01-15 10:00:00'),

('sam_rogers', 'hashed_password_789', 'Sam', 'Rogers', '5559991111', 'sam.rogers@example.com',
 '789 Pine Road, Gotham', '(37.7749, -122.4194)',
 '2024-04-05', 'patient', 'other', '2024-04-05 09:15:00', '2024-04-05 09:15:00'),

-- Doctors
('alice_smith', 'hashed_password_456', 'Alice', 'Smith', '0987654321', 'alice.smith@example.com',
 '456 Oak Avenue, Metropolis', '(34.0522, -118.2437)',
 '2023-11-20', 'doctor', 'female', '2023-11-20 08:30:00', '2024-02-10 17:45:00'),

('robert_jones', 'hashed_password_321', 'Robert', 'Jones', '2228883333', 'robert.jones@example.com',
 '22 Maple Street, Star City', '(36.1627, -86.7816)',
 '2023-08-12', 'doctor', 'male', '2023-08-12 09:00:00', '2023-09-01 10:00:00'),

('mei_chen', 'hashed_password_654', 'Mei', 'Chen', '7775552222', 'mei.chen@example.com',
 '88 Riverwalk Blvd, Central City', '(40.4406, -79.9959)',
 '2023-05-10', 'doctor', 'female', '2023-05-10 14:00:00', '2023-05-10 14:00:00');

INSERT INTO doctors
(image, user_id, specialization, license_number, years_experience, qualification, availability_schedule)
VALUES
    ('https://example.com/images/dr_smith.jpg', 3, 'Cardiology', 'LIC-12345-A', 12,
     'MD, Fellow of the American College of Cardiology',
     '{
       "monday": "09:00-16:00",
       "tuesday": "09:00-16:00",
       "wednesday": "09:00-14:00",
       "thursday": "Closed",
       "friday": "10:00-15:00"
     }'),

    ('https://example.com/images/dr_jones.jpg', 4, 'Pediatrics', 'LIC-67890-B', 8,
     'MD, Pediatric Specialist',
     '{
       "monday": "08:00-15:00",
       "tuesday": "08:00-15:00",
       "wednesday": "08:00-15:00",
       "thursday": "10:00-17:00",
       "friday": "Closed"
     }'),

    ('https://example.com/images/dr_chen.jpg', 5, 'Dermatology', 'LIC-54321-C', 15,
     'MBBS, Diploma in Dermatology',
     '{
       "monday": "11:00-18:00",
       "tuesday": "11:00-18:00",
       "wednesday": "Closed",
       "thursday": "11:00-18:00",
       "friday": "11:00-18:00"
     }');

INSERT INTO patients
(user_id, date_of_birth, emergency_contact, insurance_number)
VALUES
    (1, '1988-03-22', 'Jane Doe – 555-888-9999', 'INS-001122-A'),
    (2, '1995-07-18', 'Alex Rogers – 555-777-4444', 'INS-009988-B');

INSERT INTO hospitals
(hospital_name, address, gps_location, phone_number, email, hospital_type,
 bed_capacity, accreditation, emergency_services, has_pharmacy, created_at, updated_at)
VALUES
    ('Springfield General Hospital',
     '100 Main Street, Springfield',
     '(40.7128, -74.0060)',
     '555-111-2222',
     'info@springfieldgeneral.org',
     'general',
     250,
     'Joint Commission Accredited',
     TRUE,
     TRUE,
     '2023-01-15 09:00:00',
     '2024-05-10 10:30:00'),

    ('Metropolis Heart Center',
     '200 Heart Avenue, Metropolis',
     '(34.0522, -118.2437)',
     '555-333-4444',
     'contact@metropolisheart.org',
     'specialty',
     120,
     'Cardiology Excellence Board',
     TRUE,
     TRUE,
     '2022-11-01 08:45:00',
     '2024-03-20 12:15:00'),

    ('Star City Medical University Hospital',
     '1 Academic Plaza, Star City',
     '(36.1627, -86.7816)',
     '555-555-7777',
     'admin@starcityunivmed.edu',
     'teaching',
     600,
     'National Teaching Hospital Council',
     TRUE,
     TRUE,
     '2021-09-10 07:30:00',
     '2024-01-05 09:50:00'),

    ('Central Valley Psychiatric Institute',
     '88 Calm Street, Central Valley',
     '(40.4406, -79.9959)',
     '555-999-0000',
     'support@cvpi.org',
     'psychiatric',
     180,
     'Mental Health Accreditation Board',
     TRUE,
     FALSE,
     '2023-06-22 10:10:00',
     '2024-04-18 11:20:00'),

    ('Gotham Rehabilitation & Wellness Center',
     '400 Renewal Road, Gotham',
     '(37.7749, -122.4194)',
     '555-222-6666',
     'care@gothamrehab.com',
     'rehabilitation',
     90,
     'Physical Therapy Alliance',
     FALSE,
     TRUE,
     '2022-02-14 14:30:00',
     '2024-02-14 14:30:00');

INSERT INTO hospital_doctors
(hospital_id, doctor_id, start_date, end_date, consultation_fee)
VALUES
-- Dr Alice Smith works at two hospitals
(1, 1, '2015-03-01', NULL,1500),
(2, 1, '2018-06-01', NULL,2000),

-- Dr Robert Jones is teaching pediatrics at the university hospital
(3, 2, '2017-09-12', NULL,1500),

-- Dr Mei Chen consults for both a general and a rehabilitation hospital
(1, 3, '2012-11-05', '2020-12-31',4500),
(5, 3, '2021-01-15', NULL,1300);

INSERT INTO doctor_time_slots
(doctor_id, hospital_id, day_of_week, start_time, end_time, total_seats, version, created_at, updated_at)
VALUES
-- Dr Alice Smith at Springfield General Hospital
(1, 1, 'MONDAY',    '09:00', '12:00', 10, 0, NOW(), NOW()),
(1, 1, 'WEDNESDAY', '13:00', '16:00', 8,  0, NOW(), NOW()),
(1, 1, 'FRIDAY',    '09:00', '11:00', 6,  0, NOW(), NOW()),

-- Dr Alice Smith also at Metropolis Heart Center
(1, 2, 'TUESDAY',   '10:00', '14:00', 5,  0, NOW(), NOW()),
(1, 2, 'THURSDAY',  '10:00', '13:00', 5,  0, NOW(), NOW()),

-- Dr Robert Jones at Star City Medical University Hospital
(2, 3, 'MONDAY',    '08:00', '12:00', 12, 0, NOW(), NOW()),
(2, 3, 'WEDNESDAY', '08:00', '12:00', 12, 0, NOW(), NOW()),
(2, 3, 'FRIDAY',    '08:00', '11:00', 10, 0, NOW(), NOW()),

-- Dr Mei Chen at Gotham Rehabilitation & Wellness Center
(3, 5, 'TUESDAY',   '11:00', '15:00', 7,  0, NOW(), NOW()),
(3, 5, 'THURSDAY',  '11:00', '15:00', 7,  0, NOW(), NOW()),
(3, 5, 'SATURDAY',  '09:00', '12:00', 6,  0, NOW(), NOW());

INSERT INTO doctor_availability
(doctor_id, hospital_id, slot_id, availability, effective_from, effective_until, version, created_at, updated_at)
VALUES
-- Dr Alice Smith, Springfield General Hospital
(1, 1, 1, TRUE,  '2024-01-01 00:00:00', NULL, 0, NOW(), NOW()),
(1, 1, 2, TRUE,  '2024-01-01 00:00:00', NULL, 0, NOW(), NOW()),
(1, 1, 3, FALSE, '2024-06-01 00:00:00', '2024-06-30 00:00:00', 0, NOW(), NOW()),  -- on leave in June

-- Dr Alice Smith, Metropolis Heart Center
(1, 2, 4, TRUE,  '2024-01-01 00:00:00', NULL, 0, NOW(), NOW()),
(1, 2, 5, TRUE,  '2024-01-01 00:00:00', NULL, 0, NOW(), NOW()),

-- Dr Robert Jones, Star City University Hospital
(2, 3, 6, TRUE,  '2024-01-01 00:00:00', NULL, 0, NOW(), NOW()),
(2, 3, 7, FALSE, '2024-02-15 00:00:00', '2024-03-15 00:00:00', 0, NOW(), NOW()),  -- unavailable one month
(2, 3, 8, TRUE,  '2024-01-01 00:00:00', NULL, 0, NOW(), NOW()),

-- Dr Mei Chen, Gotham Rehabilitation & Wellness Center
(3, 5, 9, TRUE,  '2024-01-01 00:00:00', NULL, 0, NOW(), NOW()),
(3, 5, 10, TRUE, '2024-01-01 00:00:00', NULL, 0, NOW(), NOW()),
(3, 5, 11, FALSE,'2024-08-01 00:00:00', '2024-08-15 00:00:00', 0, NOW(), NOW());

INSERT INTO appointments
(patient_id, doctor_id, hospital_id, appointment_date, time_slots_id, seat_number,
 status, reason, notes, consultation_fee, created_at, updated_at)
VALUES
-- Dr Alice Smith at Springfield General (slot 1 – Monday 09‑12)
(1, 1, 1, '2024-06-03', 1, 1,
 'scheduled', 'Annual heart checkup', 'Patient requested early morning slot.', 150.00,
 '2024-05-25 09:00:00', '2024-05-25 09:00:00'),

-- Second patient, same session different seat
(2, 1, 1, '2024-06-03', 1, 2,
 'scheduled', 'Follow‑up on blood pressure readings', NULL, 150.00,
 '2024-05-25 09:05:00', '2024-05-25 09:05:00'),

-- Dr Robert Jones – Star City University Hospital (slot 6 – Monday 08‑12)
(1, 2, 3, '2024-06-10', 6, 1,
 'completed', 'Pediatric consultation for John’s son.', 'Case closed, healthy.', 100.00,
 '2024-06-10 08:00:00', '2024-06-10 10:30:00'),

-- Dr Mei Chen – Gotham Rehab (slot 9 – Tuesday 11‑15)
(2, 3, 5, '2024-06-11', 9, 1,
 'scheduled', 'Physical therapy session post‑injury', NULL, 180.00,
 '2024-06-05 14:00:00', '2024-06-05 14:00:00'),

-- Dr Alice Smith – Metropolis Heart Center (slot 4 – Tuesday 10‑14)
(1, 1, 2, '2024-06-18', 4, 1,
 'cancelled', 'Patient rescheduled due to travel', 'Cancelled 24 h before.', 150.00,
 '2024-06-01 12:00:00', '2024-06-17 10:00:00'),

-- Dr Mei Chen – Gotham Rehab (slot 10 – Thursday 11‑15)
(1, 3, 5, '2024-06-20', 10, 2,
 'rescheduled', 'Extended rehab session', 'Doctor moved from Tuesday → Thursday.', 180.00,
 '2024-06-15 09:30:00', '2024-06-19 16:00:00');


 -- Medications
 INSERT INTO medications
 (medication_name, generic_name, price, description, manufacturer, created_at, updated_at)
 VALUES
 (
     'Paracetamol 500mg',
     'Acetaminophen',
     150.00,
     'Pain reliever and fever reducer. Used for headaches, muscle aches, arthritis, backache, toothaches, colds, and fevers.',
     'GlaxoSmithKline',
     NOW(),
     NOW()
 ),
 (
     'Amoxicillin 250mg',
     'Amoxicillin',
     350.00,
     'Antibiotic used to treat bacterial infections including chest infections, dental abscesses, and ear infections.',
     'Pfizer',
     NOW(),
     NOW()
 ),
 (
     'Omeprazole 20mg',
     'Omeprazole',
     280.00,
     'Proton pump inhibitor used to treat gastroesophageal reflux disease (GERD), stomach ulcers, and heartburn.',
     'AstraZeneca',
     NOW(),
     NOW()
 ),
 (
     'Metformin 500mg',
     'Metformin Hydrochloride',
     120.00,
     'Oral diabetes medication used to control blood sugar levels in type 2 diabetes patients.',
     'Bristol-Myers Squibb',
     NOW(),
     NOW()
 ),
 (
     'Cetirizine 10mg',
     'Cetirizine Hydrochloride',
     180.00,
     'Antihistamine used to relieve allergy symptoms such as watery eyes, runny nose, itching, and sneezing.',
     'Johnson & Johnson',
     NOW(),
     NOW()
 );