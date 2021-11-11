package commands;

import data.CommonData;
import game.Require;
import game.Round;
import listener.ButtonListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;

public class CommandEvents {
    private static final String rules = """
            遊戲需要兩個人才能玩
            使用 /join 加入遊戲
            玩家人數足夠之後即可使用 /start 開始遊戲
            你可以使用扔卡隨機扔掉一張牌，並有幾率獲得一張新的卡
            當所有玩家的牌都用完後，色色的玩家勝出，如果所有玩家都色色則平手
            其他指令:
            """;

    public static void onJoin(SlashCommandEvent event) {
        CommonData.getOrAddData(event.getTextChannel(), (game)-> game.join(event), ()->new Round(event));
    }

    public static void onStartGame(SlashCommandEvent event) {
        getGame(event, (game)-> game.startGame(event));
    }

    public static void onDoAction(SlashCommandEvent event) {
        getGame(event, (game)-> game.DoAction(event));
    }

    public static void onDoActionMenu(SlashCommandEvent event) {
        getGame(event, (game)-> game.DoActionMenu(event));
    }

    public static void onPeekCard(SlashCommandEvent event) {
        getGame(event, (game)-> game.peekCard(event));
    }

    public static void onDropCard(SlashCommandEvent event) {
        getGame(event, (game)-> game.dropCard(event));
    }

    public static void onGetStatus(SlashCommandEvent event) {
        getGame(event, (game)-> game.getStatus(event));
    }

    public static void onGetRules(SlashCommandEvent event) {
        event.replyEmbeds(new EmbedBuilder().setTitle("規則")
                .setDescription(rules)
                        .addField("/use", "出牌", false)
                        .addField("/peek", "查看你的所有卡牌", false)
                        .addField("/status", "查看所有玩家的狀態", false)
                        .addField("/drop", "扔卡", false)
                        .addField("/endgame", "強制結束遊戲 (僅適用於已加入的玩家)", false)
                        .setColor(Color.cyan)
                        .build())
                .setEphemeral(true).queue();
    }

    public static void onForceEnd(SlashCommandEvent event) {
        getGame(event, (game)-> {
            if (!game.requires.notApply(event, Require.JOINED)) {
                CommonData.removeEntity(event.getTextChannel());
                ButtonListener.removeEntity(event.getTextChannel());
                event.replyEmbeds(new EmbedBuilder().setTitle("已強制結束遊戲").setColor(Color.RED).build()).queue();
            }
        });
    }

    public static void getGame(SlashCommandEvent event, Result result) {
        Round game = CommonData.getData(event.getTextChannel());
        if (game == null) {
            event.replyEmbeds(
                    new EmbedBuilder()
                            .setTitle("還沒有任何遊戲")
                            .setColor(Color.RED)
                            .build()
            ).setEphemeral(true).queue();
        } else result.onGet(game);
    }

    public interface Result {
        void onGet(Round game);
    }
}
