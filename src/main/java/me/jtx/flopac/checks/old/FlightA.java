package me.jtx.flopac.checks.old;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;
import me.jtx.flopac.util.EntityUtil;

@CheckInformation(checkName = "Flight", lagBack = true, description = "Checks if the players predicted y delta")
public class FlightA extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.shouldCancel()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)
                        || user.getLastTeleportTimer().hasNotPassed(20
                        + user.getConnectionProcessor().getClientTick())
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || user.getVehicleTicks() > 0
                        || EntityUtil.isOnBoat(user)
                        || user.getBlockData().webTicks > 0
                        || user.getBlockData().cakeTicks > 0
                        || user.getBlockData().climbableTicks > 0
                        || user.getCombatProcessor().getVelocityTicks() <= 20
                        || checkConditions(user)) {
                    threshold = 0;
                    return;
                }

                double deltaY = user.getMovementProcessor().getDeltaY();

                double lastDeltaY = user.getMovementProcessor().getLastDeltaY();

                double prediction = (lastDeltaY - 0.08D) * 0.9800000190734863D;
            //    Bukkit.broadcastMessage(""+deltaY + " "+lastDeltaY);

                //0.08075199932861322

                double difference = Math.abs(deltaY - prediction);

                if ((user.getLastBlockPlaceTimer().hasNotPassed(20)
                        || user.getLastBlockPlaceCancelTimer().hasNotPassed(20))
                        && (deltaY >= .404f && deltaY <= .405f || lastDeltaY >= .404f && lastDeltaY <= .405F)) {
                    difference = 0.0;
                }

                if (user.getMovementProcessor().getDeltaXZ() <= 0.005 && deltaY < 0 && deltaY > -0.08) {
                    threshold -= Math.min(threshold, 1);
                }

                if (!user.getMovementProcessor().isOnGround()
                        && !user.getMovementProcessor().isLastGround()) {

                    if (difference > 0.005) {
                        if (threshold++ > 3) {
                            flag(user, "Moved the wrong prediction");
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.001f);
                    }
                }

            }
        }
    }
    boolean checkConditions(User user) {
        return user.getBlockData().waterTicks > 0
                || user.getTick() < 60
                || user.shouldCancel();
    }
}
