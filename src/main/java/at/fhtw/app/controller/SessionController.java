package at.fhtw.app.controller;

import at.fhtw.app.model.request.LoginRequest;
import at.fhtw.app.service.SessionService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpMethod;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SessionController implements at.fhtw.httpserver.server.Controller {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final SessionService sessionService = new SessionService();

    @Override
    public Response handleRequest(Request request) {
        if (!"/sessions".equals(request.getServiceRoute())) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{\"message\": \"Not Found\"}"
            );
        }
        if (request.getMethod() == HttpMethod.POST) {
            LoginRequest loginRequest = getLoginRequestFromJson(request.getBody());
            if (loginRequest == null) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{\"message\": \"Invalid request body\"}"
                );
            }
            String token = sessionService.createSession(loginRequest);
            if (token != null) {
                return new Response(
                        HttpStatus.CREATED,
                        ContentType.JSON,
                        "{\"token\": \"" + token + "\"}"
                );
            } else {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{\"message\": \"Invalid credentials\"}"
                );
            }
        }
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{\"message\": \"Method Not Allowed\"}"
        );
    }

    private LoginRequest getLoginRequestFromJson(String json) {
        try {
            return mapper.readValue(json, LoginRequest.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
