package at.fhtw.app.service;

import at.fhtw.app.model.Card;
import at.fhtw.app.model.RawRequestCard;
import java.util.List;

public interface IPackageService {
    List<Card> createNewPackage(List<RawRequestCard> cards);
    boolean savePackage(List<Card> cards);
    boolean isPackageAvailable();
    String assignUserToPackage(int userId);
}
