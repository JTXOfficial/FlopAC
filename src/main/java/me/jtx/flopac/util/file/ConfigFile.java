package me.jtx.flopac.util.file;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ConfigFile {

    private ConfigFile() {}

    static ConfigFile instance = new ConfigFile();

    public static ConfigFile getInstance() {
        return instance;
    }

    private FileConfiguration config;
    private FileConfiguration data;
    private File dfile;

    public void setup(Plugin p) {
        config = p.getConfig();
        if (!p.getDataFolder().exists()) {
            p.getDataFolder().mkdir();
        }
        dfile = new File("plugins/FlopAC/config.yml");

        if (!dfile.exists()) {
            try {
                dfile.createNewFile();
            } catch (IOException ignored) {
            }
        }

        data = YamlConfiguration.loadConfiguration(dfile);

    }

    public FileConfiguration getData() {
        return data;
    }


    public void writeDefaults() {
        data.options().header("%player% = the player cheating. %PREFIX% = the prefix of the anticheat you set below. %DEBUG% - dev/check info");

        if (!data.contains("Prefix")) data.set("Prefix", "&7[&6FlopAC&7]&r");
        if (!data.contains("ConsoleAlerts")) data.set("ConsoleAlerts", true);
        if (!data.contains("AlertsMessage")) data.set("AlertsMessage",
                "%PREFIX% &f%player% &7failed &6%CHECK% &6(%CHECKTYPE%) &7[&6x%VL%&7]");
        if (!data.contains("AlertsHoverableMessage")) data.set("AlertsHoverableMessage",
                "&f%info%\n" + "(Ping: %ping% TPS: %tps%) &a(Click to teleport)");
        if (!data.contains("AlertsClickCommand.command")) data.set("AlertsClickCommand.command",
                "/tp %player%");
        if (!data.contains("discord.enable")) data.set("discord.enable", false);
        if (!data.contains("discord.alert-webhook-url")) data.set("discord.alert-webhook-url",
                "https://discord.com/api/webhooks/");
        if (!data.contains("discord.alert-message")) data.set("discord.alert-message",
                "[FlopAC] %player% failed %CHECK% (%CHECKTYPE%) [x%VL%]");
        if (!data.contains("Punishment.LagBack")) data.set("Punishment.LagBack", false);
        if (!data.contains("Punishment.autoban")) data.set("Punishment.autoban", true);
        if (!data.contains("Punishment.execute")) data.set("Punishment.execute",
                "/ban %player% %PREFIX% &6Unfair Advantage.");
        if (!data.contains("Punishment.broadcast")) data.set("Punishment.broadcast", true);
        if (!data.contains("Punishment.message")) data.set("Punishment.message",
                "&7&m--------------------------------------------------\n" + "&c&lK Cheat Detection\n" + "&fK &7has removed &f%player% &7for using &6Unfair Advantages.\n" + "&7&m--------------------------------------------------");

        if (!data.contains("bypass.permission")) data.set("bypass.permission", false);
        if (!data.contains("Logs.Enabled")) data.set("Logs.Enabled", false);
        if (!data.contains("Logs.MongoDBURI")) data.set("Logs.MongoDBURI",
                "mongodb+srv://user:password@node.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");

        saveData();
    }

    public void saveData() {
        try {
            data.save(dfile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadData() {
        this.data = YamlConfiguration.loadConfiguration(dfile);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
