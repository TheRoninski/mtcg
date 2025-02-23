package at.fhtw.app.controller;

import at.fhtw.app.service.PackageService;
import at.fhtw.app.service.SessionService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpMethod;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;

public class TransactionController implements at.fhtw.httpserver.server.Controller {

    private final SessionService sessionService = new SessionService();
    private final PackageService packageService = new PackageService();

    @Override
    public Response handleRequest(Request request) {
        if (!"/transactions/packages".equals(request.getServiceRoute())) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\":\"Not Found\"}");
        }
        if (request.getMethod() != HttpMethod.POST) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\":\"Method Not Allowed\"}");
        }
        String username = sessionService.getUsernameForRequest(request);
        if (username == null) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\":\"Unauthorized\"}");
        }
        boolean success = packageService.acquirePackage(username);
        if (success) {
            return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\":\"Package acquired\"}");
        } else {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\":\"Not enough coins or no package available\"}");
        }
    }
}
