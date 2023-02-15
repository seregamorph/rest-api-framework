INSERT INTO team (name, description, created_date, last_modified_date)
VALUES ('First Team', 'The first team', '2020-12-01T04:05:48.757Z', '2020-12-01T04:05:48.757Z'),
       ('Second Team', 'The second team', '2020-12-01T04:05:48.757Z', '2020-12-01T04:05:48.757Z'),
       ('Third Team', 'The third team', '2020-12-01T04:05:48.757Z', '2020-12-01T04:05:48.757Z');

INSERT INTO person (name, team_id, year_of_birth, email_address, activation_date, created_date, last_modified_date)
VALUES ('John Smith', 1, 1950, 'john.smith@company.com', '2020-12-01T04:05:48.757Z', '2020-12-01T04:05:48.757Z', '2020-12-01T04:05:48.757Z'),
       ('Michael Geller', 2, 1951, 'michael.geller@company.com', '2020-12-01T04:05:48.757Z', '2020-12-01T04:05:48.757Z', '2020-12-01T04:05:48.757Z'),
       ('Robert Miller', 2, 1952, 'robert.miller@company.com', '2020-12-01T04:05:48.757Z', '2020-12-01T04:05:48.757Z', '2020-12-01T04:05:48.757Z'),
       ('Maria Garcia', 1, 1953, 'maria.garcia@company.com', '2020-12-01T04:05:48.757Z', '2020-12-01T04:05:48.757Z', '2020-12-01T04:05:48.757Z'),
       ('David Brown', 3, 1954, 'david.brown@company.com', '2020-12-01T04:05:48.757Z', '2020-12-01T04:05:48.757Z', '2020-12-01T04:05:48.757Z'),
       ('Maria Rodriguez', 3, 1955, 'maria.rodriguez@company.com', '2020-12-01T04:05:48.757Z', '2020-12-01T04:05:48.757Z', '2020-12-01T04:05:48.757Z'),
       ('Mary Green', 3, 1956, 'mary.green@company.com', '2020-12-01T04:05:48.757Z', '2020-12-01T04:05:48.757Z', '2020-12-01T04:05:48.757Z'),
       ('Maria Hernandez', 3, 1957, 'maria.hernandez@company.com', '2020-12-01T04:05:48.757Z', '2020-12-01T04:05:48.757Z', '2020-12-01T04:05:48.757Z'),
       ('Maria Martinez', 2, 1958, 'maria.martinez@company.com', '2020-12-01T04:05:48.757Z', '2020-12-01T04:05:48.757Z', '2020-12-01T04:05:48.757Z'),
       ('James Johnson', 3, 1959, 'james.johnson@company.com', '2020-12-01T04:05:48.757Z', '2020-12-01T04:05:48.757Z', '2020-12-01T04:05:48.757Z');

UPDATE team SET manager_id = 1 WHERE id = 1;
UPDATE team SET manager_id = 2 WHERE id = 2;
UPDATE team SET manager_id = 5 WHERE id = 3;

