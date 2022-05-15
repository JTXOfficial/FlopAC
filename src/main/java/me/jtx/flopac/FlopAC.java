package me.jtx.flopac;

import lombok.Getter;
import me.jtx.flopac.banwave.BanWaveManager;
import me.jtx.flopac.base.check.impl.CachedCheckManager;
import me.jtx.flopac.base.command.CommandManager;
import me.jtx.flopac.base.connection.KeepAliveHandler;
import me.jtx.flopac.base.connection.TransactionHandler;
import me.jtx.flopac.base.listener.BukkitListener;
import me.jtx.flopac.base.user.UserManager;
import me.jtx.flopac.base.user.objects.LogData;
import me.jtx.flopac.base.user.objects.LogObject;
import me.jtx.flopac.config.ConfigLoader;
import me.jtx.flopac.config.ConfigValues;
import me.jtx.flopac.database.DatabaseManager;
import me.jtx.flopac.discord.DiscordWebhook;
import me.jtx.flopac.tinyprotocol.api.TinyProtocolHandler;
import me.jtx.flopac.util.FileManager;
import me.jtx.flopac.util.MathUtil;
import me.jtx.flopac.util.TPSUtil;
import me.jtx.flopac.util.box.BlockBoxManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class FlopAC extends JavaPlugin {
    @Getter private static FlopAC instance;

    private UserManager userManager;
    private List<LogObject> logObjectList;
    public LogData logData;

    private CommandManager commandManager;

    private String longLine =
            "-----------------------------------------------------------------------------------------------";

    private DiscordWebhook discordWebhook;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService logService = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService timeService = Executors.newSingleThreadScheduledExecutor();
    private TransactionHandler transactionHandler;
    private KeepAliveHandler keepAliveHandler;
    private TinyProtocolHandler tinyProtocolHandler;
    public String bukkitVersion;
    private final ConfigValues configValues = new ConfigValues();
    private final ConfigLoader configLoader = new ConfigLoader();
    private final CachedCheckManager checkManager = new CachedCheckManager();
    private final DatabaseManager databaseManager = new DatabaseManager();
    private BlockBoxManager blockBoxManager;
    private BanWaveManager banWaveManager;
    private String currentVersion = "null", latestVersion = "null";
    public String currentDate = "(NOT SET)";
    private FileManager fileManager;

    public FlopAC() {
        this.logObjectList = new ArrayList<>();
    }

    @Override
    public void onEnable() {
        instance = this;
        currentVersion = getDescription().getVersion();

        this.tinyProtocolHandler = new TinyProtocolHandler();
        this.checkManager.setup();

        this.configLoader.load();

        this.bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
        this.transactionHandler = new TransactionHandler();
        this.keepAliveHandler = new KeepAliveHandler();
        this.logData = new LogData();
        this.userManager = new UserManager();

        this.commandManager = new CommandManager();
        this.blockBoxManager = new BlockBoxManager();

        new MathUtil();

        getServer().getPluginManager().registerEvents(new BukkitListener(), this);

        getServer().getOnlinePlayers().forEach(player -> TinyProtocolHandler.getInstance().addChannel(player));

        getServer().getOnlinePlayers().forEach(player -> userManager.addUser(player));

        //Resets violations after 1 minute
        this.executorService.scheduleAtFixedRate(() -> this.getUserManager().getUserMap().forEach((uuid, user) ->
                user.getCheckManager().getCheckList().forEach(check -> check.setViolation(0))),
                1L, 3L, TimeUnit.MINUTES);

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new TPSUtil(), 100L, 1L);

        banWaveManager = new BanWaveManager();

        this.fileManager = new FileManager();
        this.databaseManager.setup();

        this.discordWebhook = new DiscordWebhook(configValues.getDiscordWebURL());
    }

    @Override
    public void onDisable() {

        this.databaseManager.shutdown();

        this.userManager.getUserMap().forEach((uuid, user) ->
                TinyProtocolHandler.getInstance().removeChannel(user.getPlayer()));

        this.executorService.shutdownNow();
        this.logService.shutdownNow();
        this.timeService.shutdownNow();

        commandManager.removeCommand();
    }
}
