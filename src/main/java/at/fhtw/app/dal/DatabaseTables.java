package at.fhtw.app.dal;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseTables {

    private static final String DropCards = "DROP TABLE IF EXISTS cards;";
    private static final String DropUsers = "DROP TABLE IF EXISTS users;";
    private static final String DropTrades = "DROP TABLE IF EXISTS trades;";

    private static final String CreateUserTableCommand =
            "CREATE TABLE IF NOT EXISTS users (" +
                    "id SERIAL PRIMARY KEY, " +
                    "username VARCHAR(100) NOT NULL UNIQUE, " +
                    "password VARCHAR(200) NOT NULL, " +
                    "token VARCHAR(50) NOT NULL UNIQUE, " +
                    "coins INT DEFAULT 20, " +
                    "elo INT DEFAULT 100, " +
                    "is_admin BOOLEAN DEFAULT false, " +
                    "wins INT DEFAULT 0, " +
                    "losses INT DEFAULT 0, " +
                    "name VARCHAR(100), " +
                    "bio VARCHAR(255), " +
                    "image VARCHAR(255)" +
                    ");";

    private static final String CreateCardTableCommand =
            "CREATE TABLE IF NOT EXISTS cards (" +
                    "id VARCHAR(255) PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "damage FLOAT NOT NULL, " +
                    "type VARCHAR(50) NOT NULL, " +
                    "element VARCHAR(50) NOT NULL, " +
                    "packageId INT NOT NULL, " +
                    "inDeck BOOLEAN DEFAULT false, " +
                    "userId INT, " +
                    "FOREIGN KEY (userId) REFERENCES users(id)" +
                    ");";

    private static final String CreatePackageTableCommand =
            "CREATE TABLE IF NOT EXISTS trades (" +
                    "Id VARCHAR(255) PRIMARY KEY, " +
                    "CardToTrade VARCHAR(255), " +
                    "Type VARCHAR(50), " +
                    "MinimumDamage INT, " +
                    "FOREIGN KEY (CardToTrade) REFERENCES cards(id)" +
                    ");";

    public static void ensureTables(Connection connection) throws SQLException {
        connection.setAutoCommit(false);
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(DropTrades);
            stmt.executeUpdate(DropCards);
            stmt.executeUpdate(DropUsers);
            //stmt.executeUpdate(CreateUserTableCommand);
            stmt.executeUpdate(CreateCardTableCommand);
            stmt.executeUpdate(CreatePackageTableCommand);
        }
        connection.commit();
    }
}
