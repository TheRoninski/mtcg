package at.fhtw.app.service;

import at.fhtw.app.model.Trade;

public interface ITradingService {
    boolean createTrade(Trade trade);
    String getAllTrades();
    Trade getTrade(String tradeId);
    void deleteTrade(String tradeId);
    boolean isCardMeetingRequirements(Trade trade, String cardToTradeId);
    void executeTrade(Trade trade, String cardToTradeId);
}
