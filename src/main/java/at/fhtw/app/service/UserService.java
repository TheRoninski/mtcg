package at.fhtw.app.service;

import at.fhtw.app.dal.IUserManager;
import at.fhtw.app.model.Credentials;
import at.fhtw.app.model.UserData;

public class UserService implements IUserService {

    private final IUserManager userDao;

    public UserService(IUserManager userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean createUser(Credentials credentials) {
        return userDao.insertUser(credentials, "Bearer " + credentials.username() + "-mtcgToken");
    }

    @Override
    public boolean updateUser(String username, String password) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean getUser(String username) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public String loginUser(Credentials credentials) {
        return userDao.getTokenByCredentials(credentials);
    }

    @Override
    public boolean payPackage(int userId, int amount) {
        return userDao.decreaseCoinsAmount(userId, amount);
    }

    @Override
    public void updateUserData(String username, UserData userData) {
        userDao.updateUserData(username, userData);
    }

    @Override
    public String getUserData(String username) {
        return userDao.getUserData(username);
    }
}
