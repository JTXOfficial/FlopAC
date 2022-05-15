package me.jtx.flopac.checks.old;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;

@CheckInformation(checkName = "Flight", checkType = "G", canPunish = false, description = "Checks if player is using yPort", punishmentVL = 75)
public class FlightG extends Check {

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
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || user.getVehicleTicks() > 0
                        || user.getBlockData().webTicks > 0
                        || user.getBlockData().cakeTicks > 0
                        || user.getCombatProcessor().getVelocityTicks() <= 20
                        || checkConditions(user)) {
                    threshold = 0;
                    return;
                }

                double deltaY = user.getMovementProcessor().getDeltaY();

                double lastDeltaY = user.getMovementProcessor().getLastDeltaY();

                double prediction = (lastDeltaY - 0.08D) * 0.98F;

                double difference = Math.abs(deltaY - prediction);

                if (prediction > 0.005 && deltaY < 0.0) {
                    if (difference > 0.005) {
                        if (threshold++ > 2) {
                            flag(user, "Moved the wrong fall prediction");
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
                || user.getBlockData().underBlockTicks > 0
                || user.getBlockData().stairTicks > 0
                || user.getBlockData().slabTicks > 0
                || user.shouldCancel()
                || user.getBlockData().climbableTicks > 0
                || user.getBlockData().climbableTimer.hasNotPassed();
    }
}
