package game;

import data.CommonData;
import game.cards.models.Card;
import listener.ButtonListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;

public class Round {
    public boolean started = false;
    public Players players = Players.empty();
    public final Requires requires = new Requires(this);

    public Round(SlashCommandEvent event) {
        join(event);
    }

    public void join(SlashCommandEvent event) {
        if (requires.notApply(event, Require.NON_STARTED, Require.NON_JOINED, Require.NON_FULLED)) return;
        players.add(event.getUser());
        replySuccess(event, event.getUser().getName() + " 加入了遊戲");
    }

    public void startGame(SlashCommandEvent event) {
        if (requires.notApply(event, Require.NON_STARTED, Require.JOINED, Require.FULLED)) return;
        started = true;
        players.getOther(event.getUser()).isHisTurn(true);
        replySuccess(event, "遊戲開始!", (s)->
                event.getMessageChannel()
                        .sendMessage("由" + players.switchTurn().user.getAsMention() + "先出牌")
                        .queue()
        );
    }

    public void DoAction(SlashCommandEvent event) {
        int id = (int) Objects.requireNonNull(event.getOption("card_id")).getAsLong();
        useCard(id, event);
    }

    public void DoActionMenu(SlashCommandEvent event) {
        if (requires.notApply(event, Require.STARTED, Require.JOINED)) return;
        List<ActionRow> buttons = new ArrayList<>();
        Players.Player player = players.getPlayer(event.getUser());
        Card[] cards = player.inventory.cards;

        // The max button count is 5, including the DropCard button, we can't show more than 4 cards
        int hidden = -4;
        for (int i = 0;i < cards.length;i++) {
            if (cards[i] == null || (hidden++) >= 0) continue;
            String ID = cut(event.getToken() + i);
            buttons.add(ActionRow.of(Button.secondary(ID, cards[i].name() + " (ID: " + i + ")")));
            int j = i;
            ButtonListener.addListener(event.getTextChannel(), ID, (e)-> useCard(j, e));
        }

        String ID = cut(event.getToken() + "DropCard");
        buttons.add(ActionRow.of(Button.danger(ID, "扔卡")));
        ButtonListener.addListener(event.getTextChannel(), ID, this::dropCard);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("所有卡片: ");
        if (hidden > 0) eb.setDescription("隱藏了" + hidden + "張卡片，使用 /peek 查看全部");
        event.replyEmbeds(eb.build()).addActionRows(buttons).setEphemeral(true).queue();
    }

    private static String cut(String s) {
        if (s.length() > 100)
            s = s.substring(s.length() - 100);
        return s;
    }

    private void useCard(int id, GenericInteractionCreateEvent event) {
        if (requires.notApply(event, Require.STARTED, Require.JOINED, Require.HIS_TURN)) return;
        Players.Player player = players.getPlayer(event.getUser());

        Card[] cards = player.inventory.cards;
        if (id < 0 || id >= cards.length || cards[id] == null) {
            event.reply("無效的ID").setEphemeral(true).queue();
            return;
        }
        if (cards[id].canUse()) {
            String effects =  cards[id].Execute(event);

            MessageEmbed message = new EmbedBuilder()
                    .setTitle(event.getUser().getName() + "使用了\"" + cards[id].name() + "\"")
                    .setDescription(effects).setColor(Color.RED)
                    .setImage(cards[id].URL())
                    .build();

            cards[id] = null;
            switchTurn(event, message);
        } else {
            event.reply("現在不能使用!").setEphemeral(true).queue();
        }
    }

    public void dropCard(GenericInteractionCreateEvent event) {
        if (requires.notApply(event, Require.STARTED, Require.JOINED, Require.HIS_TURN)) return;
        Players.Player player = players.getPlayer(event.getUser());
        int id = player.inventory.randomCard();
        if (id == -1) {
            event.reply("Error: NO_CARD_FOUND").setEphemeral(true).queue();
            return;
        }
        boolean newCard = new Random().nextBoolean();
        MessageEmbed message = new EmbedBuilder()
                .setTitle(event.getUser().getName() + "選擇了扔卡")
                .setDescription(event.getUser().getAsMention() + "扔掉了\"" + player.inventory.cards[id].name() + "\" (ID: " + id + ")"
                        + (newCard? "\n並獲得了新卡" : ""))
                .setColor(Color.RED)
                .setImage(player.inventory.cards[id].URL())
                .build();
        player.inventory.cards[id] = newCard? player.randomCard(players) : null;
        switchTurn(event, message);
    }

    public void getStatus(SlashCommandEvent event) {
        if (requires.notApply(event, Require.STARTED)) return;
        event.replyEmbeds(new EmbedBuilder().setTitle("球員狀態")
                .addField(players.firstPlayer().user.getName(), players.firstPlayer().isBad? "可以色色" : "不能色色", true)
                .addField(players.secondPlayer().user.getName(), players.secondPlayer().isBad? "可以色色" : "不能色色", true)
                        .setColor(Color.orange)
                        .build())
                .setEphemeral(true).queue();
    }

    private void switchTurn(GenericInteractionCreateEvent event, MessageEmbed message) {
        if (win(event)) {
            CommonData.removeEntity(event.getTextChannel());
            ButtonListener.removeEntity(event.getTextChannel());
            return;
        }
        players.getCurrentRoundPlayer().disabled.clear();
        Players.Player other = players.switchTurn();
        if (other.inventory.getCardCount() == 0) {
            event.reply(other.user.getAsMention() + " 沒有牌了，繼續由" + players.switchTurn().user.getAsMention() + " 出牌").addEmbeds(message).queue();
        } else {
            event.reply(other.user.getAsMention() + ", 到你了").addEmbeds(message).queue();
        }
    }

    public void peekCard(SlashCommandEvent event) {
        if (requires.notApply(event, Require.STARTED, Require.JOINED)) return;
        List<MessageEmbed> images = new ArrayList<>();
        Card[] cards = players.getInventory(event.getUser()).cards;

        for (int i = 0;i < cards.length;i++) {
            if (cards[i] == null) continue;
            EmbedBuilder eb = new EmbedBuilder();
            eb.setImage(cards[i].URL())
                    .setFooter("ID: " + i);
            images.add(eb.build());
        }
        if (images.isEmpty()) images.add(
                new EmbedBuilder()
                .setTitle("你沒有任何卡")
                .setColor(Color.RED)
                .build()
        );

        event.replyEmbeds(images).setEphemeral(true).queue();
    }

    public boolean win(GenericInteractionCreateEvent event) {
        if (players.firstPlayer().noCard() && players.secondPlayer().noCard()) {
            Players.Player badPlayer;
            if (players.sameBad()) {
                replySuccess(event, "平手!");
            } else if ((badPlayer = players.getBadPlayer()) != null) {
                replySuccess(event, badPlayer.user.getName() + " 勝利了");
            }
            return true;
        }
        return false;
    }

    private static void replySuccess(GenericInteractionCreateEvent event, String s) {
        event.replyEmbeds(new EmbedBuilder().setTitle(s).setColor(Color.GREEN).build()).queue();
    }

    private static void replySuccess(GenericInteractionCreateEvent event, String s, @Nullable Consumer<? super InteractionHook> success) {
        event.replyEmbeds(new EmbedBuilder().setTitle(s).setColor(Color.GREEN).build()).queue(success);
    }
}
