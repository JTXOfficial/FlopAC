package me.jtx.flopac.base.command.commands.sub;

import me.jtx.flopac.FlopAC;
import me.jtx.flopac.base.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand {

    private final String line = ChatColor.GRAY + "§m------------------------------------------";

    public void execute(String[] args, String s, CommandSender commandSender) {
        User user = FlopAC.getInstance().getUserManager().getUser(((Player) commandSender));

        try {
            if (user != null) {
                if (args.length >= 2) {
                    String targetName = args[1];

                    if (targetName.length() > 0) {
                        User target = FlopAC.getInstance().getUserManager().getUser(Bukkit.getPlayer(args[1]));
                        if (target != null) {

                            int transPing = target.getConnectionProcessor().getTransPing();
                            int avgPing = target.getConnectionProcessor().getAverageTransactionPing();
                            int keepPing = target.getConnectionProcessor().getPing();

                            commandSender.sendMessage("\n" + line);
                            commandSender.sendMessage("Player: " + ChatColor.RED + target.getPlayer().getName());
                            commandSender.sendMessage("\n");
                            commandSender.sendMessage("Transaction Ping: " + ChatColor.GREEN + transPing);
                            commandSender.sendMessage("KeepAlive Ping: " + ChatColor.GREEN + keepPing);
                            commandSender.sendMessage("Average Ping: " + ChatColor.GREEN + avgPing);
                            commandSender.sendMessage(line + "\n");
                        } else {
                            commandSender.sendMessage("[ERROR] Player your trying to ban is NULL, try another name.");
                        }
                    } else {
                        commandSender.sendMessage("Please enter a valid username.");
                    }
                } else {
                    commandSender.sendMessage("Usage: /ac ping (player)");
                }
            } else {
                commandSender.sendMessage("How tf are u running this command?");
            }
        } catch (NullPointerException nullP) {

        }
    }
}