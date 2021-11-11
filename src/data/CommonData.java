package data;

import game.Round;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class CommonData {
    /**HashMap has a better performance than TreeMap when there's no many users' data stored in HashMap**/
    private static final HashMap<TextChannel, Round> commonData = new HashMap<>();

    public static void getOrAddData(TextChannel entity, Result onGet, Failed onFailed) {
        Round data = commonData.getOrDefault(entity, null);
        if (data == null) commonData.put(entity, onFailed.create());
        else {
            onGet.onGet(data);
        }
    }

    @Nullable
    public static Round getData(TextChannel entity) {
        return commonData.getOrDefault(entity, null);
    }

    public static void removeEntity(TextChannel entity) {
        commonData.remove(entity);
    }

    public interface Result {
        void onGet(Round obj);
    }

    public interface Failed {
        Round create();
    }
}
