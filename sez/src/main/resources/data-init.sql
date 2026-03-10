INSERT INTO subjects (id, name, created_at) VALUES
                                                (gen_random_uuid(), 'Mathematics', NOW()),
                                                (gen_random_uuid(), 'Physics', NOW()),
                                                (gen_random_uuid(), 'English Language', NOW()),
                                                (gen_random_uuid(), 'Java Programming', NOW()),
                                                (gen_random_uuid(), 'History', NOW()),
                                                (gen_random_uuid(), 'Biology', NOW()),
                                                (gen_random_uuid(), 'Chemistry', NOW()),
                                                (gen_random_uuid(), 'Geography', NOW()),
                                                (gen_random_uuid(), 'Literature', NOW()),
                                                (gen_random_uuid(), 'Art', NOW()),
                                                (gen_random_uuid(), 'Economics', NOW()),
                                                (gen_random_uuid(), 'Psychology', NOW()),
                                                (gen_random_uuid(), 'Philosophy', NOW()),
                                                (gen_random_uuid(), 'Marketing', NOW()),
                                                (gen_random_uuid(), 'Management', NOW()),
                                                (gen_random_uuid(), 'Law', NOW()),
                                                (gen_random_uuid(), 'Design', NOW()),
                                                (gen_random_uuid(), 'German Language', NOW()),
                                                (gen_random_uuid(), 'French Language', NOW()),
                                                (gen_random_uuid(), 'Spanish Language', NOW()),
                                                (gen_random_uuid(), 'Databases', NOW()),
                                                (gen_random_uuid(), 'Algorithms', NOW()),
                                                (gen_random_uuid(), 'Networking', NOW()),
                                                (gen_random_uuid(), 'Cybersecurity', NOW()),
                                                (gen_random_uuid(), 'Soft Skills', NOW())
    ON CONFLICT (name) DO NOTHING;

INSERT INTO courses (id, tutor_id, subject_id, hourly_rate, description, created_at)
SELECT
    gen_random_uuid(),
    (SELECT id FROM users WHERE email = 'andre.chrn@gmail.com'),
    s.id,
    (15 + (random() * 50))::numeric(10,2),
    'Comprehensive course on ' || s.name,
    NOW()
FROM subjects s
WHERE NOT EXISTS (
    SELECT 1 FROM courses c
    WHERE c.tutor_id = (SELECT id FROM users WHERE email = 'andre.chrn@gmail.com')
      AND c.subject_id = s.id
);

INSERT INTO enrollments (id, course_id, student_id, status, created_at)
SELECT
    gen_random_uuid(),
    c.id,
    (SELECT id FROM users WHERE email = 'andre.chrnko@gmail.com'),
    'ACTIVE',
    NOW()
FROM courses c
WHERE c.tutor_id = (SELECT id FROM users WHERE email = 'andre.chrn@gmail.com')
    ON CONFLICT DO NOTHING;

INSERT INTO enrollments (id, course_id, student_id, status, created_at)
SELECT gen_random_uuid(), c.id, (SELECT id FROM users WHERE email = 'polina.chrnko@gmail.com'), 'ACTIVE', NOW()
FROM courses c JOIN subjects s ON c.subject_id = s.id
WHERE s.name IN ('English Language', 'German Language')
  AND c.tutor_id = (SELECT id FROM users WHERE email = 'andre.chrn@gmail.com');

INSERT INTO enrollments (id, course_id, student_id, status, created_at)
SELECT gen_random_uuid(), c.id, (SELECT id FROM users WHERE email = 'student.dmytro@gmail.com'), 'ACTIVE', NOW()
FROM courses c JOIN subjects s ON c.subject_id = s.id
WHERE s.name IN ('Java Programming', 'Databases', 'Algorithms', 'Cybersecurity')
  AND c.tutor_id = (SELECT id FROM users WHERE email = 'andre.chrn@gmail.com');

INSERT INTO enrollments (id, course_id, student_id, status, created_at)
SELECT gen_random_uuid(), c.id, (SELECT id FROM users WHERE email = 'student.ivan@gmail.com'), 'ACTIVE', NOW()
FROM courses c JOIN subjects s ON c.subject_id = s.id
WHERE s.name IN ('Psychology', 'Economics')
  AND c.tutor_id = (SELECT id FROM users WHERE email = 'andre.chrn@gmail.com');

INSERT INTO lessons (id, enrollment_id, start_time, end_time, status, price, created_at)
SELECT
    gen_random_uuid(),
    e.id,
    CASE
        WHEN n = 1 THEN NOW() - (interval '1 day' * row_number() over (partition by e.student_id, n))
        ELSE NOW() + (interval '1 day' * row_number() over (partition by e.student_id, n))
        END,
    CASE
        WHEN n = 1 THEN NOW() - (interval '1 day' * row_number() over (partition by e.student_id, n)) + interval '1 hour'
    ELSE NOW() + (interval '1 day' * row_number() over (partition by e.student_id, n)) + interval '1 hour'
END,
    CASE WHEN n = 1 THEN 'CONDUCTED' ELSE 'PLANNED' END,
    (SELECT hourly_rate FROM courses WHERE id = e.course_id),
    NOW()
FROM enrollments e
CROSS JOIN (SELECT 1 as n UNION SELECT 2) as multiplier
WHERE NOT EXISTS (SELECT 1 FROM lessons l WHERE l.enrollment_id = e.id);

INSERT INTO assignments (id, lesson_id, title, description, status, created_at)
SELECT
    gen_random_uuid(),
    l.id,
    'Homework for ' || s.name || ' #' || (row_number() over (partition by e.student_id)),
    'Please complete the exercises discussed during the lesson.',
    'PENDING',
    NOW()
FROM lessons l
         JOIN enrollments e ON l.enrollment_id = e.id
         JOIN courses c ON e.course_id = c.id
         JOIN subjects s ON c.subject_id = s.id
WHERE NOT EXISTS (SELECT 1 FROM assignments a WHERE a.lesson_id = l.id);

INSERT INTO reviews (id, tutor_id, student_id, rating, comment, created_at)
SELECT
    gen_random_uuid(),
    (SELECT id FROM users WHERE email = 'andre.chrn@gmail.com'),
    u.id,
    (4 + (random() * 1))::int,
    'Great experience with the tutor on various subjects!',
    NOW()
FROM users u
WHERE u.email IN ('andre.chrnko@gmail.com', 'student.olena@gmail.com', 'student.dmytro@gmail.com')
  AND NOT EXISTS (
    SELECT 1 FROM reviews r
    WHERE r.tutor_id = (SELECT id FROM users WHERE email = 'andre.chrn@gmail.com')
      AND r.student_id = u.id
);