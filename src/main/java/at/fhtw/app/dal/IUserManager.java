package at.fhtw.app.dal;

import at.fhtw.app.model.Credentials;
import at.fhtw.app.model.User;
import at.fhtw.app.model.UserData;

public interface IUserManager {
    String getTokenByCredentials(Credentials credentials);
    boolean insertUser(Credentials credentials, String authToken);
    User getUserByAuthToken(String authToken);
    boolean decreaseCoinsAmount(int userId, int amount);
    String getGameScoreboard();
    String getUserScore(int userId);
    void changeUserElo(int userId, int amount);
    void changeWinLosses(int userId, boolean win);
    void updateUserData(String username, UserData userData);
    String getUserData(String username);
    int getUserElo(int userId);
}
