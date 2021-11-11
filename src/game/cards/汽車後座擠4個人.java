package game.cards;

import game.Players;
import game.cards.models.Card;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

public class 汽車後座擠4個人 extends Card {

    public 汽車後座擠4個人(Players.Player Owner, Players Players) {
        super(Owner, Players);
    }

    @Override
    public String name() {
        return "汽車後座擠4個人";
    }

    @Override
    public String URL() {
        return "https://media.discordapp.net/attachments/907241851835281488/907604966645063700/93ce3cf76dd3dd73.png";
    }

    @Override
    public String Execute(GenericInteractionCreateEvent event) {
        Players.Player other = players.getOther(owner.user);
        other.disabled.add(Type.Bad);
        return other.user.getAsMention() + "放棄色色了，下回合無法色色";
    }

    public boolean canUse() {
        return !owner.disabled.contains(type());
    }
}
