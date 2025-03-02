package at.fhtw.app.service;

import java.util.List;

public interface ICardService {
    String getCardsForUser(int userId);
    String getUserDeck(int userId, boolean plain);
    boolean areCardsAvailable(List<String> cardIds, int userId);
    boolean setUserDeck(int userId, List<String> cardIds);
    boolean isCardFromUser(String cardId, int userId);
    boolean isCardInDeck(String cardId, int userId);
}
