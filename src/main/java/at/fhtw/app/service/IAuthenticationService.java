package at.fhtw.app.service;

import at.fhtw.app.model.User;

public interface IAuthenticationService {
    User authenticateUser(String authToken, boolean isAdmin);
}
