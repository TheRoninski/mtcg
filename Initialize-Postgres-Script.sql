DROP TABLE IF EXISTS users;
CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(25) UNIQUE NOT NULL,
    password VARCHAR(255)       NOT NULL,
    coins    INTEGER            NOT NULL DEFAULT 20
);

DROP TABLE IF EXISTS packages;
CREATE TABLE packages
(
    id      SERIAL PRIMARY KEY,
    card1   VARCHAR(50) NOT NULL,
    card2   VARCHAR(50) NOT NULL,
    card3   VARCHAR(50) NOT NULL,
    card4   VARCHAR(50) NOT NULL,
    card5   VARCHAR(50) NOT NULL,
    name1   VARCHAR(50) NOT NULL,
    name2   VARCHAR(50) NOT NULL,
    name3   VARCHAR(50) NOT NULL,
    name4   VARCHAR(50) NOT NULL,
    name5   VARCHAR(50) NOT NULL,
    damage1 FLOAT       NOT NULL,
    damage2 FLOAT       NOT NULL,
    damage3 FLOAT       NOT NULL,
    damage4 FLOAT       NOT NULL,
    damage5 FLOAT       NOT NULL
);

DROP TABLE IF EXISTS user_cards;
CREATE TABLE user_cards
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(25) NOT NULL,
    card_id  VARCHAR(50) NOT NULL,
    name     VARCHAR(50) NOT NULL,
    damage   FLOAT       NOT NULL,
    FOREIGN KEY (username) REFERENCES users (username)
);

DROP TABLE IF EXISTS user_deck;
CREATE TABLE user_deck
(
    username VARCHAR(25) NOT NULL,
    card_id  VARCHAR(50) NOT NULL,
    position INTEGER     NOT NULL,
    PRIMARY KEY (username, position),
    FOREIGN KEY (username) REFERENCES users (username)
);

DROP TABLE IF EXISTS user_stats;
CREATE TABLE user_stats
(
    username    VARCHAR(25) PRIMARY KEY,
    elo         INTEGER NOT NULL DEFAULT 100,
    gamesPlayed INTEGER NOT NULL DEFAULT 0,
    wins        INTEGER NOT NULL DEFAULT 0,
    losses      INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (username) REFERENCES users (username)
);
