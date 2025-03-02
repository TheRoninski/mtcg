package at.fhtw.app.service;

import at.fhtw.app.dal.ICardManager;
import at.fhtw.app.service.IPackageService;
import at.fhtw.app.model.Card;
import at.fhtw.app.model.CardType;
import at.fhtw.app.model.Element;
import at.fhtw.app.model.RawRequestCard;
import at.fhtw.app.util.CardHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PackageService implements IPackageService {

    private static final String NAMES_PATTERN = "(?=[A-Z])";
    private final ICardManager cardsManager;

    public PackageService(ICardManager cardsManager) {
        this.cardsManager = cardsManager;
    }

    @Override
    public List<Card> createNewPackage(List<RawRequestCard> cards) {
        List<Card> pkg = new ArrayList<>();
        for (RawRequestCard raw : cards) {
            String[] splittedNames = Pattern.compile(NAMES_PATTERN).split(raw.name());
            if (splittedNames.length == 2) {
                Element element;
                switch (splittedNames[1]) {
                    case "Knight":
                        element = Element.Regular;
                        break;
                    case "Dragon":
                        element = Element.Fire;
                        break;
                    case "Ork":
                        element = Element.Regular;
                        break;
                    case "Kraken":
                        element = Element.Water;
                        break;
                    case "Wizzard":
                        element = Element.Fire;
                        break;
                    default:
                        throw new IllegalArgumentException("Card Name not known: " + splittedNames[1]);
                }
                CardType type;
                try {
                    type = CardType.valueOf(splittedNames[1]);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Card type not known for: " + splittedNames[1]);
                }
                pkg.add(new Card(raw.id(), raw.name(), raw.damage(), type, element));
            } else if (splittedNames.length == 3) {
                Element element;
                try {
                    element = Element.valueOf(splittedNames[1]);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Element not known: " + splittedNames[1]);
                }
                if ("Spell".equals(splittedNames[2])) {
                    pkg.add(new Card(raw.id(), raw.name(), raw.damage(), CardType.Spell, element));
                    continue;
                }
                CardType type;
                try {
                    type = CardType.valueOf(splittedNames[2]);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Species not known: " + splittedNames[2]);
                }
                pkg.add(new Card(raw.id(), raw.name(), raw.damage(), type, element));
            } else {
                throw new IllegalArgumentException("Card Name not known: " + raw.name());
            }
        }
        return pkg;
    }

    @Override
    public boolean savePackage(List<Card> cards) {
        return cardsManager.insertCards(cards);
    }

    @Override
    public boolean isPackageAvailable() {
        return cardsManager.getFreePackage(null) != null;
    }

    @Override
    public String assignUserToPackage(int userId) {
        List<Card> cards = cardsManager.getFreePackage(userId);
        return CardHelper.mapCardsToResponse(cards, false);
    }
}
