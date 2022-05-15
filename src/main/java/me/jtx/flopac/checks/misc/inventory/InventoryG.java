package me.jtx.flopac.checks.misc.inventory;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

@CheckInformation(checkName = "Inventory", checkType = "G", lagBack = false, punishmentVL = 3)
public class InventoryG extends Check {

    private int ticks;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.WINDOW_CLICK: {

                if (user.shouldCancel()
                        || user.getBlockData().pistonTicks > 0
                        || user.getBlockData().iceTimer.hasNotPassed(20)
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)
                        || user.getActionProcessor().getVelocityTimer().hasNotPassed(20)
                        || user.getTick() < 60
                        || !user.isChunkLoaded()) {
                    ticks = 0;
                    return;
                }

                Vector vector = user.getMovementProcessor().getInventoryVector();

                if (ticks > 7) {
                    if (user.getMovementProcessor().getDeltaXZ() > MathUtil.getBaseSpeed(user.getPlayer())) {
                        flag(user, "Clicking in inventory while moving");

                        user.getPlayer().closeInventory();

                        user.getMovementProcessor().setInInventory(false);

                        user.getPlayer().teleport(
                                new Location(user.getPlayer().getWorld(),
                                        vector.getX(), vector.getY(), vector.getZ(),
                                        user.getCurrentLocation().getYaw(),
                                        user.getCurrentLocation().getPitch()),
                                PlayerTeleportEvent.TeleportCause.UNKNOWN);

                        user.getGhostBlockProcessor().getGhostBlockTeleportTimer().reset();
                    }
                }

                break;
            }

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                if (user.getMovementProcessor().isInInventory()) {
                    ticks++;
                } else {
                    ticks -= Math.min(ticks, 2);
                }
                break;
            }
        }
    }
}
