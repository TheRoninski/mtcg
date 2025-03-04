package at.fhtw.app.dal;

import at.fhtw.app.model.Credentials;
import at.fhtw.app.model.User;
import at.fhtw.app.model.UserData;
import at.fhtw.app.exceptions.UserNotFoundException;
import at.fhtw.app.dal.DatabaseTables;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;

public class UserManager implements IUserManager {

    private static final String INSERT_USER_COMMAND = "INSERT INTO users(username, password, token, is_admin) VALUES (?, ?, ?, ?)";
    private static final String GET_USER_BY_AUTH_TOKEN_COMMAND = "SELECT * FROM users WHERE token = ?";
    private static final String SELECT_TOKEN_BY_CREDENTIALS_COMMAND = "SELECT token FROM users WHERE username = ? AND password = ?";
    private static final String UPDATE_COINS_COMMAND = "UPDATE users SET coins = ? WHERE id = ?";
    private static final String GET_USER_COMMAND = "SELECT * FROM users WHERE id = ?";
    private static final String GET_GAME_SCOREBOARD_COMMAND = "SELECT * FROM users ORDER BY elo DESC";
    private static final String GET_USER_ELO_COMMAND = "SELECT elo FROM users WHERE id = ?";
    private static final String UPDATE_ELO_COMMAND = "UPDATE users SET elo = ? WHERE id = ?";
    private static final String GET_USER_WINS_COMMAND = "SELECT wins FROM users WHERE id = ?";
    private static final String UPDATE_USER_WINS_COMMAND = "UPDATE users SET wins = ? WHERE id = ?";
    private static final String GET_USER_LOSSES_COMMAND = "SELECT losses FROM users WHERE id = ?";
    private static final String UPDATE_USER_LOSSES_COMMAND = "UPDATE users SET losses = ? WHERE id = ?";
    private static final String UPDATE_USER_DATA_COMMAND = "UPDATE users SET name = ?, bio = ?, image = ? WHERE username = ?";
    private static final String GET_USER_BY_NAME_COMMAND = "SELECT * FROM users WHERE username = ?";

    private final String connectionString;

    public UserManager(String connectionString) {
        this.connectionString = connectionString;
        try (Connection connection = DriverManager.getConnection(connectionString)) {
            //DatabaseTables.ensureTables(connection);
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing UserManager", e);
        }
    }

    @Override
    public String getTokenByCredentials(Credentials credentials) {
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement stmt = connection.prepareStatement(SELECT_TOKEN_BY_CREDENTIALS_COMMAND)) {
            stmt.setString(1, credentials.username());
            stmt.setString(2, credentials.password());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("token");
                }
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean insertUser(Credentials credentials, String authToken) {
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement stmt = connection.prepareStatement(INSERT_USER_COMMAND)) {
            stmt.setString(1, credentials.username());
            stmt.setString(2, credentials.password());
            stmt.setString(3, authToken);
            stmt.setBoolean(4, credentials.username().equals("admin"));
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) { // Duplicate key
                return false;
            }
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public User getUserByAuthToken(String authToken) {
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement stmt = connection.prepareStatement(GET_USER_BY_AUTH_TOKEN_COMMAND)) {
            stmt.setString(1, authToken);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("token"),
                            rs.getInt("coins"),
                            rs.getInt("elo"),
                            rs.getBoolean("is_admin")
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int getUserCoins(Connection connection, int userId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(GET_USER_COMMAND)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("coins");
                } else {
                    throw new IllegalArgumentException("User not found: " + userId);
                }
            }
        }
    }

    @Override
    public boolean decreaseCoinsAmount(int userId, int amount) {
        try (Connection connection = DriverManager.getConnection(connectionString)) {
            connection.setAutoCommit(false);
            int coins = getUserCoins(connection, userId);
            if (coins - amount < 0) {
                connection.rollback();
                return false;
            }
            try (PreparedStatement stmt = connection.prepareStatement(UPDATE_COINS_COMMAND)) {
                stmt.setInt(1, coins - amount);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
            }
            connection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String readerToJson(ResultSet rs) throws SQLException {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        boolean first = true;
        while (rs.next()) {
            if (!first) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("\n{\n");
            jsonBuilder.append(String.format("\"Name\":\"%s\",\n", rs.getString("username")));
            jsonBuilder.append(String.format("\"Elo\":%d,\n", rs.getInt("elo")));
            jsonBuilder.append(String.format("\"Wins\":%d,\n", rs.getInt("wins")));
            jsonBuilder.append(String.format("\"Losses\":%d\n", rs.getInt("losses")));
            jsonBuilder.append("}\n");
            first = false;
        }
        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }

    @Override
    public String getGameScoreboard() {
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement stmt = connection.prepareStatement(GET_GAME_SCOREBOARD_COMMAND);
             ResultSet rs = stmt.executeQuery()) {
            return readerToJson(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return "[]";
        }
    }

    @Override
    public String getUserScore(int userId) {
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement stmt = connection.prepareStatement(GET_USER_COMMAND)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return readerToJson(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "[]";
        }
    }

    private int getUserElo(Connection connection, int userId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(GET_USER_ELO_COMMAND)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("elo");
                } else {
                    throw new IllegalArgumentException("User not found: " + userId);
                }
            }
        }
    }

    @Override
    public void changeUserElo(int userId, int amount) {
        try (Connection connection = DriverManager.getConnection(connectionString)) {
            connection.setAutoCommit(false);
            int elo = getUserElo(connection, userId);
            int newElo = Math.max(elo + amount, 0);
            try (PreparedStatement stmt = connection.prepareStatement(UPDATE_ELO_COMMAND)) {
                stmt.setInt(1, newElo);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getUserLosses(Connection connection, int userId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(GET_USER_LOSSES_COMMAND)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("losses");
                } else {
                    throw new IllegalArgumentException("User not found: " + userId);
                }
            }
        }
    }

    private int getUserWins(Connection connection, int userId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(GET_USER_WINS_COMMAND)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("wins");
                } else {
                    throw new IllegalArgumentException("User not found: " + userId);
                }
            }
        }
    }

    @Override
    public void changeWinLosses(int userId, boolean win) {
        try (Connection connection = DriverManager.getConnection(connectionString)) {
            connection.setAutoCommit(false);
            if (win) {
                int newWins = getUserWins(connection, userId) + 1;
                try (PreparedStatement stmt = connection.prepareStatement(UPDATE_USER_WINS_COMMAND)) {
                    stmt.setInt(1, newWins);
                    stmt.setInt(2, userId);
                    stmt.executeUpdate();
                }
            } else {
                int newLosses = getUserLosses(connection, userId) + 1;
                try (PreparedStatement stmt = connection.prepareStatement(UPDATE_USER_LOSSES_COMMAND)) {
                    stmt.setInt(1, newLosses);
                    stmt.setInt(2, userId);
                    stmt.executeUpdate();
                }
            }
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateUserData(String username, UserData userData) {
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement stmt = connection.prepareStatement(UPDATE_USER_DATA_COMMAND)) {
            stmt.setString(1, userData.name());
            stmt.setString(2, userData.bio());
            stmt.setString(3, userData.image());
            stmt.setString(4, username);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new IllegalArgumentException("User not found: " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUserData(String username) {
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement stmt = connection.prepareStatement(GET_USER_BY_NAME_COMMAND)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean isNameNull = rs.getObject("name") == null;
                    boolean isBioNull = rs.getObject("bio") == null;
                    boolean isImageNull = rs.getObject("image") == null;
                    if (isNameNull || isBioNull || isImageNull) {
                        return null;
                    }
                    ObjectMapper mapper = new ObjectMapper();
                    var userData = new Object() {
                        public final String name = rs.getString("name");
                        public final String bio = rs.getString("bio");
                        public final String image = rs.getString("image");
                    };
                    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userData);
                } else {
                    throw new IllegalArgumentException("User not found: " + username);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getUserElo(int userId) {
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement stmt = connection.prepareStatement(GET_USER_ELO_COMMAND)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("elo");
                } else {
                    throw new IllegalArgumentException("User not found: " + userId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
