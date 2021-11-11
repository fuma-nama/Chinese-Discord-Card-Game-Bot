package game.cards;

import game.Players;
import game.cards.models.Card;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

public class 我的很大你忍一下 extends Card {

    public 我的很大你忍一下(Players.Player Owner, Players Players) {
        super(Owner, Players);
    }

    @Override
    public String name() {
        return "我的很大你忍一下";
    }

    @Override
    public String URL() {
        return "https://i.ibb.co/F5q5bvb/18.jpg";
    }

    @Override
    public String Execute(GenericInteractionCreateEvent event) {
        Players.Player other = players.getOther(event.getUser());
        Card[] cards = other.inventory.cards;
        int removed = 0;
        for (int i = 0;i < cards.length;i++) {
            if (cards[i] != null && cards[i].type() == Type.God) {
                cards[i] = null;
                removed++;
            }
        }
        return other.user.getAsMention() + " 的所有狗勾神族色色了，並因此而自爆\n" +
                "損失了" + removed + "張牌";
    }

    public boolean canUse() {
        return !owner.disabled.contains(type());
    }
}
