package me.jtx.flopac.base.processor.impl.processors;

import lombok.Getter;
import lombok.Setter;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.processor.api.Processor;
import me.jtx.flopac.base.processor.api.ProcessorInformation;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.util.EntityUtil;
import me.jtx.flopac.util.EventTimer;
import me.jtx.flopac.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;

@ProcessorInformation(name = "GhostBlock")
@Getter
@Setter
public class GhostBlockProcessor extends Processor {
    private EventTimer ghostBlockTeleportTimer;
    private double flags;


    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.shouldCancel()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)
                        || user.getLastTeleportTimer().hasNotPassed(10 + user.getConnectionProcessor().getClientTick())
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || user.getLastBlockPlaceTimer().hasNotPassed(3 + user.getConnectionProcessor().getClientTick())
                        || user.getLastBlockPlaceCancelTimer().hasNotPassed(20)
                        || user.getLastFallDamageTimer().hasNotPassed(10 + user.getConnectionProcessor().getClientTick())
                        || user.getVehicleTicks() > 0
                        || EntityUtil.isOnBoat(user)
                        || user.getBlockData().webTicks > 0
                        || user.getBlockData().cakeTicks > 0
                        || user.getBlockData().climbableTicks > 0
                        || user.getCombatProcessor().getVelocityTicks() <= 9
                        + user.getConnectionProcessor().getClientTick()
                        || user.getBlockData().waterTicks > 0
                        || user.getBlockData().lavaTicks > 0
                        || user.getBlockData().lillyPad
                        || user.getBlockData().carpet
                        || user.getBlockData().snow
                        || user.getBlockData().skull
                        || user.getTick() < 60) {
                    flags = 0;
                    return;
                }


                boolean ground = user.getMovementProcessor().isOnGround() || user.getMovementProcessor().isLastGround();

                boolean serverPositionGround = user.getMovementProcessor().isServerYGround()
                        || user.getMovementProcessor().isLastPositionYGround();

                boolean serverGround = user.getBlockData().onGround || user.getBlockData().lastOnGround;

                if (ground && serverPositionGround
                        && !serverGround) {

                    Location lastGroundLocation = user.getMovementProcessor().getLastGroundLocation();

                    Location groundBelow = MathUtil.getGroundLocation(user);

                    if (++flags > 1) {
                        if (lastGroundLocation != null) {

                            user.getPlayer().teleport(lastGroundLocation,
                                    PlayerTeleportEvent.TeleportCause.UNKNOWN);
                        } else {
                            user.getPlayer().teleport(groundBelow,
                                    PlayerTeleportEvent.TeleportCause.UNKNOWN);
                        }

                        ghostBlockTeleportTimer.reset();
                        flags = 0;
                    }
                }

            }

            break;
        }
    }

    @Override
    public void setupTimers(User user) {
        ghostBlockTeleportTimer = new EventTimer(20, user);
    }
}