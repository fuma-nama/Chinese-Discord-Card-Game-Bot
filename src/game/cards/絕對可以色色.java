package game.cards;

import game.Players;
import game.cards.models.BadCard;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

public class 絕對可以色色 extends BadCard {

    public 絕對可以色色(Players.Player Owner, Players Players) {
        super(Owner, Players);
    }

    @Override
    public String name() {
        return "絕對可以色色";
    }

    @Override
    public String URL() {
        return "https://i.ibb.co/2PZkVFH/17.jpg";
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
