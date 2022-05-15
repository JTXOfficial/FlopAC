package me.jtx.flopac.base.command.commands;

import me.jtx.flopac.FlopAC;
import me.jtx.flopac.base.command.commands.sub.*;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class MainCommand extends BukkitCommand {
    private final String line = ChatColor.GRAY + "§m------------------------------------------";
    private final AlertsCommand alertsCommand = new AlertsCommand();
    private final ChecksCommand checksCommand = new ChecksCommand();
    private final ForceBanCommand forceBanCommand = new ForceBanCommand();
    private final GUICommand guiCommand = new GUICommand();
    private final PingCommand pingCommand = new PingCommand();
    private final BanWaveCommand banWaveCommand = new BanWaveCommand();
    private final LogsCommand logsCommand = new LogsCommand();
    private final ReloadCommand reloadCommand = new ReloadCommand();
    private final LookupCommand lookupCommand = new LookupCommand();
    private final DevAlertsCommand devAlertsCommand = new DevAlertsCommand();


    public MainCommand(String name) {
        super(name);
        this.description = "FlopAC command.";
        this.usageMessage = "/" + name;
        this.setAliases(new ArrayList<>());
    }

    @Override
    public boolean execute(CommandSender commandSender, String commandLabel, String[] args) {
        if (commandLabel.equalsIgnoreCase("flopac")
                || commandLabel.equalsIgnoreCase("flop")) {
            if (commandSender.hasPermission("flopac.command")) {
                if (args.length < 1) {
                    commandSender.sendMessage(ChatColor.GOLD + "FlopAC" + ChatColor.GRAY + " - "
                            + ChatColor.GOLD + FlopAC.getInstance().getDescription().getVersion());
                    commandSender.sendMessage(line);

                    Player player = (Player) commandSender;

                    FlopAC.getInstance().getCommandManager().getCommandList().forEach(command -> {
                        TextComponent textComponent = new TextComponent(ChatColor.GRAY + "» " + ChatColor.WHITE
                                + "/" + command.getCommand() + ChatColor.GRAY + " - " + ChatColor.GOLD
                                + command.getDescription());
                        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder((command.getUsage() != null ? ChatColor.GOLD + command.getUsage()
                                        : ChatColor.WHITE + "No usage found.")).create()));
                        player.spigot().sendMessage(textComponent);
                    });

                    commandSender.sendMessage(line);
                } else {
                    String s = args[0];
                    boolean found = false;

                    if (s.equalsIgnoreCase("alerts")) {
                        found = true;
                        alertsCommand.execute(args, s, commandSender);
                    } else if (s.equalsIgnoreCase("devalerts")) {
                        found = true;
                        devAlertsCommand.execute(args, s, commandSender);
                    } else if (s.equalsIgnoreCase("check")) {
                        found = true;
                        checksCommand.execute(args, s, commandSender);
                    } else if (s.equalsIgnoreCase("forceban")) {
                        found = true;
                        forceBanCommand.execute(args, s, commandSender);
                    } else if (s.equalsIgnoreCase("banwave")) {
                      //  found = true;
                      //  banWaveCommand.execute(args, s, commandSender);
                    } else if (s.equalsIgnoreCase("gui")) {
                        found = true;
                        guiCommand.execute(args, s, commandSender);
                    } else if (s.equalsIgnoreCase("ping")) {
                        found = true;
                        pingCommand.execute(args, s, commandSender);
                    } else if (s.equalsIgnoreCase("logs")) {
                        found = true;
                        logsCommand.execute(args, s, commandSender);
                    } else if (s.equalsIgnoreCase("reload")) {
                        found = true;
                        reloadCommand.execute(args, s, commandSender);
                    } else if (s.equalsIgnoreCase("lookup")) {
                        found = true;
                        lookupCommand.execute(args, s, commandSender);
                    }

                    if (!found) commandSender.sendMessage(ChatColor.GOLD + "Sub command doesn't exist!");
                }
            } else {
                commandSender.sendMessage(ChatColor.RED + "You don't have permission to execute this command.");
            }
        }
        return false;
    }
}
