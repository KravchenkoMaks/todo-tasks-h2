INSERT INTO users(username, password, role)
VALUES ('admin@gmail.com', '{bcrypt}$2a$10$0qcGtERuEqK60aMk/Tl2cuKmV1CSy8.MRuZ0fXxGoV3LfRgo25IxG', 'a'),
       ('user-1@gmail.com', '{bcrypt}$2a$10$0qcGtERuEqK60aMk/Tl2cuKmV1CSy8.MRuZ0fXxGoV3LfRgo25IxG', 'u'),
       ('user-2@gmail.com', '{bcrypt}$2a$10$0qcGtERuEqK60aMk/Tl2cuKmV1CSy8.MRuZ0fXxGoV3LfRgo25IxG', 'u'),
       ('user-3@gmail.com', '{bcrypt}$2a$10$0qcGtERuEqK60aMk/Tl2cuKmV1CSy8.MRuZ0fXxGoV3LfRgo25IxG', 'u'),
       ('user-4@gmail.com', '{bcrypt}$2a$10$0qcGtERuEqK60aMk/Tl2cuKmV1CSy8.MRuZ0fXxGoV3LfRgo25IxG', 'u'),
       ('user-5@gmail.com', '{bcrypt}$2a$10$0qcGtERuEqK60aMk/Tl2cuKmV1CSy8.MRuZ0fXxGoV3LfRgo25IxG', 'u');

INSERT INTO tasks(description, deadline, state, user_id)
VALUES ('task1', '2024-12-12', 'pl', 3),
       ('task2', '2024-11-06', 'pl', 2),
       ('task3', '2024-10-15', 'pl', 3),
       ('task4', '2024-12-23', 'pl', 5),
       ('task5', '2024-10-09', 'pl', 4),
       ('task6', '2024-10-01', 'pl', 2);

