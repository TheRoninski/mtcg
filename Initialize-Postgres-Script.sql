DROP TABLE if exists users;

CREATE TABLE users
(
    id       serial PRIMARY KEY,
    username varchar(25) UNIQUE NOT NULL,
    password varchar(255)        NOT NULL
);