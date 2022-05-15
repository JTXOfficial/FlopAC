package me.jtx.flopac.checks.movement.flight;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.util.EntityUtil;

@CheckInformation(checkName = "Flight", checkType = "F", description = "Checks for acceleration mid air")
public class FlightF extends Check {

    private int threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                if (user.shouldCancel()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)
                        || user.getLastTeleportTimer().hasNotPassed(3)
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || user.getVehicleTicks() > 0
                        || EntityUtil.isOnBoat(user)
                        || user.getLastBlockPlaceTimer().hasNotPassed(3)
                        || user.getLastBlockPlaceCancelTimer().hasNotPassed(3)
                        || user.getBlockData().snowTicks > 0
                        || user.getBlockData().skullTicks > 0
                        || user.getBlockData().stairSlabTimer.hasNotPassed(20)
                        || user.getBlockData().webTicks > 0
                        || user.getBlockData().cakeTicks > 0
                        || user.getBlockData().door
                        || !user.isChunkLoaded()
                        || user.getActionProcessor().getVelocityTimer().hasNotPassed(10)
                        && user.getLastFallDamageTimer().passed(20)
                        || user.getBlockData().lavaTicks > 0
                        || user.getBlockData().waterTicks > 0
                        || user.getTick() < 60) {
                    return;
                }
            }
            case Packet.Client.FLYING: {
                final int serverAirTicks = user.getMovementProcessor().getServerAirTicks();
                final int clientAirTicks = user.getMovementProcessor().getAirTicks();

                final double deltaY = user.getMovementProcessor().getDeltaY();
                final double lastDeltaY = user.getMovementProcessor().getLastDeltaY();

                final double acceleration = deltaY - lastDeltaY;

                final boolean invalid = acceleration > 0.0 && (serverAirTicks > 8 || clientAirTicks > 8);

                if (invalid) {
                    if (threshold++ > 2) {
                        flag(user, "Going too fast: acc=" + acceleration);
                    }
                } else {
                    threshold = 0;

                }
            }
        }
    }
}
