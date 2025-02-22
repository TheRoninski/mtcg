package at.fhtw.app.service;

import at.fhtw.app.model.request.LoginRequest;
import at.fhtw.app.repository.UserRepository;
import at.fhtw.app.database.UnitOfWork;
import at.fhtw.httpserver.server.Request;

public class SessionService {

    public String createSession(LoginRequest loginRequest) {
        UnitOfWork unitOfWork = new UnitOfWork();
        try (unitOfWork) {
            UserRepository userRepository = new UserRepository(unitOfWork);
            if (userRepository.validateUser(loginRequest.getUsername(), loginRequest.getPassword())) {
                String token = loginRequest.getUsername() + "-mtcgToken";
                unitOfWork.commitTransaction();
                return token;
            }
        } catch (Exception e) {
            unitOfWork.rollbackTransaction();
            e.printStackTrace();
        }
        return null;
    }
// bearer username-mtcg
    private static String TOKEN_PREFIX = "Bearer ";
    private static String TOKEN_SUFFIX = "-mtcgToken";


    public String getUsernameForRequest(Request request) {
        String authorizedHeader = request.getHeaderMap().getHeader("Authorization");
        if (authorizedHeader == null || !authorizedHeader.startsWith(TOKEN_PREFIX)) {
            return null;
        }
        String token = authorizedHeader.substring(TOKEN_PREFIX.length()).trim();
        if (!token.endsWith(TOKEN_SUFFIX)) {
            return null;
        }
        return token.substring(TOKEN_PREFIX.length(), token.length() - TOKEN_SUFFIX.length());
    }
}