package at.fhtw.app.service;

import at.fhtw.app.dal.IUserManager;
import at.fhtw.app.exceptions.AccessTokenException;
import at.fhtw.app.model.User;

public class AuthenticationService implements IAuthenticationService {

    private final IUserManager userDao;

    public AuthenticationService(IUserManager userDao) {
        this.userDao = userDao;
    }

    @Override
    public User authenticateUser(String authToken, boolean isAdmin) {
        System.out.println("##################################" + authToken);
        if (authToken == null || authToken.isEmpty()) {
            throw new AccessTokenException("No authentication token provided");
        }
        User user = userDao.getUserByAuthToken(authToken);
        if (user == null) {
            throw new AccessTokenException("Invalid authentication token");
        }
        if (isAdmin && !user.isAdmin()) {
            throw new AccessTokenException(true);
        }
        return user;
    }
}
