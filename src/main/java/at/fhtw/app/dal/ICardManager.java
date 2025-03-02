package at.fhtw.app.dal;

import at.fhtw.app.model.Card;
import java.util.List;

public interface ICardManager {
    boolean insertCards(List<Card> cards);
    List<Card> getFreePackage(Integer userId);
    List<Card> getUserCards(int userId, boolean onlyDeck);
    boolean setCardsToDeck(List<String> cardIds);
    List<Card> getAvailableUserCards(int userId);
    Card getCard(String cardId);
    boolean updateCardOwner(String cardId, int newUserId);
    int getCardOwner(String cardId);

    // Overloaded method if you choose to use username instead of int:
    List<Card> getUserCards(String username, boolean onlyDeck);
}
