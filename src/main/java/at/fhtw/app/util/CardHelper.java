package at.fhtw.app.util;

import at.fhtw.app.model.Card;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.List;

public class CardHelper {

    private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static String mapCardsToResponse(List<Card> cards, boolean plain) {
        if (cards == null) return null;

        if (plain) {
            StringBuilder sb = new StringBuilder();
            sb.append("|||||---------------------------------------------------------------|||||\n");
            for (int i = 0; i < cards.size(); i++) {
                Card card = cards.get(i);
                sb.append("     Id = ").append(card.id()).append("\n");
                sb.append("     Name = ").append(card.name()).append("\n");
                sb.append("     Damage = ").append(card.damage()).append("\n");
                if (i < cards.size() - 1) {
                    sb.append("     ---------------------------------------------------------------\n");
                }
            }
            sb.append("|||||---------------------------------------------------------------|||||\n");
            return sb.toString();
        } else {
            try {
                return mapper.writeValueAsString(cards);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
