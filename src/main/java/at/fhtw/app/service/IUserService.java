package at.fhtw.app.service;

import at.fhtw.app.model.Credentials;
import at.fhtw.app.model.UserData;

public interface IUserService {
    boolean createUser(Credentials credentials);
    boolean updateUser(String username, String password);
    boolean getUser(String username);
    String loginUser(Credentials credentials);
    boolean payPackage(int userId, int amount);
    void updateUserData(String username, UserData userData);
    String getUserData(String username);
}
