package me.jtx.flopac.base.user;

import lombok.Getter;
import lombok.Setter;
import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.impl.CheckManager;
import me.jtx.flopac.base.event.EventManager;
import me.jtx.flopac.base.processor.impl.ProcessorManager;
import me.jtx.flopac.base.processor.impl.processors.*;
import me.jtx.flopac.base.user.objects.BlockData;
import me.jtx.flopac.base.user.objects.LogObject;
import me.jtx.flopac.tinyprotocol.api.TinyProtocolHandler;
import me.jtx.flopac.util.EventTimer;
import me.jtx.flopac.util.PastLocation;
import me.jtx.flopac.util.PlayerLocation;
import me.jtx.flopac.util.TPSUtil;
import me.jtx.flopac.util.box.BoundingBox;
import me.jtx.flopac.util.evicting.EvictingMap;
import me.jtx.flopac.util.math.TrigHandler;
import me.jtx.flopac.FlopAC;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter @Setter
public class User {
    private final Player player;
    private final UUID uuid;
    private final CheckManager checkManager = new CheckManager();
    private final EventManager eventManager;
    private final ExecutorService executorService;
    private final BlockData blockData = new BlockData();

    @Getter @Setter
    private GhostBlockProcessor ghostBlockProcessor;
    private ConnectionProcessor connectionProcessor;
    private PredictionProcessor predictionProcessor;
    private MovementProcessor movementProcessor;
    private ProcessorManager processorManager;
    private ActionProcessor actionProcessor;
    private PotionProcessor potionProcessor;
    private CombatProcessor combatProcessor;
    private ElytraProcessor elytraProcessor;
    private ReachProcessor reachProcessor;

    private TrigHandler trigHandler;

    private Block blockPlaced;

    private final Map<Short, Long> connectionMap = new EvictingMap<>(100);
    private final Map<Long, Long> connectionMap2 = new EvictingMap<>(100);
    private int tick, vehicleTicks;

    private short transactionId;
    private WeakHashMap<Check, Integer> flaggedChecks = new WeakHashMap<>();

    public PastLocation previousLocations = new PastLocation();

    private boolean chunkLoaded = false, devAlerts = false, alerts = true, banned = false;

    private double enderPearlDistance, mouseDeltaY, mouseDeltaX, lastAimHDeltaPitch, lastAimHDeltaYaw;

    private BoundingBox boundingBox = new BoundingBox(0f, 0f, 0f, 0f, 0f, 0f);
    private PlayerLocation currentLocation = new PlayerLocation(null, 0, 0, 0, 0, 0,
            false, System.currentTimeMillis());
    private Location enderPearlThrowLocation;
    private PlayerLocation lastLocation = currentLocation, lastLastLocation = lastLocation;


    private EventTimer lastEnderPearlTimer = new EventTimer(20, this),
            lastFlaggedFlightCTimer = new EventTimer(20, this),
            lastFlightToggleTimer = new EventTimer(20, this),
            lastSuffocationTimer = new EventTimer(20, this),
            lastBlockBreakTimer = new EventTimer(20, this),
            vehicleTimer = new EventTimer(40, this),
            lastExplosionTimer = new EventTimer(40, this),
            lastShotByArrowTimer = new EventTimer(20, this),
            lastAttackByEntityTimer = new EventTimer(20, this),
            lastFireTickTimer = new EventTimer(20, this),
            lastBlockPlaceCancelTimer = new EventTimer(20, this),
            lastBlockPlaceTimer = new EventTimer(20, this),
            lastFallDamageTimer = new EventTimer(20, this),
            lastTeleportTimer = new EventTimer(20, this),
            lastUnknownTeleportTimer = new EventTimer(20, this);

    private LogObject logObject;

    public User(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.checkManager.setupChecks(this);
        this.eventManager = new EventManager(this);
        this.processorManager = new ProcessorManager(this);
        this.processorManager.setup();
        this.setupProcessors();
        this.blockData.setupTimers(this);

        if (FlopAC.getInstance().getLogData() != null && FlopAC.getInstance().getLogObjectList() != null) {
            FlopAC.getInstance().getLogData().addUser(new LogObject(this.uuid.toString()));

            this.logObject = FlopAC.getInstance().getLogData().getUser(this.uuid.toString());
            this.logObject.name = player.getName();
        }


        eventManager.processTime();

        trigHandler = new TrigHandler(this);
    }

    private void setupProcessors() {
        this.connectionProcessor = (ConnectionProcessor) this.processorManager.forClass(ConnectionProcessor.class);
        this.ghostBlockProcessor = (GhostBlockProcessor) this.processorManager.forClass(GhostBlockProcessor.class);
        this.movementProcessor = (MovementProcessor) this.processorManager.forClass(MovementProcessor.class);
        this.predictionProcessor = (PredictionProcessor) this.processorManager.forClass(PredictionProcessor.class);
        this.actionProcessor = (ActionProcessor) this.processorManager.forClass(ActionProcessor.class);
        this.potionProcessor = (PotionProcessor) this.processorManager.forClass(PotionProcessor.class);
        this.combatProcessor = (CombatProcessor) this.processorManager.forClass(CombatProcessor.class);
        this.elytraProcessor = (ElytraProcessor) this.processorManager.forClass(ElytraProcessor.class);
        this.reachProcessor = (ReachProcessor) this.processorManager.forClass(ReachProcessor.class);
    }

    public void sendPacket(Object packet) {
        TinyProtocolHandler.sendPacket(this.player, packet);
    }

    public boolean shouldCancel() {
        return !this.chunkLoaded || TPSUtil.getTPS() <= 19.0 || this.lastFlightToggleTimer.hasNotPassed(20
                + connectionProcessor.getClientTick()) || this.player.getAllowFlight()
                || this.player.isFlying() || this.player.getGameMode() == GameMode.CREATIVE
                || this.player.getGameMode() == GameMode.SPECTATOR;
    }

    public boolean isSword(ItemStack itemStack) {
        return itemStack.getType() == Material.WOOD_SWORD
                || itemStack.getType() == Material.STONE_SWORD
                || itemStack.getType() == Material.GOLD_SWORD
                || itemStack.getType() == Material.IRON_SWORD
                || itemStack.getType() == Material.DIAMOND_SWORD;
    }

    public ItemStack getPlayerHead() {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setOwner(player.getName());
        item.setItemMeta(skull);
        return item;
    }
}
