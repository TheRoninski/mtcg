package at.fhtw;

import at.fhtw.app.controller.SessionController;
import at.fhtw.app.controller.UserController;
import at.fhtw.app.controller.TransactionController;
import at.fhtw.app.controller.CardsController;
import at.fhtw.app.controller.DeckController;
import at.fhtw.app.controller.EloController;

import at.fhtw.httpserver.server.Server;
import at.fhtw.httpserver.utils.Router;
import at.fhtw.sampleapp.service.echo.EchoService;
import at.fhtw.sampleapp.service.weather.WeatherService;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(10001, configureRouter());
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Router configureRouter() {
        Router router = new Router();
        router.addController("/users", new UserController());
        router.addController("/sessions", new SessionController());
        router.addController("/transactions/packages", new TransactionController());
        router.addController("/cards", new CardsController());
        router.addController("/deck", new DeckController());
        router.addController("/stats", new EloController());
        return router;
    }
}
