package at.fhtw.app.dal;

import at.fhtw.app.model.Card;
import at.fhtw.app.model.CardType;
import at.fhtw.app.model.Element;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardsManager implements ICardManager {
    private final String connectionString;

    public CardsManager(String connectionString) {
        this.connectionString = connectionString;
        try (Connection connection = DriverManager.getConnection(connectionString)) {
            //DatabaseTables.ensureTables(connection);
        } catch (SQLException e) {
            throw new RuntimeException("Error ensuring tables", e);
        }
    }

    @Override
    public boolean insertCards(List<Card> cards) {
        try (Connection connection = DriverManager.getConnection(connectionString)) {
            connection.setAutoCommit(false);
            for (Card card : cards) {
                try (PreparedStatement checkStmt = connection.prepareStatement("SELECT * FROM cards WHERE id = ?")) {
                    checkStmt.setString(1, card.id());
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next()) {
                            connection.rollback();
                            return false;
                        }
                    }
                }
            }
            int newPackageId = getHighestPackageId(connection);
            for (Card card : cards) {
                try (PreparedStatement insertStmt = connection.prepareStatement(
                        "INSERT INTO cards (id, name, damage, type, element, packageId) VALUES (?, ?, ?, ?, ?, ?)")) {
                    insertStmt.setString(1, card.id());
                    insertStmt.setString(2, card.name());
                    insertStmt.setFloat(3, card.damage());
                    insertStmt.setString(4, card.type().toString());
                    insertStmt.setString(5, card.element().toString());
                    insertStmt.setInt(6, newPackageId);
                    insertStmt.executeUpdate();
                }
            }
            connection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getHighestPackageId(Connection connection) throws SQLException {
        int newPackageId = 0;
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT MAX(packageId) AS highestPackageId FROM cards WHERE packageId IS NOT NULL");
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next() && !rs.wasNull()) {
                newPackageId = rs.getInt("highestPackageId") + 1;
            }
        }
        return newPackageId;
    }

    @Override
    public List<Card> getFreePackage(Integer userId) {
        try (Connection connection = DriverManager.getConnection(connectionString)) {
            connection.setAutoCommit(true);
            try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM cards WHERE userId IS NULL ORDER BY packageId ASC")) {
                try (ResultSet rs = stmt.executeQuery()) {
                    List<Card> cards = new ArrayList<>();
                    Integer packageId = null;
                    if (rs.next()) {
                        cards.add(sqlReturnToCard(rs));
                        packageId = rs.getInt("packageId");
                    }
                    if (userId != null && packageId != null) {
                        assignUserToPackage(connection, packageId, userId);
                    }
                    return cards.isEmpty() ? null : cards;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void assignUserToPackage(Connection connection, int packageId, int userId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("UPDATE cards SET userId = ? WHERE packageId = ?")) {
            stmt.setInt(1, userId);
            stmt.setInt(2, packageId);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Card> getUserCards(int userId, boolean onlyDeck) {
        String sqlCommand = onlyDeck ? "SELECT * FROM cards WHERE userId = ? AND inDeck" : "SELECT * FROM cards WHERE userId = ?";
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement stmt = connection.prepareStatement(sqlCommand)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Card> cards = new ArrayList<>();
                while (rs.next()) {
                    cards.add(sqlReturnToCard(rs));
                }
                return cards.isEmpty() ? null : cards;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean setCardsToDeck(List<String> cardIds) {
        try (Connection connection = DriverManager.getConnection(connectionString)) {
            connection.setAutoCommit(true);
            for (String cardId : cardIds) {
                try (PreparedStatement stmt = connection.prepareStatement("UPDATE cards SET inDeck = true WHERE id = ?")) {
                    stmt.setString(1, cardId);
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected == 0) return false;
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Card> getAvailableUserCards(int userId) {
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT c.* FROM cards c LEFT JOIN trades t ON c.id = t.CardToTrade WHERE c.userId = ? AND t.CardToTrade IS NULL AND c.inDeck = false")) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Card> cards = new ArrayList<>();
                while (rs.next()) {
                    cards.add(sqlReturnToCard(rs));
                }
                return cards.isEmpty() ? null : cards;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Card getCard(String cardId) {
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM cards WHERE id = ?")) {
            stmt.setString(1, cardId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return sqlReturnToCard(rs);
                }
                throw new IllegalArgumentException("Card not found: " + cardId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateCardOwner(String cardId, int newUserId) {
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement stmt = connection.prepareStatement("UPDATE cards SET userId = ? WHERE id = ?")) {
            stmt.setInt(1, newUserId);
            stmt.setString(2, cardId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int getCardOwner(String cardId) {
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement stmt = connection.prepareStatement("SELECT userId FROM cards WHERE id = ?")) {
            stmt.setString(1, cardId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("userId");
                }
                throw new IllegalArgumentException("Card owner not found for card: " + cardId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Card sqlReturnToCard(ResultSet rs) throws SQLException {
        return new Card(
                rs.getString("id"),
                rs.getString("name"),
                rs.getFloat("damage"),
                CardType.valueOf(rs.getString("type")),
                Element.valueOf(rs.getString("element"))
        );
    }

    @Override
    public List<Card> getUserCards(String username, boolean onlyDeck) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
