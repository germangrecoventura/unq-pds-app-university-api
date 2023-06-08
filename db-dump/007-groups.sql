INSERT INTO group_app (id, name)
VALUES (1, "Grupo 2");
INSERT INTO group_app_members (group_id, members_id)
VALUES (1, 2),
       (1, 3);
INSERT INTO group_app_projects (group_id, projects_id)
VALUES (1, 1);