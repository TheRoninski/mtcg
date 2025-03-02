package at.fhtw.app.service;

import at.fhtw.app.dal.IUserManager;
import at.fhtw.app.dal.ICardManager;
import at.fhtw.app.service.IGameService;
import at.fhtw.app.model.Card;
import at.fhtw.app.model.CardType;
import at.fhtw.app.model.Element;
import at.fhtw.app.model.User;
import at.fhtw.app.model.Winner;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameService implements IGameService {

    private final IUserManager userDao;
    private final ICardManager cardDao;
    private final ConcurrentLinkedQueue<User> playerBattleQueue;
    private final Map<Integer, String> playerLogsBattle;
    private final Object battleLock;
    private final Random rand;
    private static final int standardEloChanges = 10;

    public GameService(IUserManager userDao, ICardManager cardDao) {
        this.userDao = userDao;
        this.cardDao = cardDao;
        this.playerBattleQueue = new ConcurrentLinkedQueue<>();
        this.playerLogsBattle = new HashMap<>();
        this.battleLock = new Object();
        this.rand = new Random();
    }

    @Override
    public Winner battleCards(Card card1, Card card2) {
        Map<CardType, CardType> attackMap = new HashMap<>();
        attackMap.put(CardType.Dragon, CardType.Goblin);
        attackMap.put(CardType.Wizzard, CardType.Ork);

        if ((card1.name().equals("WaterSpell") && card2.type() == CardType.Knight) ||
                (card2.name().equals("WaterSpell") && card1.type() == CardType.Knight)) {
            return card2.name().equals("WaterSpell") ? Winner.Second : Winner.First;
        }
        if ((card1.type() == CardType.Spell && card2.type() == CardType.Kraken) ||
                (card2.type() == CardType.Spell && card1.type() == CardType.Kraken)) {
            return card2.type() == CardType.Kraken ? Winner.Second : Winner.First;
        }
        if ((card1.name().equals("FireElf") && card2.type() == CardType.Dragon) ||
                (card2.name().equals("FireElf") && card1.type() == CardType.Dragon)) {
            return card2.name().equals("FireElf") ? Winner.Second : Winner.First;
        }
        if ((card1.type() != CardType.Spell && card2.type() != CardType.Spell) ||
                card1.element() == card2.element()) {
            if ((attackMap.containsKey(card1.type()) && attackMap.get(card1.type()) == card2.type()) ||
                    (attackMap.containsKey(card2.type()) && attackMap.get(card2.type()) == card1.type())) {
                return attackMap.containsKey(card2.type()) ? Winner.Second : Winner.First;
            }
            if (card1.damage() == card2.damage())
                return Winner.Draw;
            return card2.damage() > card1.damage() ? Winner.Second : Winner.First;
        }
        Map<Element, Element> elementAttack = new HashMap<>();
        elementAttack.put(Element.Water, Element.Fire);
        elementAttack.put(Element.Fire, Element.Regular);
        elementAttack.put(Element.Regular, Element.Water);

        float damage1, damage2;
        if (elementAttack.get(card1.element()) == card2.element()) {
            damage1 = card1.damage() * 2;
            damage2 = card2.damage() / 2;
        } else {
            damage1 = card1.damage() / 2;
            damage2 = card2.damage() * 2;
        }
        if (damage1 == damage2)
            return Winner.Draw;
        return damage2 > damage1 ? Winner.Second : Winner.First;
    }

    @Override
    public String getScoreboard() {
        return userDao.getGameScoreboard();
    }

    @Override
    public String getUserScore(int userId) {
        return userDao.getUserScore(userId);
    }

    private <T> T randomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("The list is empty or null.");
        }
        int index = rand.nextInt(list.size());
        return list.get(index);
    }

    @Override
    public String waitOrStartBattle(User user) {
        List<Card> userDeck = cardDao.getUserCards(user.id(), true);
        if (userDeck == null || userDeck.isEmpty()) return null;

        synchronized (battleLock) {
            if (playerBattleQueue.isEmpty()) {
                playerBattleQueue.add(user);
                try {
                    battleLock.wait(30);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
                String thisUserBattle = playerLogsBattle.get(user.id());
                playerLogsBattle.remove(user.id());
                return thisUserBattle;
            }
        }
        User opponent = playerBattleQueue.poll();
        StringBuilder battleLog = new StringBuilder();

        for (int i = 0; i < 100; i++) {
            List<Card> thisUserCards = cardDao.getUserCards(user.id(), true);
            List<Card> opponentCards = cardDao.getUserCards(opponent.id(), true);
            if (thisUserCards == null || thisUserCards.isEmpty()) {
                battleLog.append(user.getUsername()).append(" ran out of cards\n");
                synchronized (battleLock) {
                    playerLogsBattle.put(opponent.id(), battleLog.toString());
                }
                return battleLog.toString();
            } else if (opponentCards == null || opponentCards.isEmpty()) {
                battleLog.append(opponent.getUsername()).append(" ran out of cards\n");
                synchronized (battleLock) {
                    playerLogsBattle.put(opponent.id(), battleLog.toString());
                }
                return battleLog.toString();
            }
            Card thisUserCard = randomElement(thisUserCards);
            Card opponentCard = randomElement(opponentCards);
            int thisUserElo = userDao.getUserElo(user.id());
            int opponentElo = userDao.getUserElo(opponent.id());

            int eloCalculation = Math.abs(thisUserElo - opponentElo) / 10;
            int firstUserWinElo, secondUserWinElo;
            if (thisUserElo >= opponentElo) {
                firstUserWinElo = (standardEloChanges - eloCalculation > 1) ? standardEloChanges - eloCalculation : 1;
                secondUserWinElo = 2 * standardEloChanges - firstUserWinElo;
            } else {
                secondUserWinElo = (standardEloChanges - eloCalculation > 1) ? standardEloChanges - eloCalculation : 1;
                firstUserWinElo = 2 * standardEloChanges - secondUserWinElo;
            }
            Winner winner = battleCards(thisUserCard, opponentCard);
            switch (winner) {
                case First -> {
                    userDao.changeUserElo(user.id(), firstUserWinElo);
                    userDao.changeUserElo(opponent.id(), -firstUserWinElo);
                    userDao.changeWinLosses(user.id(), true);
                    userDao.changeWinLosses(opponent.id(), false);
                    cardDao.updateCardOwner(opponentCard.id(), user.id());
                    battleLog.append(String.format("%s's %s (ELO: %d to %d) defeated %s's %s (ELO: %d to %d).\n",
                            user.getUsername(), thisUserCard.toString(), thisUserElo, userDao.getUserElo(user.id()),
                            opponent.getUsername(), opponentCard.toString(), opponentElo, userDao.getUserElo(opponent.id())));
                }
                case Second -> {
                    userDao.changeUserElo(user.id(), -secondUserWinElo);
                    userDao.changeUserElo(opponent.id(), secondUserWinElo);
                    userDao.changeWinLosses(user.id(), false);
                    userDao.changeWinLosses(opponent.id(), true);
                    cardDao.updateCardOwner(thisUserCard.id(), opponent.id());
                    battleLog.append(String.format("%s's %s (ELO: %d to %d) was defeated by %s's %s (ELO: %d to %d).\n",
                            user.getUsername(), thisUserCard.toString(), thisUserElo, userDao.getUserElo(user.id()),
                            opponent.getUsername(), opponentCard.toString(), opponentElo, userDao.getUserElo(opponent.id())));
                }
                case Draw -> {
                    userDao.changeUserElo(user.id(), -(thisUserElo - opponentElo) / 10);
                    userDao.changeUserElo(opponent.id(), (thisUserElo - opponentElo) / 10);
                    battleLog.append(String.format("It's a draw! %s's %s (ELO: %d to %d) and %s's %s (ELO: %d to %d).\n",
                            user.getUsername(), thisUserCard.toString(), thisUserElo, userDao.getUserElo(user.id()),
                            opponent.getUsername(), opponentCard.toString(), opponentElo, userDao.getUserElo(opponent.id())));
                }
            }
        }
        battleLog.append("Battle reached max round (100)\n");
        synchronized (battleLock) {
            playerLogsBattle.put(opponent.id(), battleLog.toString());
        }
        return battleLog.toString();
    }
}
