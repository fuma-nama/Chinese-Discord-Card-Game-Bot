package game.cards;

import game.Players;
import game.cards.models.Card;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

public class 還是不能色色 extends Card {

    public 還是不能色色(Players.Player Owner, Players Players) {
        super(Owner, Players);
    }

    @Override
    public String name() {
        return "還是不能色色";
    }

    @Override
    public String URL() {
        return "https://i.ibb.co/bdyntk4/15.jpg";
    }

    @Override
    public String Execute(GenericInteractionCreateEvent event) {
        Players.Player other = players.getOther(owner.user);
        other.isBad = false;
        return other.user.getAsMention() + "不能色色了\n" + bonk(other);
    }

    private String bonk(Players.Player player) {
        int id = player.inventory.randomCard();
        Card removed;
        if (id != -1) {
            removed = player.inventory.cards[id];

            if (removed.type() != Type.Unbonkable) {
                player.inventory.cards[id] = null;
                return "\"" + removed.name() + "\"被bonk了 (ID: " + id + ")";
            }
            else return owner.user.getAsMention() + "打算bonk\"" + removed.name() + "\", 但他放棄了";
        } else {
            return player.user.getAsMention() + "沒有牌可以bonk了";
        }
    }

    public boolean canUse() {
        return players.getOther(owner.user).isBad && !owner.disabled.contains(type());
    }

    public Type type() {
        return Type.God;
    }
}
