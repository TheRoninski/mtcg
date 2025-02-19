package at.fhtw.app.controller;

import at.fhtw.app.model.request.CreateUserRequest;
import at.fhtw.app.service.UserService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpMethod;
import at.fhtw.httpserver.http.HttpStatus;
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
        if (!"/users".equals(request.getServiceRoute())) {
            return null;
        }

        if (request.getMethod() == HttpMethod.POST) {
            if (request.getPathParts().size() == 1) {
                String body = request.getBody();
                CreateUserRequest createUserRequest = getCreateUserRequestFromJson(body);
                boolean isSuccessful = this.userService.createUser(createUserRequest);
                if (isSuccessful) {
                    return new Response(
                            HttpStatus.CREATED,
                            ContentType.PLAIN_TEXT,
                            "User created"
                    );
                } else {
                    return new Response(
                            HttpStatus.BAD_REQUEST,
                            ContentType.PLAIN_TEXT,
                            "User already exists"
                    );
                }
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
