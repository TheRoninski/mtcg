package at.fhtw.app.controller;

import at.fhtw.app.service.SessionService;
import at.fhtw.app.service.CardService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpMethod;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.util.List;

public class CardController extends AbstractController {

    private final SessionService sessionService;
    private final CardService cardService;
    private final ObjectMapper mapper = new ObjectMapper();

    public CardController(SessionService sessionService, CardService cardService) {
        this.sessionService = sessionService;
        this.cardService = cardService;
    }

    @Override
    public Response handleRequest(Request request) {
        String route = request.getServiceRoute();
        String authHeader = request.getHeaderMap().getHeader("Authorization");
        String username = sessionService.getUsernameForRequest(authHeader);

        // 1) If no token or invalid token, user is not authenticated
        if (username == null) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\":\"Unauthorized\"}");
        }

        // 2) /cards GET -> get user’s cards (replace '0' with actual userId if needed)
        if ("/cards".equals(route) && request.getMethod() == HttpMethod.Get) {
            String cardsJson = cardService.getCardsForUser(0);  // TODO: map username -> userId if needed
            return new Response(HttpStatus.OK, ContentType.JSON, cardsJson);
        }

        // 3) /deck -> GET or PUT
        if ("/deck".equals(route)) {
            if (request.getMethod() == HttpMethod.Get) {
                boolean plain = request.getParams() != null && request.getParams().contains("format=plain");
                // get the user’s deck from cardService
                String deckJson = cardService.getUserDeck(0, plain); // TODO: map username -> userId
                return new Response(HttpStatus.OK, ContentType.JSON, deckJson);

            } else if (request.getMethod() == HttpMethod.Put) {
                // parse the JSON array of card IDs using Jackson directly
                List<String> cardIds;
                try {
                    CollectionType listType = mapper.getTypeFactory()
                            .constructCollectionType(List.class, String.class);
                    cardIds = mapper.readValue(request.getBody(), listType);
                } catch (Exception e) {
                    return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                            "{\"message\":\"Invalid JSON format\"}");
                }

                // 4) check if exactly 4 cards
                if (cardIds.size() != 4) {
                    return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                            "{\"message\":\"The deck must contain exactly 4 cards\"}");
                }

                // 5) update deck in cardService
                boolean success = cardService.setUserDeck(0, cardIds); // TODO: map username -> userId
                if (success) {
                    return new Response(HttpStatus.OK, ContentType.JSON,
                            "{\"message\":\"Deck updated successfully\"}");
                } else {
                    return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                            "{\"message\":\"Invalid deck data or update failed\"}");
                }
            }
        }

        // 6) if route not matched:
        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\":\"Not Found\"}");
    }
}
