package game.cards;

import game.Players;
import game.cards.models.Card;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

public class 熱狗 extends Card {
    public 熱狗(Players.Player Owner, Players Players) {
        super(Owner, Players);
    }

    @Override
    public String name() {
        return "熱狗";
    }

    @Override
    public String URL() {
        return "https://i.ibb.co/yVB2qSx/image.jpg";
    }

    @Override
    public String Execute(GenericInteractionCreateEvent event) {
        return "熱狗從戰鬥逃跑了...";
    }

    @Override
    public boolean canUse() {
        return !owner.disabled.contains(type());
    }

    @Override
    public Type type() {
        return Type.Unbonkable;
    }
}
