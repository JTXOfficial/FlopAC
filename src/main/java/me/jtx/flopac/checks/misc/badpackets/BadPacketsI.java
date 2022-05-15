package me.jtx.flopac.checks.misc.badpackets;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.tinyprotocol.packet.out.WrappedOutPositionPacket;
import me.jtx.flopac.util.PlayerLocation;
import org.bukkit.event.player.PlayerTeleportEvent;

@CheckInformation(checkName = "BadPackets", checkType = "I", lagBack = false, canPunish = false, description = "Detection for NoRotate")
public class BadPacketsI extends Check {

    private double posYaw, teleportTicks, ticks;
    private PlayerLocation serverPosLoc;

    /**
     * @Author Rhys
     * Thanks to Rhys for the check/idea on how to make it.
     */

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Server.POSITION: {
                WrappedOutPositionPacket positionPacket =
                        new WrappedOutPositionPacket(event.getPacket(), user.getPlayer());

                teleportTicks = 60;
                ticks = 3;
                posYaw = positionPacket.getYaw();

                serverPosLoc = new PlayerLocation(positionPacket.getX(), positionPacket.getY(),
                        positionPacket.getZ(), System.currentTimeMillis());

                break;
            }
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

               //TODO: account for lag with players moving head.

                if (serverPosLoc != null) {
                    if (ticks-- > 0) {

                        double serverYaw = posYaw;
                        double currentYaw = user.getCurrentLocation().getYaw();

                        double yawDifference = Math.abs(serverYaw - currentYaw);

                        if (yawDifference < 1E-9) {
                            ticks = 0;
                        } else {
                            if (yawDifference > .3) {
                                ticks = 3;

                                if (teleportTicks-- < 1) {
                                    teleportTicks = 20;

                                    user.getPlayer().teleport(serverPosLoc
                                                    .toBukkitLocation(user.getPlayer().getWorld()),
                                            PlayerTeleportEvent.TeleportCause.PLUGIN);

                                    flag(user, "Possibly using NoRotate");
                                }
                            }
                        }
                    }
                }
                break;
            }
        }
    }
}