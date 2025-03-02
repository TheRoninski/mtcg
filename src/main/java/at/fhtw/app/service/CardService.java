package at.fhtw.app.service;

import at.fhtw.app.dal.ICardManager;
import at.fhtw.app.model.Card;
import at.fhtw.app.util.CardHelper;
import java.util.List;

public class CardService implements ICardService {

    private final ICardManager cardsManager;

    public CardService(ICardManager cardsManager) {
        this.cardsManager = cardsManager;
    }

    @Override
    public boolean areCardsAvailable(List<String> cardIds, int userId) {
        List<Card> cards = cardsManager.getAvailableUserCards(userId);
        if (cards == null) return false;
        for (String cardId : cardIds) {
            if (cards.stream().noneMatch(card -> card.id().equals(cardId))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getCardsForUser(int userId) {
        List<Card> cards = cardsManager.getUserCards(userId, false);
        return CardHelper.mapCardsToResponse(cards, false);
    }

    @Override
    public String getUserDeck(int userId, boolean plain) {
        List<Card> cards = cardsManager.getUserCards(userId, true);
        return CardHelper.mapCardsToResponse(cards, plain);
    }

    @Override
    public boolean isCardFromUser(String cardId, int userId) {
        List<Card> cards = cardsManager.getUserCards(userId, false);
        return cards != null && cards.stream().anyMatch(card -> card.id().equals(cardId));
    }

    @Override
    public boolean isCardInDeck(String cardId, int userId) {
        List<Card> cards = cardsManager.getUserCards(userId, true);
        return cards != null && cards.stream().anyMatch(card -> card.id().equals(cardId));
    }

    @Override
    public boolean setUserDeck(int userId, List<String> cardIds) {
        if (!areCardsAvailable(cardIds, userId)) return false;
        return cardsManager.setCardsToDeck(cardIds);
    }
}
