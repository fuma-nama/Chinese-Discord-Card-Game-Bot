package game.cards;

import game.Players;
import game.cards.models.Card;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

public class 不可以色色 extends Card {

    public 不可以色色(Players.Player Owner, Players Players) {
        super(Owner, Players);
    }

    @Override
    public String name() {
        return "不可以色色";
    }

    @Override
    public String URL() {
        return "https://i.ibb.co/48DhnWh/2.jpg";
    }

    @Override
    public String Execute(GenericInteractionCreateEvent event) {
        Players.Player other = players.getOther(owner.user);
        other.isBad = false;
        return other.user.getAsMention() + "不能色色了\n";
    }

    public boolean canUse() {
        return players.getOther(owner.user).isBad && !owner.disabled.contains(type());
    }
}
