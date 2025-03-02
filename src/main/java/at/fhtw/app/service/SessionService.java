package at.fhtw.app.service;

import at.fhtw.app.model.Credentials;

public class SessionService {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String TOKEN_SUFFIX = "-mtcgToken";

    public String createSession(Credentials credentials) {
        return credentials.username() + TOKEN_SUFFIX;
    }

    public String getUsernameForRequest(String headerValue) {
        if (headerValue == null || !headerValue.startsWith(TOKEN_PREFIX)) {
            return null;
        }
        String token = headerValue.substring(TOKEN_PREFIX.length()).trim();
        if (!token.endsWith(TOKEN_SUFFIX)) {
            return null;
        }
        return token.substring(0, token.length() - TOKEN_SUFFIX.length());
    }
}
