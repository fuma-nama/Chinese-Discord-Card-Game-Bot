package game.cards;

import game.Players;
import game.cards.models.BadCard;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

public class 可以色色 extends BadCard {

    public 可以色色(Players.Player Owner, Players Players) {
        super(Owner, Players);
    }

    @Override
    public String name() {
        return "可以色色";
    }

    @Override
    public String URL() {
        return "https://i.ibb.co/0FDvVmp/14.jpg";
    }

    @Override
    public String Execute(GenericInteractionCreateEvent event) {
        owner.isBad = true;
        return owner.user.getAsMention() + " 開始色色了";
    }
}
