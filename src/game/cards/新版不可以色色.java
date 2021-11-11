package game.cards;

import game.Players;
import game.cards.models.Card;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

public class 新版不可以色色 extends 不可以色色 {

    public 新版不可以色色(Players.Player Owner, Players Players) {
        super(Owner, Players);
    }

    @Override
    public String URL() {
        return "https://i.ibb.co/t2PB9S4/19.jpg";
    }
}
