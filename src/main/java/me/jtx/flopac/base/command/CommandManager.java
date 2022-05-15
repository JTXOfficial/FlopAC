package me.jtx.flopac.base.command;


import lombok.Getter;
import me.jtx.flopac.base.command.commands.MainCommand;
import me.jtx.flopac.util.command.CommandUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CommandManager {
    private final List<Command> commandList = new ArrayList<>();

    public CommandManager() {
        addCommand(new Command(new MainCommand("flopac"), "flopac", null, "Main command.",
                true));

        addCommand(new Command(new MainCommand("flopac"), "flopac alerts", "/flopac alerts",
                "Toggle on, and off alerts.", true));

        addCommand(new Command(new MainCommand("flopac"), "flopac check", "/flopac check [check&type]",
                "Toggle on, and off detections.", true));

        addCommand(new Command(new MainCommand("flopac"), "flopac forceban", "/flopac forceban [player]",
                "Forceban a player with the anticheat.", true));

        addCommand(new Command(new MainCommand("flopac"), "flopac ping", "/flopac ping [player]",
                "Gets the ping of the target player.", true));

        addCommand(new Command(new MainCommand("flopac"), "flopac gui", "/flopac gui",
                "GUI for the anticheat.", true));

        addCommand(new Command(new MainCommand("flopac"), "flopac logs", "/flopac logs (player)",
                "Shows recent flags of the desired player that are stored", true));

        addCommand(new Command(new MainCommand("flopac"), "flopac lookup", "/flopac lookup (player)",
                "Opens a gui of information about the player", true));

        addCommand(new Command(new MainCommand("flopac"), "flopac reload", "/flopac reload",
                "Reloads the anticheat", true));
    }

    private void addCommand(Command... commands) {
        for (Command command : commands) {
            commandList.add(command);
            if (command.isEnabled()) CommandUtils.registerCommand(command);
        }
    }

    public void removeCommand() {
        commandList.forEach(CommandUtils::unRegisterBukkitCommand);
    }
}

