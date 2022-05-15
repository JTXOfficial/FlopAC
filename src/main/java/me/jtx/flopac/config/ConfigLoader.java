package me.jtx.flopac.config;

import me.jtx.flopac.FlopAC;
import me.jtx.flopac.util.file.ConfigFile;

public class ConfigLoader {

    public void  load() {
        ConfigFile.getInstance().setup(FlopAC.getInstance());
        ConfigFile.getInstance().writeDefaults();

        FlopAC.getInstance().getConfigValues().setPrefix(this.convertColor(ConfigFile.getInstance().getData()
                .getString("Prefix")));
        FlopAC.getInstance().getConfigValues().setConsoleAlerts(ConfigFile.getInstance().getData()
                .getBoolean("ConsoleAlerts"));
        FlopAC.getInstance().getConfigValues().setAlertsMessage(this.convertColor(ConfigFile.getInstance().getData()
                .getString("AlertsMessage")));
        FlopAC.getInstance().getConfigValues().setAlertsHoverMessage(this.convertColor(ConfigFile.getInstance().getData()
                .getString("AlertsHoverableMessage")));
        FlopAC.getInstance().getConfigValues().setAlertHoverCommand(ConfigFile.getInstance().getData()
                .getString("AlertsClickCommand.command"));
                FlopAC.getInstance().getConfigValues().setDiscordWebhook(ConfigFile.getInstance().getData()
                .getBoolean("discord.enable"));
        FlopAC.getInstance().getConfigValues().setDiscordWebURL(ConfigFile.getInstance().getData()
                .getString("discord.alert-webhook-url"));
        FlopAC.getInstance().getConfigValues().setDiscordAlerts(ConfigFile.getInstance().getData()
                .getString("discord.alert-message"));
        FlopAC.getInstance().getConfigValues().setLagBack(ConfigFile.getInstance().getData()
                .getBoolean("Punishment.LagBack"));
        FlopAC.getInstance().getConfigValues().setPunish(ConfigFile.getInstance().getData()
                .getBoolean("Punishment.autoban"));
        FlopAC.getInstance().getConfigValues().setPunishCommand(this.convertColor(ConfigFile.getInstance().getData()
                .getString("Punishment.execute")));
        FlopAC.getInstance().getConfigValues().setAnnounce(ConfigFile.getInstance().getData()
                .getBoolean("Punishment.broadcast"));
        FlopAC.getInstance().getConfigValues().setAnnounceMessage(this.convertColor(ConfigFile.getInstance().getData()
                .getString("Punishment.message")));

        FlopAC.getInstance().getConfigValues().setAllowBypass(ConfigFile.getInstance().getData()
                .getBoolean("bypass.permission"));

        FlopAC.getInstance().getConfigValues().setLogs(ConfigFile.getInstance().getData()
                .getBoolean("Logs.Enabled"));

        FlopAC.getInstance().getConfigValues().setMongoDBURI(ConfigFile.getInstance().getData()
                .getString("Logs.MongoDBURI"));
    }

    String convertColor(String in) {
        return in.replace("&", "§");
    }


}
