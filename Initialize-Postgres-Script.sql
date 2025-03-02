DROP TABLE IF EXISTS trades;
DROP TABLE IF EXISTS cards;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(100) UNIQUE NOT NULL,
                       password VARCHAR(200) NOT NULL,
                       token VARCHAR(50) UNIQUE,
                       coins INT DEFAULT 20,
                       elo INT DEFAULT 100,
                       is_admin BOOLEAN DEFAULT false,
                       wins INT DEFAULT 0,
                       losses INT DEFAULT 0,
                       name VARCHAR(100),
                       bio VARCHAR(255),
                       image VARCHAR(255)
);

CREATE TABLE cards (
                       id VARCHAR(255) PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       damage FLOAT NOT NULL,
                       type VARCHAR(50) NOT NULL,
                       element VARCHAR(50) NOT NULL,
                       packageId INT NOT NULL,
                       inDeck BOOLEAN DEFAULT false,
                       userId INT,
                       FOREIGN KEY (userId) REFERENCES users(id)
);

CREATE TABLE trades (
                        Id VARCHAR(255) PRIMARY KEY,
                        CardToTrade VARCHAR(255),
                        Type VARCHAR(50),
                        MinimumDamage INT,
                        FOREIGN KEY (CardToTrade) REFERENCES cards(id)
);
