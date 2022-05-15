package me.jtx.flopac.base.command.commands.sub;


import me.jtx.flopac.FlopAC;
import me.jtx.flopac.base.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class BanWaveCommand {

    public void execute(String[] args, String s, CommandSender commandSender) {
        if (args.length >= 2) {
            User user = FlopAC.getInstance().getUserManager().getUser(((Player) commandSender));
            if (user != null) {

                if (args[1].equalsIgnoreCase("enable")) {
                    FlopAC.getInstance().getConfigValues().setJudgementDay(true);
                    commandSender.sendMessage("Enabled Banwave!");
                } else if (args[1].equalsIgnoreCase("disable")) {
                    FlopAC.getInstance().getConfigValues().setJudgementDay(false);
                    commandSender.sendMessage("Disabled Banwave!");
                }

                if (args[1].equalsIgnoreCase("start")
                        && FlopAC.getInstance().getConfigValues().isJudgementDay()) {
                    FlopAC.getInstance().getBanWaveManager().commenceBanWave(commandSender);
                } else if (args[1].equalsIgnoreCase("stop")
                        && FlopAC.getInstance().getConfigValues().isJudgementDay()) {
                    commandSender.sendMessage("Banwave Should Have Stopped!");
                    FlopAC.getInstance().getBanWaveManager().stopBanwave();
                }
            }
        }
    }
}
