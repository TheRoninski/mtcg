package at.fhtw.app.controller;

import at.fhtw.app.model.User;
import at.fhtw.app.service.AuthenticationService;
import at.fhtw.app.service.IGameService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpMethod;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;

public class GameController extends AbstractController {

    private final IGameService gameService;
    private final AuthenticationService authenticationService;

    public GameController(IGameService gameService, AuthenticationService authenticationService) {
        this.gameService = gameService;
        this.authenticationService = authenticationService;
    }

    private Response getUserStats(String authToken) {
        User user = authenticationService.authenticateUser(authToken, false);
        String userScore = gameService.getUserScore(user.id());
        return new Response(HttpStatus.OK, ContentType.JSON,
                "{\"message\":\"The stats could be retrieved successfully.\\n" + userScore + "\"}");
    }

    private Response getScoreboard(String authToken) {
        authenticationService.authenticateUser(authToken, false);
        String scoreboard = gameService.getScoreboard();
        return new Response(HttpStatus.OK, ContentType.JSON,
                "{\"message\":\"The scoreboard could be retrieved successfully.\\n" + scoreboard + "\"}");
    }

    private Response battle(String authToken) {
        User user = authenticationService.authenticateUser(authToken, false);
        String battleLog = gameService.waitOrStartBattle(user);
        if (battleLog == null) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                    "{\"message\":\"The User deck was not set.\"}");
        }
        return new Response(HttpStatus.OK, ContentType.JSON,
                "{\"message\":\"The battle has been carried out successfully.\\n" + battleLog + "\"}");
    }

    @Override
    public Response handleRequest(Request request) {
        String route = request.getServiceRoute();
        HttpMethod method = request.getMethod();
        String authToken = request.getHeaderMap().getHeader("Authorization");

        if ("/stats".equals(route)) {
            return getUserStats(authToken);
        } else if ("/scoreboard".equals(route)) {
            return getScoreboard(authToken);
        } else if ("/battles".equals(route) && method == HttpMethod.Post) {
            return battle(authToken);
        }
        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\":\"Not Found\"}");
    }
}
