CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(64) UNIQUE NOT NULL,
    password VARCHAR(128)       NOT NULL,
    role     varchar(16)
);

CREATE TABLE tasks
(
    id          BIGSERIAL PRIMARY KEY,
    description VARCHAR(256) NOT NULL,
    deadline    DATE,
    state       VARCHAR(32),
    user_id     BIGINT       REFERENCES users (id) ON DELETE SET NULL
);
