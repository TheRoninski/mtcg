package at.fhtw.app.service;

import at.fhtw.app.dal.ICardManager;
import at.fhtw.app.dal.ITradingManager;
import at.fhtw.app.service.ITradingService;
import at.fhtw.app.model.Card;
import at.fhtw.app.model.CardType;
import at.fhtw.app.model.MonsterOrSpell;
import at.fhtw.app.model.Trade;

public class TradingService implements ITradingService {

    private final ITradingManager tradingManager;
    private final ICardManager cardManager;

    public TradingService(ITradingManager tradingManager, ICardManager cardManager) {
        this.tradingManager = tradingManager;
        this.cardManager = cardManager;
    }

    @Override
    public boolean createTrade(Trade trade) {
        if (tradingManager.getTrade(trade.id()) != null) {
            return false;
        }
        tradingManager.insertTrade(trade);
        return true;
    }

    @Override
    public void deleteTrade(String tradeId) {
        tradingManager.deleteTrade(tradeId);
    }

    @Override
    public void executeTrade(Trade trade, String cardToTradeId) {
        int tradeUserId = cardManager.getCardOwner(trade.cardToTrade());
        int acceptedUserId = cardManager.getCardOwner(cardToTradeId);
        cardManager.updateCardOwner(trade.cardToTrade(), acceptedUserId);
        cardManager.updateCardOwner(cardToTradeId, tradeUserId);
        tradingManager.deleteTrade(trade.id());
    }

    @Override
    public String getAllTrades() {
        return tradingManager.getAllTrades();
    }

    @Override
    public Trade getTrade(String tradeId) {
        return tradingManager.getTrade(tradeId);
    }

    @Override
    public boolean isCardMeetingRequirements(Trade trade, String cardToTradeId) {
        Card card = cardManager.getCard(cardToTradeId);
        if ((trade.type() == MonsterOrSpell.Spell && card.type() != CardType.Spell) ||
                (trade.type() == MonsterOrSpell.Monster && card.type() == CardType.Spell)) {
            return false;
        }
        return card.damage() >= trade.minimumDamage();
    }
}
