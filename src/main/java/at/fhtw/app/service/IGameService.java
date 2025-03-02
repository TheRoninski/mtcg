package at.fhtw.app.service;

import at.fhtw.app.model.Card;
import at.fhtw.app.model.User;
import at.fhtw.app.model.Winner;

public interface IGameService {
    Winner battleCards(Card card1, Card card2);
    String getUserScore(int userId);
    String getScoreboard();
    String waitOrStartBattle(User user);
}
