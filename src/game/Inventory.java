package game;

import game.cards.*;
import game.cards.models.Card;

import java.security.SecureRandom;
import java.util.Random;

public class Inventory {
    public static final int MAX_ITEM_COUNT = 7;

    public final Card[] cards = new Card[MAX_ITEM_COUNT];

    public static final CardReturner[] ALL_CARDS = {
            還是不能色色::new,
            不可以色色::new,
            不可以色色::new,
            強制bonk::new,
            可以色色::new,
            絕對可以色色::new,
            我的很大你忍一下::new,
            熱狗::new,
            汽車後座擠4個人::new,
            不可以色色2::new,
            新版不可以色色::new,
            //窩就色色 is same as 絕對可以色色, it will be removed soon
    };

    public void giveCards(Players.Player owner, Players players) {
        SecureRandom generator = new SecureRandom();
        for (int i = 0;i < cards.length;i++) {
            cards[i] = ALL_CARDS[generator.nextInt(ALL_CARDS.length)].Return(owner, players);
        }
    }

    public int getCardCount() {
        int count = 0;
        for (Card card : cards) {
            if (card != null) count++;
        }
        return count;
    }

    public int randomCard() {
        int count = getCardCount(), target;
        target = new Random().nextInt(count);
        int i = 0;
        for (int j = 0;j < cards.length;j++) {
            if (cards[j] != null && (i++ == target || count == 1)) {
                return j;
            }
        }
        return -1;
    }

    public interface CardReturner {
        Card Return(Players.Player owner, Players players);
    }
}
