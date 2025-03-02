package at.fhtw.app.controller;

import at.fhtw.app.model.Trade;
import at.fhtw.app.model.User;
import at.fhtw.app.service.AuthenticationService;
import at.fhtw.app.service.CardService;
import at.fhtw.app.service.TradingService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpMethod;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class TradingController extends AbstractController {

    private final TradingService tradingService;
    private final CardService cardService;
    private final AuthenticationService authenticationService;
    private final ObjectMapper mapper = new ObjectMapper();

    public TradingController(TradingService tradingService, CardService cardService,
                             AuthenticationService authenticationService) {
        this.tradingService = tradingService;
        this.cardService = cardService;
        this.authenticationService = authenticationService;
    }

    private Response createTrade(String content, String authToken) {
        Trade trade = parseContent(content, Trade.class);
        User user = authenticationService.authenticateUser(authToken, false);
        List<String> cardToTrade = List.of(trade.cardToTrade());
        if (!cardService.areCardsAvailable(cardToTrade, user.id())) {
            return new Response(HttpStatus.FORBIDDEN, ContentType.JSON,
                    "{\"message\":\"The deal contains a card that is not owned by the user, locked in the deck or already in a trade.\"}");
        }
        if (!tradingService.createTrade(trade)) {
            return new Response(HttpStatus.CONFLICT, ContentType.JSON,
                    "{\"message\":\"A deal with this deal ID already exists.\"}");
        }
        return new Response(HttpStatus.CREATED, ContentType.JSON,
                "{\"message\":\"Trading deal successfully created\"}");
    }

    private Response showAllTrades(String authToken) {
        authenticationService.authenticateUser(authToken, false);
        String allTrades = tradingService.getAllTrades();
        if (allTrades == null || allTrades.isEmpty()) {
            return new Response(HttpStatus.NO_CONTENT, ContentType.JSON,
                    "{\"message\":\"The request was fine, but there are no trading deals available\"}");
        }
        return new Response(HttpStatus.OK, ContentType.JSON,
                "{\"message\":\"Trading deals available: " + allTrades + "\"}");
    }

    private Response deleteTrade(String tradeId, String authToken) {
        if (tradeId == null || tradeId.isEmpty()) {
            throw new IllegalArgumentException("Trade ID is null or empty");
        }
        User user = authenticationService.authenticateUser(authToken, false);
        Trade trade = tradingService.getTrade(tradeId);
        if (trade == null) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                    "{\"message\":\"The provided deal ID was not found.\"}");
        }
        if (!cardService.isCardFromUser(trade.cardToTrade(), user.id())) {
            return new Response(HttpStatus.FORBIDDEN, ContentType.JSON,
                    "{\"message\":\"The deal contains a card that is not owned by the user.\"}");
        }
        tradingService.deleteTrade(tradeId);
        return new Response(HttpStatus.OK, ContentType.JSON,
                "{\"message\":\"Trading deal successfully deleted\"}");
    }

    private Response acceptTrade(String tradeId, String content, String authToken) {
        if (tradeId == null || content == null || tradeId.isEmpty() || content.isEmpty()) {
            throw new IllegalArgumentException("Trade ID or card ID is null or empty");
        }
        String cardToTradeId = content.trim().replaceAll("^\"|\"$", "");
        User user = authenticationService.authenticateUser(authToken, false);
        Trade trade = tradingService.getTrade(tradeId);
        if (trade == null) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                    "{\"message\":\"The provided deal ID was not found.\"}");
        }
        if (cardService.isCardFromUser(trade.cardToTrade(), user.id()) ||
                !cardService.isCardFromUser(cardToTradeId, user.id()) ||
                cardService.isCardInDeck(cardToTradeId, user.id()) ||
                !tradingService.isCardMeetingRequirements(trade, cardToTradeId)) {
            return new Response(HttpStatus.FORBIDDEN, ContentType.JSON,
                    "{\"message\":\"The offered card is not owned by the user, or the requirements are not met, or the card is locked in the deck, or you are trying to trade with yourself.\"}");
        }
        tradingService.executeTrade(trade, cardToTradeId);
        return new Response(HttpStatus.OK, ContentType.JSON,
                "{\"message\":\"Trading deal successfully executed.\"}");
    }

    @Override
    public Response handleRequest(Request httpRequest) {
        String route = httpRequest.getServiceRoute();
        HttpMethod method = httpRequest.getMethod();
        String subRouteParameter = httpRequest.getPathParts().size() > 1 ? httpRequest.getPathParts().get(1) : null;
        String content = httpRequest.getBody();
        String authToken = httpRequest.getHeaderMap().getHeader("Authorization");

        if ("/tradings".equals(route)) {
            if (method == HttpMethod.Post && subRouteParameter == null) {
                return createTrade(content, authToken);
            } else if (method == HttpMethod.Get) {
                return showAllTrades(authToken);
            } else if (method == HttpMethod.Delete) {
                return deleteTrade(subRouteParameter, authToken);
            } else if (method == HttpMethod.Post) {
                return acceptTrade(subRouteParameter, content, authToken);
            }
        }
        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\":\"Not Found\"}");
    }
}
