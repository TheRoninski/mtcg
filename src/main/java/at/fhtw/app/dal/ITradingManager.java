package at.fhtw.app.dal;

import at.fhtw.app.model.Trade;

public interface ITradingManager {
    boolean insertTrade(Trade trade);
    Trade getTrade(String tradeId);
    String getAllTrades();
    void deleteTrade(String tradeId);
}
