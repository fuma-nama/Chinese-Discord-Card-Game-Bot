package listener;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;

public class ButtonListener extends ListenerAdapter {
    private static final HashMap<String, HashMap<String, onClick>> listenersMap = new HashMap<>();

    public void onButtonClick(ButtonClickEvent event) {
        HashMap<String, onClick> listeners = listenersMap.getOrDefault(event.getTextChannel().getId(), null);
        onClick listener = listeners == null? null : listeners.getOrDefault(event.getComponentId(), null);

        if (listener != null) {
            listener.get(event);
        } else event.reply("here's no listener").setEphemeral(true).queue();
    }

    public static void addListener(TextChannel channel, String ID, onClick onClick) {
        listenersMap.compute(channel.getId(), (k, v)-> {
            if (v == null) v = new HashMap<>();
            v.put(ID, onClick);
            return v;
        });
    }

    public static void removeEntity(TextChannel channel) {
        listenersMap.remove(channel.getId());
    }

    public interface onClick {
        void get(ButtonClickEvent event);
    }
}
