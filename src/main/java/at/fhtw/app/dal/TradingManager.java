package at.fhtw.app.dal;

import at.fhtw.app.model.Trade;
import at.fhtw.app.model.MonsterOrSpell;

import java.sql.*;

public class TradingManager implements ITradingManager {

    private static final String GET_TRADE_BY_ID_COMMAND = "SELECT * FROM trades WHERE id = ?";
    private static final String INSERT_TRADE_COMMAND = "INSERT INTO trades (Id, CardToTrade, Type, MinimumDamage) VALUES (?, ?, ?, ?)";
    private static final String GET_ALL_TRADES_COMMAND = "SELECT * FROM trades";
    private static final String DELETE_TRADE_COMMAND = "DELETE FROM trades WHERE id = ?";

    private final String connectionString;

    public TradingManager(String connectionString) {
        this.connectionString = connectionString;
    }

    @Override
    public boolean insertTrade(Trade trade) {
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement stmt = connection.prepareStatement(INSERT_TRADE_COMMAND)) {
            stmt.setString(1, trade.id());
            stmt.setString(2, trade.cardToTrade());
            stmt.setString(3, trade.type().toString());
            stmt.setInt(4, trade.minimumDamage());
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Trade getTrade(String tradeId) {
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement stmt = connection.prepareStatement(GET_TRADE_BY_ID_COMMAND)) {
            stmt.setString(1, tradeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Trade(
                            rs.getString("id"),
                            rs.getString("cardtotrade"),
                            MonsterOrSpell.valueOf(rs.getString("type").toUpperCase()),
                            rs.getInt("minimumdamage")
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getAllTrades() {
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement stmt = connection.prepareStatement(GET_ALL_TRADES_COMMAND);
             ResultSet rs = stmt.executeQuery()) {
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");
            boolean first = true;
            while (rs.next()) {
                if (!first) {
                    jsonBuilder.append(",");
                }
                jsonBuilder.append("\n{\n");
                jsonBuilder.append(String.format("\"id\":\"%s\",\n", rs.getString("id")));
                jsonBuilder.append(String.format("\"cardToTrade\":\"%s\",\n", rs.getString("cardtotrade")));
                jsonBuilder.append(String.format("\"type\":\"%s\",\n", rs.getString("type")));
                jsonBuilder.append(String.format("\"minimumDamage\":%d\n", rs.getInt("minimumdamage")));
                jsonBuilder.append("}\n");
                first = false;
            }
            jsonBuilder.append("]");
            return jsonBuilder.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deleteTrade(String tradeId) {
        try (Connection connection = DriverManager.getConnection(connectionString);
             PreparedStatement stmt = connection.prepareStatement(DELETE_TRADE_COMMAND)) {
            stmt.setString(1, tradeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
