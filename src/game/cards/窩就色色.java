package game.cards;

import game.Players;
import game.cards.models.BadCard;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

public class 窩就色色 extends BadCard {

    public 窩就色色(Players.Player Owner, Players Players) {
        super(Owner, Players);
    }

    @Override
    public String name() {
        return "窩就色色";
    }

    @Override
    public String URL() {
        return "https://i.ibb.co/JnFZVX4/3.jpg";
    }

    @Override
    public String Execute(GenericInteractionCreateEvent event) {
        owner.isBad = true;
        return owner.user.getAsMention() + " 開始色色了";
    }

    @Override
    public boolean canUse() {
        return true;
    }
}
