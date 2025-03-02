package at.fhtw.app.controller;

import at.fhtw.app.model.Credentials;
import at.fhtw.app.model.User;
import at.fhtw.app.model.UserData;
import at.fhtw.app.service.AuthenticationService;
import at.fhtw.app.service.UserService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpMethod;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;

public class UserController extends AbstractController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public UserController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    private Response login(String content) {
        Credentials credentials = parseContent(content, Credentials.class);
        String token = userService.loginUser(credentials);
        if (token == null) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\":\"Invalid username/password provided\"}");
        }
        return new Response(HttpStatus.OK, ContentType.JSON, "{\"token\":\"" + token + "\"}");
    }

    private Response register(String content) {
        Credentials credentials = parseContent(content, Credentials.class);
        boolean created = userService.createUser(credentials);
        if (created) {
            return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\":\"User created\"}");
        } else {
            return new Response(HttpStatus.CONFLICT, ContentType.JSON, "{\"message\":\"User with same username already registered\"}");
        }
    }

    private Response getUserData(String authToken, String username) {
        if (username == null || username.isEmpty()) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\":\"User not found.\"}");
        }
        User user = authenticationService.authenticateUser(authToken, false);
        if (!user.isAdmin() && !username.equals(user.username())) {
            return new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "{\"message\":\"Access denied.\"}");
        }
        try {
            String userData = userService.getUserData(username);
            if (userData == null) {
                return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\":\"User found but user data was not set.\"}");
            }
            return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\":\"Data successfully retrieved.\\n" + userData + "\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\":\"User not found.\"}");
        }
    }

    private Response setUserData(String authToken, String username, String content) {
        if (username == null || username.isEmpty()) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\":\"User not found.\"}");
        }
        User user = authenticationService.authenticateUser(authToken, false);
        if (!user.isAdmin() && !username.equals(user.username())) {
            return new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "{\"message\":\"Access denied.\"}");
        }
        UserData userData = parseContent(content, UserData.class);
        try {
            userService.updateUserData(username, userData);
        } catch (Exception e) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\":\"User not found.\"}");
        }
        return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\":\"User successfully updated.\"}");
    }

    @Override
    public Response handleRequest(Request request) {
        String route = request.getServiceRoute();
        HttpMethod method = request.getMethod();
        String subRouteParameter = request.getPathParts().size() > 1 ? request.getPathParts().get(1) : null;
        String content = request.getBody();
        String authToken = request.getHeaderMap().getHeader("Authorization");

        if ("/users".equals(route)) {
            if (method == HttpMethod.Post) {
                return register(content);
            } else if (method == HttpMethod.Get) {
                return getUserData(authToken, subRouteParameter);
            } else if (method == HttpMethod.Put) {
                return setUserData(authToken, subRouteParameter, content);
            }
        } else if ("/sessions".equals(route) && method == HttpMethod.Post) {
            return login(content);
        }
        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\":\"Not Found\"}");
    }
}
