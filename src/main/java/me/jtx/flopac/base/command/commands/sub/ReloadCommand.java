package me.jtx.flopac.base.command.commands.sub;


import me.jtx.flopac.FlopAC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;


public class ReloadCommand {

    public void execute(String[] args, String s, CommandSender commandSender) {

        FlopAC.getInstance().getUserManager().getUserMap().forEach((uuid, user) -> {

            user.getPlayer().sendMessage("\n" + FlopAC.getInstance().getConfigValues().getPrefix()
                    + ChatColor.GOLD + " Reloading... \n");

            FlopAC.getInstance().getCheckManager().reloadAnticheat();

            user.getPlayer().sendMessage("\n" + FlopAC.getInstance().getConfigValues().getPrefix()
                    + ChatColor.GREEN + " Reloaded! \n");
        });
    }
}
