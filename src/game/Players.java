package game;

import game.cards.models.Card;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public record Players(Player firstPlayer, Player secondPlayer) {
    public static Players empty() {
        return new Players(new Player(), new Player());
    }

    public Player getPlayer(User player) throws NullPointerException {
        if (firstPlayer.isUser(player)) return firstPlayer;
        if (secondPlayer.isUser(player)) return secondPlayer;
        throw new NullPointerException("Player not exists");
    }

    public Player getOther(User player) throws NullPointerException {
        if (!firstPlayer.isUser(player)) return firstPlayer;
        if (!secondPlayer.isUser(player)) return secondPlayer;
        throw new NullPointerException("All Players are same");
    }

    public Inventory getInventory(User player) throws NullPointerException {
        return getPlayer(player).inventory;
    }

    public boolean hasPlayer(User player) {
        return firstPlayer.isUser(player) || secondPlayer.isUser(player);
    }

    public void add(User player) {
        if (firstPlayer.user == null) firstPlayer.set(player, new Inventory(), this);
        else if (secondPlayer.user == null) secondPlayer.set(player, new Inventory(), this);
    }

    public boolean isFulled() {
        return firstPlayer.user != null && secondPlayer.user != null;
    }

    public boolean sameBad() {
        return firstPlayer.isBad == secondPlayer.isBad;
    }

    public Player getBadPlayer() {
        return firstPlayer.isBad? firstPlayer : secondPlayer.isBad? secondPlayer : null;
    }

    public Player switchTurn() {
        Player user = null;
        if (firstPlayer.isHisTurn(firstPlayer.notHisTurn()))
            user = firstPlayer;
        if (secondPlayer.isHisTurn(secondPlayer.notHisTurn()))
            user = secondPlayer;

        if (user != null) return user;
        else throw new IllegalStateException("No player can use card now");
    }

    public Player getCurrentRoundPlayer() {
        if (firstPlayer.notHisTurn()) return secondPlayer;
        else return firstPlayer;
    }

    public static class Player {
        public User user;
        public Inventory inventory;
        private boolean canUseCard = false;
        public boolean isBad = false;
        public List<Card.Type> disabled = new ArrayList<>();

        public void set(User newUser, Inventory newInventory, Players players) {
            user = newUser;
            inventory = newInventory;
            inventory.giveCards(this, players);
        }


        public Card randomCard(Players players) {
            return Inventory.ALL_CARDS[new Random().nextInt(Inventory.ALL_CARDS.length)].Return(this, players);
        }

        public boolean isUser(User otherUser) {
            return user != null && user.equals(otherUser);
        }

        public boolean isHisTurn(boolean isHisTurn) {
            canUseCard = isHisTurn;
            return canUseCard;
        }

        public boolean notHisTurn() {
            return !canUseCard;
        }

        public boolean noCard() {
            return inventory.getCardCount() <= 0;
        }
    }
}
