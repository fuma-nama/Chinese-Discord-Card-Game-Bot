package game.cards.models;

import game.Players;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

public abstract class Card {
    public final Players.Player owner;
    public final Players players;
    public Card(Players.Player Owner, Players Players) {
        owner = Owner;
        players = Players;
    }
    public abstract String name();
    public abstract String URL();
    public abstract String Execute(GenericInteractionCreateEvent event);
    public abstract boolean canUse();
    public Type type() {
        return Type.Normal;
    }
    public enum Type {
        God, Normal, Bad, Unbonkable
    }
}
