package commands;

import listener.CommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CommandsBuilder {
    private final Stack<Command> commands = new Stack<>();
    private String[] servers;
    private boolean guildCommand = true;

    public Command cmd(String name, String description, @NotNull CommandEvent event) {
        return commands.push(Command.create(name, description, event));
    }

    public Command cmd(String name, String description) {
        return cmd(name, description, (e)-> e.reply("no listener.").setEphemeral(true).queue());
    }

    public void servers(String... serverIDs) {
        servers = serverIDs;
    }

    public void guildCommand(boolean onlyGuild) {
        guildCommand = onlyGuild;
    }

    public void build(JDA jda) {
        if (guildCommand) {
            for (String ID : servers) {
                System.out.println(SetUpServer(jda.getGuildById(ID))? ID + " Added command." : ID + "Failed to add command");
            }
        } else {
            for (Command cmd : commands) {
                CommandCreateAction action = jda.upsertCommand(cmd.name, cmd.description);
                buildCommand(action, cmd);
                action.queue();
            }
        }
        jda.addEventListener(new CommandListener(commands));
    }

    private boolean SetUpServer(Guild server) {
        if (server != null) {
            for (Command cmd : commands) {
                CommandCreateAction action = server.upsertCommand(cmd.name, cmd.description);
                buildCommand(action, cmd);
                action.queue();
            }
        }
        return server != null;
    }

    private static void buildCommand(CommandCreateAction action, Command cmd) {
        for (Command subCmd : cmd.subCommands) {
            if (subCmd.isGroup) action = action.addSubcommandGroups(buildSubCommandGroups(new SubcommandGroupData(subCmd.name, subCmd.description), subCmd));
            else action = action.addSubcommands(buildSubCommand(new SubcommandData(subCmd.name, subCmd.description), subCmd));
        }

        for (Option options : cmd.options) {
            action = action.addOption(options.type, options.name, options.description);
        }
        action.queue();
    }

    private static SubcommandData buildSubCommand(SubcommandData cmdData, Command cmd) {
        for (Option options : cmd.options) {
            cmdData = cmdData.addOption(options.type, options.name, options.description);
        }
        return cmdData;
    }

    private static SubcommandGroupData buildSubCommandGroups(SubcommandGroupData cmdData, Command cmd) {
        for (Command subCommand : cmd.subCommands) {
            cmdData = cmdData.addSubcommands(buildSubCommand(new SubcommandData(subCommand.name, subCommand.description), subCommand));
        }
        return cmdData;
    }

    public static record Command(String name, String description, List<Option> options, List<Command> subCommands, boolean isGroup, CommandEvent event) {
        public static Command create(String Name, String Description, CommandEvent Event) {
            return new Command(Name, Description, new ArrayList<>(), new ArrayList<>(), false, Event);
        }
        public static Command createGroup(String Name, String Description, CommandEvent Event) {
            return new Command(Name, Description, new ArrayList<>(), new ArrayList<>(), true, Event);
        }
        public Command op(OptionType type, String name, String description) {
            return op(type, name, description, null);
        }

        public Command op(OptionType type, String name, String description, CommandEvent event) {
            options.add(new Option(type, name, description, event));
            return this;
        }

        public SubCommand subCmd(String name, String description) {
            return subCmd(name, description, null);
        }

        public SubCommand subCmd(String name, String description, CommandEvent event) {
            Command cmd = Command.create(name, description, event);
            subCommands.add(cmd);
            return new SubCommand(cmd);
        }

        public SubCommandGroup subCmdGroup(String name, String description) {
            return subCmdGroup(name, description, null);
        }

        public SubCommandGroup subCmdGroup(String name, String description, CommandEvent event) {
            Command cmd = Command.createGroup(name, description, event);
            subCommands.add(cmd);
            return new SubCommandGroup(cmd, this);
        }
    }

    public static record Option(OptionType type, String name, String description, CommandEvent event) {
    }

    public interface CommandEvent {
        void onGet(SlashCommandEvent event);
    }

    public static class SubCommand {
        private final Command command;
        private SubCommandGroup group;
        public SubCommand(Command cmd) {
            command = cmd;
        }
        public SubCommand(Command cmd, SubCommandGroup parent) {
            command = cmd;
            group = parent;
        }
        public SubCommand op(OptionType type, String name, String description) {
            return op(type, name, description, null);
        }

        public SubCommand op(OptionType type, String name, String description, CommandEvent event) {
            command.options.add(new Option(type, name, description, event));
            return this;
        }

        public SubCommand subCmd(String name, String description) {
            return group.subCmd(name, description);
        }

        public SubCommand subCmd(String name, String description, CommandEvent event) {
            return group.subCmd(name, description, event);
        }

        public SubCommandGroup subCmdGroup(String name, String description) {
            return group.subCmdGroup(name, description, null);
        }

        public SubCommandGroup subCmdGroup(String name, String description, CommandEvent event) {
            return group.subCmdGroup(name, description, event);
        }
    }

    public static class SubCommandGroup {
        private final Command command, parent;

        public SubCommandGroup(Command cmd, Command Parent) {
            command = cmd;
            parent = Parent;
        }

        public SubCommand subCmd(String name, String description) {
            return subCmd(name, description, parent.event);
        }

        public SubCommand subCmd(String name, String description, CommandEvent event) {
            Command cmd = Command.create(name, description, event);
            command.subCommands.add(cmd);
            return new SubCommand(cmd, this);
        }

        public SubCommandGroup subCmdGroup(String name, String description) {
            return parent.subCmdGroup(name, description, null);
        }

        public SubCommandGroup subCmdGroup(String name, String description, CommandEvent event) {
            return parent.subCmdGroup(name, description, event);
        }
    }
}
