package me.jtx.flopac.base.check.api;

import lombok.Getter;
import lombok.Setter;
import me.jtx.flopac.FlopAC;
import me.jtx.flopac.base.event.CallableEvent;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.database.api.InputData;
import me.jtx.flopac.discord.DiscordWebhook;
import me.jtx.flopac.util.TPSUtil;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

@Getter @Setter
public class Check implements CallableEvent, Cloneable {
    public String checkName, checkType, description;
    public boolean enabled, punished, lagBack, canPunish;
    private int violation, maxViolation;


    public void setup() {
        if (getClass().isAnnotationPresent(CheckInformation.class)) {
            CheckInformation checkInformation = getClass().getAnnotation(CheckInformation.class);
            this.checkName = checkInformation.checkName();
            this.checkType = checkInformation.checkType();
            this.description = checkInformation.description();
        } else {
            FlopAC.getInstance().getLogger().warning("Unable to find CheckInformation annotation" +
                    " in the class: " + getClass().getSimpleName());
        }
    }

    public void devFlag(User user, String... data) {

        StringBuilder stringBuilder = new StringBuilder();

        if (data.length > 0) {
            for (String s : data) {
                stringBuilder.append(s).append(", ");
            }
        }

        String devAlert = FlopAC.getInstance().getConfigValues().getAlertsMessage()
                .replace("%MAX-VL%", String.valueOf(maxViolation))
                .replace("%player%", user.getPlayer().getName())
                .replace("%PREFIX%", ChatColor.RED + "[DEV] >")
                .replace("%CHECK%", checkName)
                .replace("%CHECKTYPE%", checkType)
                .replace("%VL%", String.valueOf(violation))
                .replace("%DEBUG%", (data.length > 0 ? ChatColor.GRAY + " ["
                        + ChatColor.GRAY + stringBuilder.toString().trim() + ChatColor.GRAY + "]" : ""));

        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            User staff = FlopAC.getInstance().getUserManager().getUser(player);
            if (staff.isDevAlerts()) {
                staff.getPlayer().sendMessage(devAlert);
            }
        });
    }
    public void flag(User user, String... data) {
        StringBuilder stringBuilder = new StringBuilder();

        if (data.length > 0) {
            for (String s : data) {
                stringBuilder.append(s).append(", ");
            }
        }

        if (isCanPunish() && !user.isBanned()) {
            ++violation;
        }

        if (TPSUtil.getTPS() <= 19.0
                || FlopAC.getInstance().getConfigValues().isAllowBypass() == true
                && user.getPlayer().hasPermission("flopac.bypass")) {
            return;
        }

        if (FlopAC.getInstance().getConfigValues().isPunish() && !user.isBanned()
                && this.canPunish && this.violation >= this.maxViolation) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), FlopAC.getInstance().getConfigValues()
                            .getPunishCommand()
                            .replace("%MAX-VL%", String.valueOf(maxViolation))
                            .replace("%CHECK%", checkName)
                            .replace("%CHECKTYPE%", checkType)
                            .replace("%VL%", String.valueOf(violation))
                            .replace("%player%", user.getPlayer().getName())
                            .replace("%PREFIX%", FlopAC.getInstance().getConfigValues().getPrefix())
                            .replaceFirst("/", ""));

                    if (FlopAC.getInstance().getConfigValues().isAnnounce()) {
                        Bukkit.broadcastMessage(FlopAC.getInstance().getConfigValues().getAnnounceMessage()
                                .replace("%MAX-VL%", String.valueOf(maxViolation))
                                .replace("%CHECK%", checkName)
                                .replace("%CHECKTYPE%", checkType)
                                .replace("%VL%", String.valueOf(violation))
                                .replace("%player%", user.getPlayer().getName())
                                .replace("%PREFIX%", FlopAC.getInstance().getConfigValues().getPrefix()));
                    }
                }
            }.runTask(FlopAC.getInstance());
            user.setBanned(true);
            this.violation = 0;
        }

        String alert = FlopAC.getInstance().getConfigValues().getAlertsMessage()
                .replace("%MAX-VL%", String.valueOf(maxViolation))
                .replace("%player%", user.getPlayer().getName())
                .replace("%PREFIX%", FlopAC.getInstance().getConfigValues().getPrefix())
                .replace("%CHECK%", checkName)
                .replace("%CHECKTYPE%", checkType)
                .replace("%VL%", String.valueOf(violation))
                .replace("%DEBUG%", (data.length > 0 ? ChatColor.GRAY + " ["
                        + ChatColor.GRAY + stringBuilder.toString().trim() + ChatColor.GRAY + "]" : ""));;

        String discordAlert = FlopAC.getInstance().getConfigValues().getDiscordAlerts()
                .replace("%MAX-VL%", String.valueOf(maxViolation))
                .replace("%player%", user.getPlayer().getName())
                .replace("%PREFIX%", FlopAC.getInstance().getConfigValues().getPrefix())
                .replace("%CHECK%", checkName)
                .replace("%CHECKTYPE%", checkType)
                .replace("%VL%", String.valueOf(violation))
                .replace("%DEBUG%", (data.length > 0 ? " ["
                        + stringBuilder.toString().trim()  + "]" : ""));

        TextComponent textComponent = new TextComponent(alert);
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                FlopAC.getInstance().getConfigValues().getAlertsHoverMessage()
                        .replace("%info%", stringBuilder.toString())
                        .replace("%ping%", user.getConnectionProcessor().getPing() + "")
                        .replace("%tps%", TPSUtil.getTPS() + "")).create()));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                FlopAC.getInstance().getConfigValues().getAlertHoverCommand()
                        .replace("%player%", user.getPlayer().getName())));
        user.getPlayer().spigot().sendMessage(textComponent);


        if (FlopAC.getInstance().getConfigValues().isConsoleAlerts()) {
            FlopAC.getInstance().getServer().getConsoleSender().sendMessage(alert);
        }

        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            User staff = FlopAC.getInstance().getUserManager().getUser(player);

            if (staff != null) {
                if (staff.isAlerts() && (staff.getPlayer().hasPermission("flopac.alerts"))) {
                    staff.getPlayer().spigot().sendMessage(textComponent);
                }
            }
        });



        if (FlopAC.getInstance().getConfigValues().isDiscordWebhook()) {
            FlopAC.getInstance().getDiscordWebhook().addEmbed(
                    new DiscordWebhook.EmbedObject().setDescription(discordAlert));

            try {
                FlopAC.getInstance().getDiscordWebhook().execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (FlopAC.getInstance().getConfigValues().isLagBack()) {
            user.getMovementProcessor().setLagBackTicks((this.lagBack ? 3 : 0));
        }

        int ping = user.getConnectionProcessor().getTransPing();
        boolean banned = user.isBanned();

        if (FlopAC.getInstance().getConfigValues().isLogs()) {
            user.getLogObject().logUtil.addLog(user.getPlayer().getName(),
                    this.checkName, checkType, violation, maxViolation, !canPunish, ping, banned);
        }

        if (user.isBanned()) {
            user.getLogObject().logUtil.addLogString(user.getPlayer().getName()
                    + " has been banned for unfair advantages.");
        }

        if (user.getFlaggedChecks().containsKey(this)) {
            user.getFlaggedChecks().put(this, user.getFlaggedChecks().get(this) + 1);
        } else user.getFlaggedChecks().put(this, 1);

        FlopAC.getInstance().getDatabaseManager().getLogQueue().add(
                new InputData(
                        user.getPlayer().getUniqueId().toString(),
                        user.getPlayer().getName(),
                        this.checkName,
                        this.checkType,
                        user.getFlaggedChecks().getOrDefault(this, 1),
                        false
                )
        );
    }

    @Override
    public void onPacket(PacketEvent event) {
        //
    }

    @Override
    public void setupTimers(User user) {
        //
    }

    @Override
    public void onConnection(User user) {
        //
    }

    public String getFriendlyName() {
        return this.checkName + this.checkType;
    }

    public Check clone() {
        try {
            return (Check) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }


    public void punishPlayer(User user) {
        user.setBanned(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/ban %player% %PREFIX% Unfair Advantage!"
                        .replace("%MAX-VL%", String.valueOf(maxViolation))
                        .replace("%CHECK%", checkName)
                        .replace("%CHECKTYPE%", checkType)
                        .replace("%VL%", String.valueOf(violation))
                        .replace("%player%", user.getPlayer().getName())
                        .replace("%PREFIX%", FlopAC.getInstance().getConfigValues().getPrefix())
                        .replaceFirst("/", ""));

                if (FlopAC.getInstance().getConfigValues().isAnnounce()) {
                    Bukkit.broadcastMessage(FlopAC.getInstance().getConfigValues().getAnnounceMessage()
                            .replace("%MAX-VL%", String.valueOf(maxViolation))
                            .replace("%CHECK%", checkName)
                            .replace("%CHECKTYPE%", checkType)
                            .replace("%VL%", String.valueOf(violation))
                            .replace("%player%", user.getPlayer().getName())
                            .replace("%PREFIX%", FlopAC.getInstance().getConfigValues().getPrefix()));
                }
            }
        }.runTask(FlopAC.getInstance());
    }
}
