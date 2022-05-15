package me.jtx.flopac.base.command.commands.sub;


import me.jtx.flopac.FlopAC;
import me.jtx.flopac.base.user.User;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class AlertsCommand {

    public void execute(String[] args, String s, CommandSender commandSender) {
        User user = FlopAC.getInstance().getUserManager().getUser(((Player) commandSender));
        if (user != null) {
            if (user.isAlerts()) {
                user.setAlerts(false);
                commandSender.sendMessage(ChatColor.RED + "Alerts have been toggled off!");
            } else {
                user.setAlerts(true);
                commandSender.sendMessage(ChatColor.GREEN + "Alerts have been toggled on!");
            }
        }
    }
}
