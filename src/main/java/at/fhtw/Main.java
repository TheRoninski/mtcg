package at.fhtw;

import at.fhtw.app.controller.*;
import at.fhtw.app.dal.*;
import at.fhtw.app.service.*;
import at.fhtw.httpserver.server.TCPServer;

public class Main {
    public static void main(String[] args) {
        String connectionString = "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=postgres";

        // Instantiate DAL
        IUserManager userManager = new UserManager(connectionString);
        ICardManager cardsManager = new CardsManager(connectionString);
        ITradingManager tradingManager = new TradingManager(connectionString);

        // Instantiate Services
        AuthenticationService authenticationService = new AuthenticationService(userManager);
        UserService userService = new UserService(userManager);
        IPackageService packageService = new PackageService(cardsManager);
        CardService cardService = new CardService(cardsManager);
        IGameService gameService = new GameService(userManager, cardsManager);
        TradingService tradingService = new TradingService(tradingManager, cardsManager);
        // Additional
        SessionService sessionService = new SessionService();

        // Instantiate Controllers (all extend AbstractController)
        AbstractController packageController = new PackageController(packageService, userService, authenticationService);
        AbstractController userController = new UserController(userService, authenticationService);
        AbstractController cardController = new CardController(sessionService, cardService);
        AbstractController gameController = new GameController(gameService, authenticationService);
        AbstractController tradingController = new TradingController(tradingService, cardService, authenticationService);

        // Create HttpService (implements IHttpService)
        IHttpService httpService = new HttpService(
                userController,
                packageController,
                cardController,
                gameController,
                tradingController
        );

        // Start TCP server
        TCPServer server = new TCPServer(10001, httpService);
        try {
            server.listen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
