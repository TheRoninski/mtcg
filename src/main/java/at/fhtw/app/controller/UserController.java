package at.fhtw.app.controller;

import at.fhtw.app.model.request.CreateUserRequest;
import at.fhtw.app.service.UserService;
import at.fhtw.httpserver.http.HttpMethod;
import at.fhtw.httpserver.server.Controller;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserController implements Controller {

    private static final ObjectMapper mapper = new ObjectMapper();

    private final UserService userService = new UserService();

    @Override
    public Response handleRequest(Request request) {
        // 1. Schritt: checken welche Methode das ist, ob Methode = POST ist
        // 2. Schritt: schauen ob der RequestPfad nur aus "/users" besteht
        // 3. Schritt: RequestBody muss in ein Java Object umgewandelt werden
        // 4. Schritt: User soll in der Datenbank auch erstellt werden
        if (!"/users".equals(request.getServiceRoute())) {
            return null;
        }

        if (request.getMethod() == HttpMethod.POST) {
            if (request.getPathParts().size() == 1) {
                String body = request.getBody();
                CreateUserRequest createUserRequest = getCreateUserRequestFromJson(body);
                this.userService.createUser(createUserRequest);
            }
        }
        return null;
    }

    private CreateUserRequest getCreateUserRequestFromJson(String json) {
        try {
            return mapper.readValue(json, CreateUserRequest.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
