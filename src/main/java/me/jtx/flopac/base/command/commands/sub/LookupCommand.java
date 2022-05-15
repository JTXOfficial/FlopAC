package me.jtx.flopac.base.command.commands.sub;

import me.jtx.flopac.FlopAC;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.util.ui.UiUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class LookupCommand {

    public void execute(String[] args, String s, CommandSender commandSender) {
        User user = FlopAC.getInstance().getUserManager().getUser(((Player) commandSender));

        if (user != null) {
            if (args.length >= 2) {
                String targetName = args[1];

                if (targetName.length() > 0) {
                    User target = FlopAC.getInstance().getUserManager().getUser(Bukkit.getPlayer(args[1]));
                    if (target != null) {

                        Inventory inventory = Bukkit.getServer().createInventory(null, 27,
                                "Player: " +target.getPlayer().getName());

                        inventory.setItem(12, UiUtil.generateItem(target.getPlayerHead(),
                                ChatColor.GREEN + target.getPlayer().getDisplayName(), Arrays.asList(
                                        "",
                                        ChatColor.GREEN + "Ping: " + ChatColor.RESET + target.getConnectionProcessor().getPing(),
                                        ChatColor.GREEN + "Average ping: " + ChatColor.RESET + target.getConnectionProcessor().getAverageTransactionPing(),
                                        "",
                                        ChatColor.GREEN + "Bypass: " + ChatColor.RESET + (allowedToBypass(target) ? "true" : "false"),
                                        "",
                                        ChatColor.GREEN + "Client: " + ChatColor.RESET + target.getMovementProcessor().getClientBrand(),
                                        ChatColor.GREEN + "Version: " + ChatColor.RESET + "1.8")));

                        inventory.setItem(13, UiUtil.generateItem(new ItemStack(Material.BOOK, 1),
                                ChatColor.GREEN + "Info", Arrays.asList(
                                        "",
                                        ChatColor.GREEN + "Game session: " + ChatColor.RESET + target.getPlayer().getPlayerTime() / (20 * 60),
                                        ChatColor.GREEN + "Lagging: " + ChatColor.RESET + target.getConnectionProcessor().isLagging(),
                                        ChatColor.GREEN + "In Combat: " + ChatColor.RESET + target.getCombatProcessor().getUseEntityTimer().hasNotPassed(40)
                                        )));


                        for (int slots = 0; slots < 27; slots++) {
                            if (inventory.getItem(slots) == null) inventory.setItem(slots,
                                    UiUtil.createSpacer((byte) 14));
                        }

                        user.getPlayer().openInventory(inventory);
                    }
                }
            }
        }
    }

    public boolean allowedToBypass(User user) {
        if (FlopAC.getInstance().getConfigValues().isAllowBypass() == true && user.getPlayer().hasPermission("anticheat.bypass")) {
            return true;
        }
        return false;
    }
}