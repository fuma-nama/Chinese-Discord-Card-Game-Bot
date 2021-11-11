package game.cards;

import game.Players;
import game.cards.models.Card;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

public class 強制bonk extends Card {

    public 強制bonk(Players.Player Owner, Players Players) {
        super(Owner, Players);
    }

    @Override
    public String name() {
        return "強制bonk";
    }

    @Override
    public String URL() {
        return "https://i.ibb.co/PCBfZDh/5.jpg";
    }

    @Override
    public String Execute(GenericInteractionCreateEvent event) {
        Players.Player other = players.getOther(owner.user);
        return bonk(other);
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
        return !owner.disabled.contains(type());
    }
}
