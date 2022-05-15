package me.jtx.flopac.checks.old;

import me.jtx.flopac.base.check.api.Check;
import me.jtx.flopac.base.check.api.CheckInformation;
import me.jtx.flopac.tinyprotocol.api.Packet;
import me.jtx.flopac.base.event.PacketEvent;
import me.jtx.flopac.base.user.User;

@CheckInformation(checkName = "Speed", punishmentVL = 8, description = "Detecting if the players MotionXZ matched with the predicted calculated speed.")
public class Speed extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.getTick() < 60
                        || user.getVehicleTicks() > 0
                        || user.shouldCancel()
                        || user.getLastTeleportTimer().hasNotPassed(5 + user.getConnectionProcessor().getClientTick())
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || user.getActionProcessor().getRespawnTimer().hasNotPassed(20)
                        || user.getPlayer().isDead()
                        || user.getPlayer().getWalkSpeed() != 0.2F
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(5)
                        || user.getElytraProcessor().isUsingElytra()) {
                    return;
                }

                double motionXZ = user.getPredictionProcessor().getMotionXZ();

                double deltaXZ = user.getMovementProcessor().getDeltaXZ();

                if (motionXZ > 0.005 && deltaXZ > 0.2) {
                    if (threshold++ > 2) {
                        flag(user, "Invalid MotionXZ", ""+motionXZ);
                    }
                } else {
                    threshold -= Math.min(threshold, 0.001);
                }

                break;
            }
        }
    }
}