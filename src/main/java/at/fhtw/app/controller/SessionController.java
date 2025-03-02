package at.fhtw.app.controller;

import at.fhtw.app.model.Credentials;
import at.fhtw.app.service.SessionService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpMethod;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;

public class SessionController extends AbstractController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    private Response login(String content) {
        Credentials credentials = parseContent(content, Credentials.class);
        String token = sessionService.createSession(credentials);
        if (token != null) {
            return new Response(HttpStatus.OK, ContentType.JSON, "{\"token\": \"" + token + "\"}");
        } else {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\":\"Invalid credentials\"}");
        }
    }

    @Override
    public Response handleRequest(Request request) {
        if (!"/sessions".equals(request.getServiceRoute())) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\":\"Not Found\"}");
        }
        if (request.getMethod() == HttpMethod.Post) {
            return login(request.getBody());
        }
        return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\":\"Method Not Allowed\"}");
    }
}
