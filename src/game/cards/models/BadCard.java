package game.cards.models;

import game.Players;

public abstract class BadCard extends Card {
    public BadCard(Players.Player Owner, Players Players) {
        super(Owner, Players);
    }

    public boolean canUse() {
        return !owner.disabled.contains(type());
    }

    public Card.Type type() {
        return Card.Type.Bad;
    }
}
