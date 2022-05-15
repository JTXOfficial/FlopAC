package me.jtx.flopac.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigValues {
    private boolean discordWebhook, consoleAlerts, lagBack, punish, announce, debugMessage, judgementDay, allowBypass, logs;
    private String discordWebURL, discordAlerts, punishCommand, prefix, alertsMessage, announceMessage, mongoDBURI, alertsHoverMessage, alertHoverCommand;
}
