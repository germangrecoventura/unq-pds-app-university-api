INSERT INTO commission (id, four_month_period, year, matter_id)
VALUES (1, "FIRST_PERIOD", 2023, 1),
       (2, "FIRST_PERIOD", 2023, 2);
INSERT INTO commission_teachers (commission_id, teachers_id)
VALUES (2, 4),
       (2, 5);
INSERT INTO commission_students (commission_id, students_id)
VALUES (2, 2),
       (2, 3);
INSERT INTO commission_groups_students (commission_id, groups_students_id)
VALUES (2, 1);
