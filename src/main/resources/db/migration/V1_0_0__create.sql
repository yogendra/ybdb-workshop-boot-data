CREATE SCHEMA IF NOT EXISTS todo;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" schema todo;
CREATE TABLE IF NOT EXISTS todo.todo
(
    id           uuid PRIMARY KEY,
    task         VARCHAR(255),
    status       boolean
);
