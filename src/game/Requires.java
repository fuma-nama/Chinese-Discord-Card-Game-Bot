package game;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

import java.awt.*;

public record Requires(Round parent) {
    public boolean notApply(GenericInteractionCreateEvent event, Require... requires) {
        for (Require require : requires) {
            boolean apply = switch (require) {
                case JOINED -> {
                    boolean joined = parent.players.hasPlayer(event.getUser());
                    if (!joined) replyError(event, "你還沒有加入遊戲");
                    yield joined;
                }
                case NON_JOINED -> {
                    boolean notJoined = !parent.players.hasPlayer(event.getUser());
                    if (!notJoined) replyError(event, "你已經加入了遊戲");
                    yield notJoined;
                }
                case STARTED -> {
                    if (!parent.started) replyError(event, "遊戲還沒有開始");
                    yield parent.started;
                }
                case NON_STARTED -> {
                    if (parent.started) replyError(event, "已經開始遊戲了");
                    yield (!parent.started);
                }
                case FULLED -> {
                    boolean fulled = parent.players.isFulled();
                    if (!fulled) replyError(event, "玩家人數未滿");
                    yield fulled;
                }
                case NON_FULLED -> {
                    boolean fulled = parent.players.isFulled();
                    if (fulled) replyError(event, "玩家人數已滿");
                    yield (!fulled);
                }
                case HIS_TURN -> {
                    boolean hisTurn = !parent.players.getPlayer(event.getUser()).notHisTurn();
                    if (!hisTurn) replyError(event, "還沒輪到你");
                    yield hisTurn;
                }
            };
            if (!apply) return true;
        }
        return false;
    }
    private static void replyError(GenericInteractionCreateEvent event, String message) {
        MessageEmbed EmbedMessage = new EmbedBuilder().setTitle(message).setColor(Color.RED).build();
        event.replyEmbeds(EmbedMessage).setEphemeral(true).queue();
    }
}
