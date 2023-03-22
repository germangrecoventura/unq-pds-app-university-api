START TRANSACTION;


-- DATA ELIMINATION
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE app_university.user;
SET FOREIGN_KEY_CHECKS = 1;

-- USER INSERT
INSERT INTO app_university.user (id, first_name)
VALUES (1, 'Pedro'),
       (2, 'Pablo'),
       (3, 'Raúl'),
       (4, 'Roque'),
       (5, 'Luis'),
       (6, 'Ricardo'),
       (7, 'Stan');

COMMIT;

SELECT *
FROM user;