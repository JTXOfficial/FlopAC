package me.jtx.flopac.base.command.commands.sub;

import me.jtx.flopac.FlopAC;
import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForceBanCommand {

    public void execute(String[] args, String s, CommandSender commandSender) {
        User user = FlopAC.getInstance().getUserManager().getUser(((Player) commandSender));

        try {
            if (user != null) {
                if (args.length >= 2) {
                    String targetName = args[1];

                    if (targetName.length() > 0) {
                        User target = FlopAC.getInstance().getUserManager().getUser(Bukkit.getPlayer(args[1]));
                        if (target != null) {
                            Check check = new Check();
                            check.punishPlayer(target);
                        } else {
                            commandSender.sendMessage("[ERROR] Player your trying to ban is [NULL], try another name.");
                        }
                    } else {
                        commandSender.sendMessage("Please enter a valid username.");
                    }
                } else {
                    commandSender.sendMessage("Usage: /ac forceban (player)");
                }
            } else {
                commandSender.sendMessage("How tf are u running this command?");
            }
        } catch (NullPointerException nullP) {

        }
    }
}
