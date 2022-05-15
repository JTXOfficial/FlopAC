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

public class GUICommand {

    public void execute(String[] args, String s, CommandSender commandSender) {
        User user = FlopAC.getInstance().getUserManager().getUser(((Player) commandSender));

        Inventory inventory = Bukkit.getServer().createInventory(null, 27,
               "" + ChatColor.GOLD + ChatColor.BOLD + "FlopAC" + ChatColor.GRAY + " - Menu");

        inventory.setItem(13, UiUtil.generateItem(new ItemStack(Material.BOOK, 1),
                ChatColor.RESET + "Information", Arrays.asList(
                        ChatColor.GRAY + "Version: " + ChatColor.GREEN + FlopAC.getInstance().getCurrentVersion(),
                        ChatColor.GRAY + "Latest Version: " + ChatColor.GREEN + FlopAC.getInstance().getLatestVersion(),
                        "",
                        ChatColor.GRAY + "Total Checks: " + ChatColor.GOLD + user.getCheckManager().getCheckList().size())));

        inventory.setItem(15, UiUtil.generateItem(new ItemStack(Material.BOOK_AND_QUILL, 1),
                ChatColor.GOLD + "Settings", Arrays.asList(
                        ChatColor.GRAY + "Control Settings"
                )));



        for (int slots = 0; slots < 27; slots++) {
            if (inventory.getItem(slots) == null) inventory.setItem(slots,
                    UiUtil.createSpacer((byte) 14));
        }

        user.getPlayer().openInventory(inventory);

    }
    
    public static Inventory settingsGui() {
        Inventory inventory = Bukkit.getServer().createInventory(null, 27,
                "" + ChatColor.GOLD + ChatColor.BOLD + "FlopAC" + ChatColor.GRAY + " - Settings");

        inventory.setItem(12, UiUtil.generateItem(new ItemStack(Material.BOOK, 1),
                        ChatColor.GOLD + "Autoban", Arrays.asList(
                                ChatColor.GRAY + "Enabled: " + (FlopAC.getInstance().getConfigValues().isPunish() ? ChatColor.GREEN + "✔" : ChatColor.RED + "✗")
                )));

        inventory.setItem(13, UiUtil.generateItem(new ItemStack(Material.BOOK, 1),
                ChatColor.GOLD + "Webhook", Arrays.asList(
                        ChatColor.GRAY + "Enabled: " + (FlopAC.getInstance().getConfigValues().isDiscordWebhook() ? ChatColor.GREEN + "✔" : ChatColor.RED + "✗")
                )));

        for (int slots = 0; slots < 27; slots++) {
            if (inventory.getItem(slots) == null) inventory.setItem(slots,
                    UiUtil.createSpacer((byte) 14));
        }
        return inventory;
    }
}