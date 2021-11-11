package listener;

import commands.CommandsBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Collection;
import java.util.HashMap;

public class CommandListener extends ListenerAdapter {
    private final HashMap<String, CommandsBuilder.Command> commandsMap = new HashMap<>();

    public CommandListener(Collection<CommandsBuilder.Command> commands) {
        for (CommandsBuilder.Command cmd : commands)
        commandsMap.put(cmd.name(), cmd);
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        CommandsBuilder.Command command = commandsMap.getOrDefault(event.getName(), null);
        if (command == null) return;

        CommandsBuilder.Command subCommand = getSubCommandGroup(event, command);
        subCommand = getSubCommand(event, subCommand == null? command : subCommand);
        if (subCommand != null) {
            command = subCommand;
        }

        CommandsBuilder.Option option = getCommandOption(event, command);

        if (option != null) {
            option.event().onGet(event);
        } else command.event().onGet(event);
    }

    private static CommandsBuilder.Option getCommandOption(SlashCommandEvent event, CommandsBuilder.Command command) {
        if (!event.getOptions().isEmpty()) {
            for (CommandsBuilder.Option option : command.options()) {
                if ((option.event() != null) && event.getOption(option.name()) != null) {
                    return option;
                }
            }
        }
        return null;
    }

    private static CommandsBuilder.Command getSubCommandGroup(SlashCommandEvent event, CommandsBuilder.Command command) {
        if (event.getSubcommandGroup() != null) {
            for (CommandsBuilder.Command subCommand : command.subCommands()) {
                if (subCommand.isGroup() && subCommand.name().equals(event.getSubcommandGroup())) {
                    return subCommand;
                }
            }
        }
        return null;
    }

    private static CommandsBuilder.Command getSubCommand(SlashCommandEvent event, CommandsBuilder.Command command) {
        if (event.getSubcommandName() != null) {
            for (CommandsBuilder.Command subCommand : command.subCommands()) {
                if (!subCommand.isGroup() && subCommand.name().equals(event.getSubcommandName())) {
                    return subCommand;
                }
            }
        }
        return null;
    }
}
