package at.fhtw.app.controller;

import at.fhtw.app.service.EloService;
import at.fhtw.app.service.SessionService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpMethod;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;

public class EloController implements at.fhtw.httpserver.server.Controller {

    private final SessionService sessionService = new SessionService();
    private final EloService eloService = new EloService();

    @Override
    public Response handleRequest(Request request) {
        if (!"/elo".equals(request.getServiceRoute())) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\":\"Not Found\"}");
        }
        if (request.getMethod() != HttpMethod.GET) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\":\"Method Not Allowed\"}");
        }
        String username = sessionService.getUsernameForRequest(request);
        if (username == null) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\":\"Unauthorized\"}");
        }
        String eloJson = eloService.getEloForUser(username);
        return new Response(HttpStatus.OK, ContentType.JSON, eloJson);
    }
}



