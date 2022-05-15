package me.jtx.flopac.base.listener;

import me.jtx.flopac.FlopAC;
import me.jtx.flopac.base.command.commands.sub.GUICommand;
import me.jtx.flopac.base.user.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BukkitListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        this.processEvent(event);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        this.processEvent(event);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        this.processEvent(event);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        this.processEvent(event);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        this.processEvent(event);
    }

    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {
        this.processEvent(event);
    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent event) {
        this.processEvent(event);
    }

    void processEvent(Event event) {
        if (event instanceof InventoryClickEvent) {
            process(event);
        } else {
            FlopAC.getInstance().getExecutorService().execute(() -> this.process(event));
        }
    }

    void process(Event event) {
        if (event instanceof PlayerInteractEvent) {
            PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) event;
            User user = FlopAC.getInstance().getUserManager().getUser(playerInteractEvent.getPlayer());

            if (user != null) {
                if (playerInteractEvent.getItem().getType() == Material.FIREWORK) {
                    user.getElytraProcessor().setFireworkBoost(2.3);
                }
            }
        }

        if (event instanceof PlayerTeleportEvent) {
            User user = FlopAC.getInstance().getUserManager().getUser(((PlayerTeleportEvent) event).getPlayer());

            if (user != null) {
                if (((PlayerTeleportEvent) event).getCause() != PlayerTeleportEvent.TeleportCause.UNKNOWN) {
                    user.getLastTeleportTimer().reset();
                }

                if (((PlayerTeleportEvent) event).getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN) {
                    user.getLastUnknownTeleportTimer().reset();
                }

                if (((PlayerTeleportEvent) event).getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
                    if (user.getEnderPearlThrowLocation() != null
                            && user.getEnderPearlThrowLocation().getWorld().equals(user.getPlayer().getWorld())) {
                        user.setEnderPearlDistance(user.getEnderPearlThrowLocation()
                                .distance(user.getPlayer().getLocation()));
                    }
                    user.getLastEnderPearlTimer().reset();
                }
            }
        }

        if (event instanceof EntityDamageEvent) {
            User user = FlopAC.getInstance().getUserManager().getUser((Player) ((EntityDamageEvent) event).getEntity());

            if (user != null) {
                if (((EntityDamageEvent) event).getCause() == EntityDamageEvent.DamageCause.FALL) {
                    user.getLastFallDamageTimer().reset();
                }

                if (((EntityDamageEvent) event).getCause() == EntityDamageEvent.DamageCause.FIRE
                        || ((EntityDamageEvent) event).getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {

                    user.getLastFireTickTimer().reset();
                }

                if (((EntityDamageEvent) event).getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    user.getLastAttackByEntityTimer().reset();
                }

                if (((EntityDamageEvent) event).getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                    user.getLastShotByArrowTimer().reset();
                }

                if (((EntityDamageEvent) event).getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
                        || ((EntityDamageEvent) event).getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                    user.getLastExplosionTimer().reset();
                }

                if (((EntityDamageEvent) event).getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
                    user.getLastSuffocationTimer().reset();
                }

                if (((EntityDamageByEntityEvent) event).getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    int ticks = user.getCombatProcessor().getCancelTicks();
                    if (((EntityDamageByEntityEvent) event).isCancelled()) {
                        ticks += (ticks < 20 ? 1 : 0);
                    } else {
                        ticks -= (ticks > 0 ? 5 : 0);
                    }
                    user.getCombatProcessor().setCancelTicks(ticks);
                }

            }
        }

        if (event instanceof BlockPlaceEvent) {
            User user = FlopAC.getInstance().getUserManager().getUser(((BlockPlaceEvent) event).getPlayer());

            if (user != null) {
                user.setBlockPlaced(((BlockPlaceEvent) event).getBlockPlaced());

                if (((BlockPlaceEvent) event).getItemInHand().getType().isBlock()) {
                    user.getLastBlockPlaceTimer().reset();

                    if (((BlockPlaceEvent) event).isCancelled()) {
                        user.getLastBlockPlaceCancelTimer().reset();
                    }
                }
            }
        }

        if (event instanceof BlockBreakEvent) {
            User user = FlopAC.getInstance().getUserManager().getUser(((BlockBreakEvent) event).getPlayer());

            if (user != null) {
                user.getLastBlockBreakTimer().reset();
            }
        }

        if (event instanceof InventoryClickEvent) {
            String title = ((InventoryClickEvent) event).getInventory().getTitle();
            String theTitle = "FlopAC";

            if (title.contains(theTitle)) {
                ((InventoryClickEvent) event).setCancelled(true);

                if (((InventoryClickEvent) event).isLeftClick()) {
                    if (((InventoryClickEvent) event).getCurrentItem().getType() == Material.BOOK_AND_QUILL) {
                        User user = FlopAC.getInstance().getUserManager().getUser((Player) ((InventoryClickEvent) event).getWhoClicked());
                        if (user != null) {
                            user.getPlayer().openInventory(GUICommand.settingsGui());
                        }
                    }

                    if (((InventoryClickEvent) event).getCurrentItem().getType() == Material.BOOK) {
                        if (((InventoryClickEvent) event).getCurrentItem().getItemMeta().getDisplayName().contains("Autoban")) {
                            for (String itemLore : ((InventoryClickEvent) event).getCurrentItem().getItemMeta().getLore()) {
                                if (itemLore.contains("✔")) {
                                    FlopAC.getInstance().getConfigValues().setPunish(false);
                                    FlopAC.getInstance().getCheckManager().reloadAnticheat();
                                } else {
                                    FlopAC.getInstance().getConfigValues().setPunish(true);
                                    FlopAC.getInstance().getCheckManager().reloadAnticheat();
                                }
                            }
                        } else if (((InventoryClickEvent) event).getCurrentItem().getItemMeta().getDisplayName().contains("Webhook")) {
                            for (String itemLore : ((InventoryClickEvent) event).getCurrentItem().getItemMeta().getLore()) {
                                if (itemLore.contains("✔")) {
                                    FlopAC.getInstance().getConfigValues().setDiscordWebhook(false);
                                } else {
                                    FlopAC.getInstance().getConfigValues().setDiscordWebhook(true);
                                }
                            }
                        }
                    }
                }
            }

            if (title.contains("Player: ")) {
                ((InventoryClickEvent) event).setCancelled(true);
            }
        }

        if (event instanceof PlayerInteractEvent) {
            User user = FlopAC.getInstance().getUserManager().getUser(((PlayerInteractEvent) event).getPlayer());

            if (user != null) {

                if (((PlayerInteractEvent) event).getAction() == Action.RIGHT_CLICK_AIR
                        || ((PlayerInteractEvent) event).getAction() == Action.RIGHT_CLICK_BLOCK) {

                    if (((PlayerInteractEvent) event)
                            .getPlayer().getItemInHand().getType().equals(Material.ENDER_PEARL)) {
                        user.setEnderPearlThrowLocation(user.getPlayer().getLocation());
                    }
                }
            }
        }
    }
}
