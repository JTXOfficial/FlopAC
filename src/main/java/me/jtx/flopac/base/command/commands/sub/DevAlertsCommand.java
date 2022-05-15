package me.jtx.flopac.base.command.commands.sub;


import me.jtx.flopac.base.user.User;
import me.jtx.flopac.FlopAC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class DevAlertsCommand {

    public void execute(String[] args, String s, CommandSender commandSender) {
        User user = FlopAC.getInstance().getUserManager().getUser(((Player) commandSender));
        if (user != null) {
            if (user.isDevAlerts()) {
                user.setDevAlerts(false);
                commandSender.sendMessage(ChatColor.RED + "[DEV] > " + ChatColor.WHITE + "Alerts have been toggled off!");
            } else {
                user.setDevAlerts(true);
                commandSender.sendMessage(ChatColor.RED + "[DEV] > " + ChatColor.WHITE + "Alerts have been toggled on!");
            }
        }
    }
}
